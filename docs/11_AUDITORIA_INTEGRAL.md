# SOFP — Auditoría integral del estado técnico

## Estado — 15/09/2026

Esta auditoría reconstruye el estado de `feature/swing-shell` desde GitHub. La fuente de verdad es el código y los tests; la documentación se utiliza para contrastar continuidad y detectar información obsoleta.

## 1. Estado de Git y continuidad

- Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
- Rama de trabajo: `feature/swing-shell`.
- La rama de trabajo continúa separada de `main`; no se realizó merge.
- Último commit de código: `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.
- La documentación fue actualizada posteriormente con el resultado general 718/718.

## 2. Validación actual

- `PagoTarjetaServiceTest`: **10/10**, 0 failures, 0 errors, 0 skipped.
- Validación relacionada anterior: **19/19**, 0 failures, 0 errors, 0 skipped.
- `mvn test`: **718/718**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 20:05:19 -03:00**.
- Tiempo total: **09:04 min**.

La auditoría registra resultados informados por el usuario; no vuelve a ejecutar tests.

## 3. Arquitectura actual

La estructura real contiene dominio, persistencia, servicios y Swing. La separación general observada es:

`UI → servicios → dominio/repositorios → JPA/H2`.

Los servicios coordinan operaciones transaccionales y autorización; las entidades conservan reglas propias del dominio.

No se observa una necesidad de rehacer la arquitectura para el próximo bloque.

## 4. Dominio financiero

El núcleo actual incluye `Usuario`, `PerfilFinanciero`, `Moneda`, `Cuenta`, `Movimiento`, categorías, operaciones financieras, posiciones de activos, obligaciones y cuotas.

La integridad histórica de cuentas y de movimientos origen de obligaciones está protegida por servicio y cubierta por tests.

## 5. Moneda y multidivisa

`Movimiento` conserva una moneda económica explícita y puede diferir de la moneda de la cuenta. La solución actual corrige:

1. `CuentaService` calcula el saldo de una cuenta usando la moneda de la cuenta.
2. `MovimientoService` valida disponibilidad de fondos usando la moneda del movimiento.
3. `Obligacion` separa moneda original y moneda de liquidación.
4. `TipoCambio` permite una liquidación histórica explícita y trazable.
5. `saldoLiquidacion` representa la deuda en moneda de liquidación.
6. `PagoTarjetaService` aplica pagos a ese saldo cuando corresponde.

No se deben introducir conversiones implícitas. La liquidación conserva la cotización histórica utilizada.

## 6. Pagos de tarjeta

`PagoTarjetaService` coordina en una transacción la autorización, cuenta pagadora, categoría, moneda, saldo, fondos, fecha y registro del egreso/pago.

Para obligaciones liquidadas exige que la cuenta pagadora coincida con `monedaLiquidacion` y aplica el pago mediante `registrarPagoLiquidacion`. Para obligaciones no liquidadas conserva el flujo de `saldoPendiente`/`registrarPago`.

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

`Moneda.cantidadDecimales` valida:

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
- definir y cubrir la regla con tests antes de modificar el cálculo;
- completar cobertura de persistencia/UI del pago multidivisa.

La moneda de liquidación, tasa, fecha, fuente y trazabilidad de la conversión histórica ya están modeladas para la liquidación explícita; no corresponde rehacer ese modelo sin una nueva necesidad de negocio.

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

La auditoría actual confirma que el núcleo está consolidado y que los problemas de mezcla de monedas en saldo y disponibilidad de fondos fueron corregidos. La liquidación histórica multidivisa y el pago en moneda de liquidación ya están implementados y la suite completa actual es 718/718.

El principal hueco funcional real es ahora definir cómo un consumo de tarjeta en una moneda distinta afecta el límite/crédito disponible.

El siguiente bloque debe definir primero esa regla de negocio y luego implementar el cambio mínimo con cobertura.
