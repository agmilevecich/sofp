# SOFP — Auditoría integral del estado técnico

## Estado — 15/09/2026

Esta auditoría reconstruye el estado de `feature/swing-shell` desde GitHub. La fuente de verdad es el código y los tests; la documentación se utiliza para contrastar continuidad y detectar información obsoleta.

## 1. Estado de Git y continuidad

- Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
- Rama de trabajo: `feature/swing-shell`.
- El trabajo de continuidad permanece separado de `main`.
- El último bloque funcional de esta etapa es la validación de `Moneda.cantidadDecimales`.
- La suite general actual informada por el usuario es 693/693.

## 2. Validación actual

- `mvn -Dtest=MonedaTest test`: **7/7**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 15/09/2026 11:21:20 -03:00.
- `mvn -Dtest=MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest test`: **53/53**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 15/09/2026 11:34:38 -03:00.
- `mvn test`: **693/693**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 15/09/2026 12:03:19 -03:00.

La auditoría no vuelve a ejecutar tests; registra resultados informados por el usuario.

## 3. Arquitectura actual

La estructura real contiene dominio, persistencia, servicios y Swing. La UI está integrada con servicios; la separación general observada es:

`UI → servicios → dominio/repositorios → JPA/H2`.

Los servicios coordinan operaciones transaccionales y autorización; las entidades conservan reglas propias del dominio.

No se observa una necesidad de rehacer la arquitectura para el próximo bloque.

## 4. Dominio financiero

El núcleo actual incluye `Usuario`, `PerfilFinanciero`, `Moneda`, `Cuenta`, `Movimiento`, categorías, operaciones financieras, posiciones de activos, obligaciones y cuotas.

La integridad histórica de cuentas y de movimientos origen de obligaciones está protegida por servicio y cubierta por tests.

## 5. Moneda y multidivisa

`Movimiento` conserva una moneda económica explícita y puede diferir de la moneda de la cuenta. La solución actual ya corrige dos problemas identificados en la auditoría anterior:

1. `CuentaService` calcula el saldo de una cuenta usando la moneda de la cuenta.
2. `MovimientoService` valida disponibilidad de fondos usando la moneda del movimiento.

Por lo tanto, los cálculos generales ya no deben sumar ARS y USD como una única magnitud.

Continúa abierto el tercer problema: un consumo de tarjeta en moneda distinta de la moneda de la tarjeta todavía no tiene una regla completa de impacto sobre el límite. Tampoco existe una operación explícita de liquidación/conversión para pagos entre monedas.

No se deben introducir conversiones implícitas. Antes deben definirse moneda de liquidación, tasa, fecha/fuente de cotización y trazabilidad de la operación resultante.

## 6. Pagos de tarjeta

`PagoTarjetaService` coordina en una transacción la autorización, cuenta pagadora, categoría, moneda, saldo pendiente, fondos, fecha y registro del egreso/pago.

La regla actual exige coincidencia de moneda entre la obligación y la cuenta pagadora. Esto evita conversiones implícitas, pero deja pendiente la liquidación multidivisa.

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

No están implementados feriados, fecha efectiva separada ni intereses/punitorios/CFT/refinanciación.

## 8. Integridad histórica

Una cuenta con movimientos no puede cambiar de tipo ni moneda. La API genérica tampoco permite convertir una cuenta existente hacia/desde `TARJETA_CREDITO`.

El movimiento origen de una obligación queda protegido frente a modificaciones estructurales incompatibles y eliminación.

Sigue pendiente definir una política explícita para eliminar cuentas que ya poseen historial financiero.

## 9. Robustez de Moneda

`Moneda.cantidadDecimales` ahora valida:

- `null` → `NullPointerException`;
- valor negativo → `IllegalArgumentException`;
- valor no negativo → permitido.

No se agregó un límite superior arbitrario.

## 10. Autorización y aislamiento

La autorización por `usuarioId` y el aislamiento por perfil/usuario permanecen integrados en las operaciones financieras sensibles. No se identificó un nuevo bypass en el trabajo actual.

## 11. Persistencia

La aplicación utiliza JPA/Hibernate con H2 TCP y `hibernate.hbm2ddl.auto=update`. Los tests utilizan un contexto separado con H2 en memoria.

`update` sigue siendo adecuado para desarrollo actual, pero no constituye un mecanismo formal de migraciones/versionado para una futura etapa de distribución.

## 12. UI Swing

La UI contiene shell y paneles para cuentas, categorías, ingresos, gastos, movimientos, inversiones y obligaciones. El pago de tarjeta desde `ObligacionesPanel` está integrado y cubierto por tests.

Pendiente: UI específica de tarjetas para límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos.

## 13. Financiación

Las cuotas simples sin interés están implementadas. Siguen fuera del alcance actual intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 14. Determinismo temporal

`PagoTarjetaService` utiliza `LocalDateTime.now()` para rechazar fechas futuras. La regla es correcta, pero una futura abstracción `Clock` permitiría tests más deterministas.

## 15. Clasificación actual

### P0/P1 — multidivisa de tarjetas

- definir impacto de consumos en moneda distinta sobre crédito disponible;
- definir moneda de liquidación;
- definir tasa, fecha y fuente de cotización;
- definir trazabilidad y representación de la conversión/liquidación;
- cubrir el comportamiento con tests específicos y relacionados.

### P2 — robustez

- política de eliminación de cuentas con historial;
- `Clock`;
- migraciones/versionado formal de esquema.

### P3 — evolución

- financiación avanzada;
- UI específica de tarjetas;
- pasivos/patrimonio/análisis;
- gestión de entidades financieras;
- pulido de consola.

## 16. Conclusión

La auditoría actual confirma que el núcleo está consolidado y que los problemas de mezcla de monedas en saldo y disponibilidad de fondos ya fueron corregidos. El principal hueco funcional real es ahora la multidivisa específica de tarjetas: crédito disponible y liquidación cuando las monedas difieren.

El siguiente bloque debe definir primero esas reglas de negocio y luego implementar el cambio mínimo con cobertura.
