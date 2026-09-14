# SOFP — Auditoría integral del estado técnico

## Estado — 14/09/2026

Esta auditoría reconstruye el estado de `feature/swing-shell` desde GitHub. La fuente de verdad es el código y los tests; la documentación se utilizó para contrastar continuidad y detectar información obsoleta.

## 1. Estado de Git

- Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
- Rama de trabajo: `feature/swing-shell`.
- HEAD actual de la rama de trabajo: `b1c46ebc986f12ee880d86d03027aa0c66fe67e2`.
- Último cambio funcional/test: `3a001a57c435237e62ab04f6c09a0657ff24fcb2`.
- Los commits posteriores al último cambio funcional son documentales.
- `main` no fue modificado en esta auditoría.

## 2. Validación conocida

El último resultado informado por el usuario es:

- `mvn test`: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 13/09/2026 22:05:14 -03:00.
- `mvn -Dtest=ObligacionJpaTest test`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 13/09/2026 21:12:49 -03:00.

Estos resultados son históricos conocidos; esta auditoría no los vuelve a ejecutar.

## 3. Arquitectura actual

La estructura real contiene dominio, persistencia, servicios y Swing. La UI ya existe y está integrada con servicios; por lo tanto, la documentación antigua que indicaba que Swing todavía no había comenzado quedó obsoleta.

El flujo general observado mantiene la separación:

`UI → servicios → dominio/repositorios → JPA/H2`.

Los servicios coordinan operaciones transaccionales y autorización; las entidades conservan reglas propias del dominio.

## 4. Dominio financiero

### Cerrado y coherente

- `Usuario` y `PerfilFinanciero`.
- `Moneda`.
- `Cuenta` y tipos de cuenta.
- `Movimiento` como núcleo monetario.
- `Categoria`.
- `OperacionFinanciera` y transferencias.
- activos, posiciones y valorizaciones.
- obligaciones de tarjeta y cuotas.
- auditoría de integridad de cuenta.
- auditoría de integridad movimiento → obligación.

### Hallazgo de calidad

`Moneda.cambiarCantidadDecimales` valida nulidad pero no valida rango. El código actual permite valores negativos. No se observó un test que cubra ese límite. Se registra como P2 de robustez, no como bloqueo del modelo actual.

## 5. Moneda y multidivisa — hallazgo principal

El modelo permite que `Movimiento` tenga una moneda económica explícita distinta de la moneda configurada en `Cuenta`. Esto existe expresamente para compras en moneda extranjera.

La implementación actual conserva esa moneda en la obligación y evita conversiones implícitas. Esa parte es correcta como decisión de seguridad financiera.

Sin embargo, la auditoría detectó tres huecos concretos:

1. `CuentaService.calcularSaldo` suma ingresos y egresos por cuenta sin filtrar por moneda. Por lo tanto, si una cuenta contiene movimientos en más de una moneda, el saldo actual mezcla importes incompatibles.
2. `MovimientoService.calcularSaldo` utiliza el mismo criterio y también mezcla monedas al validar fondos de una cuenta.
3. Para una tarjeta, `validarCreditoDisponible` solo aplica el límite cuando la moneda del consumo coincide con la moneda de la tarjeta. Un consumo extranjero no reduce el límite de la tarjeta bajo una regla equivalente ni existe conversión/tasa de cambio para resolverlo.

Conclusión: **la multidivisa no está cerrada funcionalmente**. El sistema conserva la moneda del movimiento, pero todavía no existe un modelo completo para saldos, crédito disponible y liquidación cuando las monedas difieren.

No se debe introducir una conversión automática sin una decisión explícita sobre tasa, fecha de cotización, fuente y moneda de liquidación.

## 6. Pagos de tarjeta

`PagoTarjetaService` está correctamente coordinado en una transacción y valida propietario, cuenta, categoría, moneda, saldo pendiente, fondos y fecha.

La regla actual exige que la moneda de la cuenta pagadora coincida con la moneda de la obligación. Esto evita conversiones implícitas, pero también confirma que el pago de una obligación en moneda extranjera todavía no dispone de un mecanismo de conversión o liquidación multidivisa.

Además, el cálculo de fondos del servicio recorre los movimientos de la cuenta sin separar por moneda. Esto debe corregirse junto con el diseño multidivisa para no evaluar fondos con importes de monedas diferentes.

## 7. Tarjetas, ciclos y temporalidad

El bloque temporal está cerrado:

- ciclo histórico persistido en `Obligacion`;
- fechas históricas de `Cuota` persistidas;
- vencimiento de sábado/domingo desplazado al lunes;
- días de gracia;
- mora basada en vencimiento efectivo más gracia;
- rechazo de pagos anteriores al consumo;
- rechazo de pagos futuros;
- pagos parciales en orden de cuotas;
- compatibilidad de registros antiguos mediante nullable/fallback.

Pendientes temporales explícitos: feriados, fecha efectiva separada e intereses/punitorios/CFT/refinanciación.

## 8. Integridad histórica

La integridad de `Cuenta` está protegida frente a cambios de tipo y moneda cuando existen movimientos. La API genérica tampoco permite convertir una cuenta existente hacia/desde `TARJETA_CREDITO`.

El movimiento origen de una obligación queda protegido frente a modificaciones estructurales incompatibles y eliminación.

### Hallazgo de cobertura

La política de eliminación de una `Cuenta` con historial financiero no está definida como regla de dominio explícita en el servicio; el test revisado cubre eliminación de una cuenta sin historial. Se registra como P2 de integridad histórica antes de habilitar borrados destructivos en UI.

## 9. Autorización y aislamiento

La auditoría actual conserva autorización por `usuarioId` en las operaciones financieras sensibles y aislamiento por perfil/usuario en las lecturas y mutaciones relevantes. La cobertura histórica de seguridad y aislamiento está integrada y la suite general permanece verde.

No se detectó en esta revisión un nuevo bypass equivalente al que motivó las auditorías anteriores.

## 10. Persistencia

La aplicación utiliza JPA/Hibernate con H2 TCP y `hibernate.hbm2ddl.auto=update`. Los tests utilizan un contexto separado con H2 en memoria.

El uso de `update` es apropiado para la etapa de desarrollo actual, pero no constituye un mecanismo formal de migraciones/versionado de esquema. Queda registrado como deuda técnica para una etapa posterior de distribución/producción.

## 11. UI Swing

La UI ya contiene shell y paneles para cuentas, categorías, ingresos, gastos, movimientos, inversiones y obligaciones. La integración de pagos de tarjeta desde `ObligacionesPanel` está implementada y cubierta por tests.

Pendiente funcional: una UI específica de tarjeta que concentre límite, disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos.

## 12. Financiación

Las cuotas simples sin interés están implementadas. No se observó implementación de:

- intereses;
- CFT;
- cuotas variables;
- adelantos;
- refinanciación;
- anulaciones/reversiones;
- ajustes financieros.

Esto queda correctamente como bloque separado y no debe mezclarse con la implementación multidivisa.

## 13. Determinismo temporal

`PagoTarjetaService` utiliza directamente `LocalDateTime.now()` para rechazar fechas futuras. La regla es correcta, pero la dependencia directa del reloj dificulta tests completamente deterministas. Se registra como P2 técnico para una futura abstracción `Clock`.

## 14. Documentación

La auditoría detectó documentación arquitectónica obsoleta: `docs/00-arquitectura/roadmap.md` todavía indicaba que Swing no había comenzado y conservaba el conteo histórico 512/512 como validación global vigente.

Ese estado ya no representa el código actual. El roadmap se actualiza junto con esta auditoría para que no contradiga el estado real.

## 15. Configuración real verificada

El `pom.xml` actual es la referencia para la configuración de build: compilación Java 23, Hibernate 6.6.4.Final, H2 2.5.250 y JUnit 5.11.4. Esta información prevalece sobre cualquier documentación histórica que indique otras versiones.

## 16. Clasificación final

### P0 — bloqueante antes de ampliar multidivisa

- Definir y corregir el tratamiento de saldos por moneda.
- Definir y corregir el cálculo de crédito disponible para consumos en moneda distinta de la moneda de la tarjeta.
- Definir la moneda de liquidación y la regla de conversión cuando corresponda.

### P1 — siguiente bloque funcional

- Implementación completa de multidivisa de tarjetas, sin conversiones implícitas.
- Tests de saldos, crédito, consumos y pagos cruzando monedas.

### P2 — robustez

- Validación del rango de `cantidadDecimales` en `Moneda`.
- Política explícita de eliminación de cuentas con historial.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema si el proyecto pasa de desarrollo local.

### P3 — evolución

- Financiación avanzada.
- UI específica de tarjetas.
- Pasivos/patrimonio/análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## 17. Conclusión

La auditoría no encuentra un problema general de arquitectura que obligue a rehacer SOFP. El núcleo actual está suficientemente consolidado y la suite conocida está en 704/704.

El principal hueco funcional real es la **multidivisa**, pero el problema no es simplemente “agregar conversiones”: primero hay que impedir que saldos y límites mezclen monedas y definir explícitamente cómo se liquida una deuda en moneda distinta.

No se modificó código funcional durante esta auditoría. Los cambios de esta etapa son documentales.
