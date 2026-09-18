# SOFP — Auditoría integral del estado técnico

## Estado — 17/09/2026

Esta auditoría reconstruye el estado de `feature/swing-shell` desde GitHub. La fuente de verdad es el código y los tests; la documentación se utiliza para contrastar continuidad y detectar información obsoleta.

## 1. Estado de Git y continuidad

- Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
- Rama de trabajo: `feature/swing-shell` → último commit `2a789c10e5399afc799d9f2cc5477e74ef799413`.
- La rama de trabajo continúa separada de `main`; no se realizó merge.
- Últimos commits funcionales del bloque: `c592cbc` (`fix: calcular credito sobre saldo de liquidacion`), `20bb282` (`test: cubrir credito liberado tras liquidacion multidivisa`) y `2a789c1` (`test: persistir tipo de cambio de liquidacion`).

## 2. Validación actual

- `CuentaServiceCreditoTest`: **3/3**, 0 failures, 0 errors, 0 skipped.
- `TarjetaCreditoPagoCreditoTest`: **5/5**, 0 failures, 0 errors, 0 skipped.
- `mvn test`: **756/756**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Suite finalizada: **17/09/2026 15:50:11 -03:00**.

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

`Movimiento` conserva una moneda económica explícita y puede diferir de la moneda de la cuenta. La solución actual mantiene:

1. `CuentaService` calcula el saldo de una cuenta usando la moneda de la cuenta.
2. `MovimientoService` valida disponibilidad de fondos usando la moneda del movimiento.
3. `Obligacion` separa moneda original y moneda de liquidación.
4. `TipoCambio` permite valorización de cierre y liquidación histórica explícita y trazable.
5. `saldoLiquidacion` representa la deuda en moneda de liquidación.
6. `PagoTarjetaService` aplica pagos a ese saldo cuando corresponde.
7. El crédito utilizado distingue entre saldo pendiente antes de liquidar y saldo de liquidación después de liquidar.

No se deben introducir conversiones implícitas. La liquidación conserva la cotización histórica utilizada.

## 6. Pagos de tarjeta

`PagoTarjetaService` coordina en una transacción la autorización, cuenta pagadora, categoría, moneda, saldo, fondos, fecha y registro del egreso/pago.

Para obligaciones liquidadas exige que la cuenta pagadora coincida con `monedaLiquidacion` y aplica el pago mediante `registrarPagoLiquidacion`. Para obligaciones no liquidadas conserva el flujo de `saldoPendiente`/`registrarPago`.

## 7. Crédito de tarjeta

El cálculo de crédito utilizado contempla la etapa real de la obligación:

- obligación en moneda de la tarjeta antes de liquidar → `saldoPendiente`;
- obligación multidivisa antes de liquidar → valorización de cierre proporcional al saldo original pendiente, cuando existe;
- obligación después de liquidar → `saldoLiquidacion`;
- consumo sin obligación asociada → comportamiento existente.

Esto evita doble contabilización del saldo original trasladado a liquidación y permite liberar completamente el crédito cuando la deuda de liquidación queda cancelada.

## 8. Tarjetas, ciclos y temporalidad

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

## 9. Integridad histórica

Una cuenta con movimientos no puede cambiar de tipo ni moneda. La API genérica tampoco permite convertir una cuenta existente hacia/desde `TARJETA_CREDITO`.

El movimiento origen de una obligación queda protegido frente a modificaciones estructurales incompatibles y eliminación.

Sigue pendiente definir una política explícita para eliminar cuentas que ya poseen historial financiero.

## 10. Robustez de Moneda

`Moneda.cantidadDecimales` valida:

- `null` → `NullPointerException`;
- valor negativo → `IllegalArgumentException`;
- valor no negativo → permitido.

No se agregó un límite superior arbitrario.

## 11. Autorización y aislamiento

La autorización por `usuarioId` y el aislamiento por perfil/usuario permanecen integrados en las operaciones financieras sensibles. No se identificó un nuevo bypass en el trabajo actual.

## 12. Persistencia

La aplicación utiliza JPA/Hibernate con H2 TCP y `hibernate.hbm2ddl.auto=update`. Los tests utilizan un contexto separado con H2 en memoria.

`update` sigue siendo adecuado para desarrollo actual, pero no constituye un mecanismo formal de migraciones/versionado para una futura etapa de distribución.

## 13. UI Swing

La UI contiene shell y paneles para cuentas, categorías, ingresos, gastos, movimientos, inversiones y obligaciones. El cierre de ciclo desde `ObligacionesPanel` y el pago de tarjeta están integrados y cubiertos por tests.

Pendiente: UI específica de tarjetas para límite/disponible, consumos, valorizaciones de cierre, ciclos, cierres, vencimientos, deuda y pagos.

## 14. Financiación

Las cuotas simples sin interés están implementadas. Siguen fuera del alcance actual intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

## 15. Determinismo temporal

`PagoTarjetaService` utiliza `LocalDateTime.now()` para rechazar fechas futuras. La regla es correcta, pero una futura abstracción `Clock` permitiría tests más deterministas.

## 16. Clasificación actual

### Bloque actual — cierre y crédito multidivisa

El cálculo de crédito después de liquidación ya está implementado y validado. El siguiente trabajo es de definición de negocio y flujo de cierre de resumen, no una corrección pendiente del cálculo ya probado.

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

## 17. Próximo paso

Revisar `ObligacionService` y reconstruir el flujo de cierre de resumen de tarjeta. Antes de cambiar código, contrastar el comportamiento buscado con normativa BCRA y documentación vigente de la entidad financiera de referencia, especialmente respecto de la cotización aplicada a consumos extranjeros al cierre y la relación entre valorización, liquidación y pago.

No fijar una nueva regla de negocio por inferencia: primero documentar la regla y luego cubrirla con tests antes de implementarla.

## 18. Conclusión

La auditoría actual confirma que el núcleo multidivisa está consolidado: moneda original y de liquidación separadas, cotizaciones históricas trazables, valorización de cierre independiente, liquidación explícita, pagos en la moneda correspondiente y cálculo de crédito coherente antes y después de liquidar.

La suite completa conocida y validada es **756/756**, con 0 failures, 0 errors y 0 skipped. El próximo bloque es definir y modelar correctamente el comportamiento de cierre de resumen de tarjeta, comenzando por `ObligacionService`.


## Actualización de continuidad — cierre 17/09/2026 22:51 -03:00

Esta sección supersede cualquier validación anterior de este documento cuando haya contradicción.

- Rama de trabajo: `feature/swing-shell`.
- HEAD actual: `b4a9bc6b4f64ce191f90eb9e4dd4291356e0574c` — `test: corregir expectativas de valorizacion multidivisa`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- Comparación GitHub: `feature/swing-shell` está 847 commits por delante de `main` y 0 por detrás.
- No se realizó merge a `main`.
- Último bloque: corrección de expectativas de tests para reflejar que la valorización de cierre de obligaciones financiadas se almacena en la cuota; no se modificó producción en este último commit.
- Validación específica posterior: 8/8 tests verdes, 0 failures, 0 errors, `BUILD SUCCESS`, informada por el usuario.
- Validación final: `mvn test` con **769/769 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 22:51:16 -03:00**, informada por el usuario.
- Validación Git local final: `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio; rama local al día con `bitbucket/feature/swing-shell`, informado por el usuario.

### Punto exacto para retomar

El cálculo de crédito multidivisa, la valorización histórica de cierre, la liquidación explícita, los pagos antes/después de liquidar y el cierre iniciado desde `ObligacionesPanel` están cubiertos por tests. El siguiente bloque debe comenzar con una revisión de `ObligacionService` y de sus clases relacionadas para definir el flujo de cierre de resumen de tarjeta siguiendo reglas bancarias reales. Antes de modificar código se debe contrastar la cotización de cierre de consumos extranjeros con normativa BCRA y documentación vigente de la entidad financiera de referencia. No inventar una regla de negocio por inferencia.

### Regla de continuidad para la próxima sesión

Reconstruir desde GitHub antes de cualquier cambio: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. No asumir que la documentación histórica representa el estado actual si contradice código o tests.