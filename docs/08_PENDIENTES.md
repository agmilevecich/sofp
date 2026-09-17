# SOFP — Pendientes

## Estado auditado — 17/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `bdfe6855ac0a37a85c76fb55f7adb801223b0795`.

Última validación informada: **755/755**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, finalizada **17/09/2026 14:43:54 -03:00**.

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

## Decisiones multidivisa vigentes

- `Obligacion.liquidar()` debe convertir únicamente el `saldoPendiente` original que permanece pendiente al momento de liquidar; no debe volver a convertir `importeOriginal` después de un pago parcial.
- No se introducen conversiones implícitas.
- La cotización utilizada para la liquidación debe ser histórica, explícita y trazable.
- Antes de la liquidación, una obligación multidivisa se paga en su moneda original.
- Después de la liquidación, el saldo a pagar queda expresado en la moneda de liquidación mediante `saldoLiquidacion`.
- `estado` representa el estado de la deuda que permanece exigible. Por eso, después de trasladar el saldo original a `saldoLiquidacion`, puede quedar `PAGADA` al cancelar la deuda de liquidación aunque `saldoPendiente` conserve el importe original trasladado.

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
