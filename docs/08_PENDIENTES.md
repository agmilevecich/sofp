# SOFP — Pendientes

## Estado auditado — 17/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `9459357bfbd5665a3f6fd42405c901c70e71b23e`.

La rama de trabajo está 816 commits por delante de `main` y 0 por detrás. No se realizó merge a `main`.

Última validación informada: **761/761**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, finalizada **17/09/2026 17:54:49 -03:00**.

## Bloques cerrados

- Shell Swing y navegación.
- Cuentas, categorías, ingresos, gastos, movimientos e inversiones.
- Obligaciones y pagos de tarjeta desde UI.
- Autorización de pagos por usuario.
- Integridad estructural e histórica.
- Ciclos, cuotas, vencimientos, gracia y mora.
- Aislamiento JPA/H2 para tests.
- Saldos y fondos separados por moneda.
- `Moneda.cantidadDecimales` no negativa.
- `TipoCambio` histórico.
- Registro de cotizaciones históricas desde la aplicación.
- Persistencia transaccional del registro de cotizaciones.
- Formulario Swing para registrar cotización histórica.
- Moneda original y moneda de liquidación de `Obligacion`.
- Liquidación histórica explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- Valorización histórica de cierre separada de la liquidación.
- Obtención de la cotización histórica necesaria al cerrar el ciclo.
- Uso de la valorización para crédito disponible.
- Corrección proporcional del crédito después de pagos parciales.
- Cierre de ciclo iniciado desde `ObligacionesPanel`.
- Pago multidivisa en moneda original antes de la liquidación.
- Rechazo de pago en moneda de liquidación antes de liquidar una obligación multidivisa.
- Liquidación multidivisa sobre el saldo original restante después de pagos parciales.
- Flujo integral de pago parcial en moneda original → saldo original restante → liquidación → pago posterior en moneda de liquidación.
- Semántica de `estado` después de una liquidación parcial: `saldoPendiente` puede conservar el saldo original ya trasladado a `saldoLiquidacion`; el pago de la liquidación lleva el estado a `PAGADA` sin descontar nuevamente ese saldo original.
- Cálculo de crédito utilizado sobre `saldoLiquidacion` después de liquidar.
- Liberación completa del crédito después de cancelar la deuda de liquidación.
- Validación de regresión completa con 761 tests.

## Decisiones multidivisa vigentes

- `Obligacion.liquidar()` convierte únicamente el `saldoPendiente` original que permanece pendiente al momento de liquidar.
- No se introducen conversiones implícitas.
- La cotización utilizada para la liquidación debe ser histórica, explícita y trazable.
- Antes de la liquidación, una obligación multidivisa se paga en su moneda original.
- Después de la liquidación, el saldo a pagar queda expresado en la moneda de liquidación mediante `saldoLiquidacion`.
- El crédito utilizado antes de liquidar usa el saldo original y la valorización de cierre proporcional cuando corresponde.
- El crédito utilizado después de liquidar usa `saldoLiquidacion`, evitando que el saldo original trasladado siga consumiendo crédito una vez cancelada la liquidación.
- `estado` representa la deuda que permanece exigible; por eso el pago de la liquidación puede llevar el estado a `PAGADA` aunque `saldoPendiente` conserve el importe original trasladado.

## Próximo paso lógico

Revisar `ObligacionService` y definir el comportamiento de cierre de resumen de tarjeta siguiendo el flujo de una entidad financiera: fecha de cierre, obtención y persistencia de la cotización histórica, valorización de consumos extranjeros, tratamiento de obligaciones sin cotización disponible y relación entre cierre, liquidación y pago.

La regla de negocio debe contrastarse con normativa BCRA y documentación vigente de la entidad tomada como referencia antes de modificar el modelo.

## P2 — Robustez

- Política de eliminación de cuentas con historial financiero.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

- Financiación avanzada.
- UI específica de tarjetas.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## Estabilización futura — previa al fast-forward a main

Separada del desarrollo funcional actual:

- iniciar H2 automáticamente desde Java al arrancar SOFP;
- detener H2 limpiamente al cerrar;
- ocultar la salida técnica de consola;
- conservar detalle técnico mediante logging a archivo;
- informar fallos de conexión con la base y otros errores de arranque mediante `JOptionPane`;
- evitar mostrar una ventana parcialmente inicializada si el arranque falla.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
