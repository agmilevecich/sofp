# SOFP — Estado actual

> Actualizado: 01/11/2026. Esta síntesis refleja el estado informado por el usuario y reconstruido desde GitHub. Para cualquier cambio, volver a consultar rama, commits, código y tests.

## Git

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- HEAD de código previo a esta actualización documental: `afc66589c9d32988fa6e011fd0293e71fbf47c39` — `fix: refrescar obligaciones al registrar gastos`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Comparación verificada en GitHub: **457 ahead / 0 behind** antes de estos commits documentales.
- No se modificó ni mergeó `main`.

## Última validación completa informada

Comando: `mvn clean test`.

- **1008 tests**.
- 0 failures, 0 errors, 0 skipped.
- `BUILD SUCCESS`.
- Finalizada: **01/11/2026 18:12:30 -03:00**.

Validación específica anterior al último commit de código:
- `MainFrameTest`, `GastosPanelTest`, `ObligacionesPanelTest`: **18/18**, 0 failures/errors/skipped, `BUILD SUCCESS`, finalizada **01/11/2026 16:34:22 -03:00**.

## Último cambio funcional

Commit `afc6658` — `fix: refrescar obligaciones al registrar gastos`.

En `MainFrame.java`, el marco principal conserva la referencia a `ObligacionesPanel` y pasa a `GastosPanel` una acción de refresco. Al registrar un gasto que genera una obligación, el panel de obligaciones se actualiza sin cerrar/reabrir la aplicación. El usuario confirmó también que el consumo aparece inmediatamente en Obligaciones y Tarjetas.

## RC1 — estabilidad de uso real

Continúan validados por tests y pruebas manuales reportadas:
- arranque, login y navegación Swing;
- persistencia entre reinicios;
- categorías por tipo de movimiento;
- ingresos, gastos, transferencias y movimientos;
- inversiones, cartera y patrimonio;
- resultado financiero y reportes;
- obligaciones, cuotas, financiación y refinanciación;
- tarjetas de crédito y pagos por ciclo;
- actualización inmediata de Obligaciones al registrar consumos.

## Pendiente inmediato

Completar la validación manual del flujo de pago de tarjeta desde la aplicación:
- identificar los saldos y estados actuales de las obligaciones;
- comprobar pago parcial/total del ciclo seleccionado;
- verificar que obligaciones de otros ciclos no cambien;
- verificar movimiento de egreso, saldo de cuenta pagadora y persistencia tras reiniciar.

El usuario no puede adjuntar capturas por el momento y retomará esta inspección cuando pueda. No inferir importes actuales a partir de capturas antiguas. La validación manual de pagos sigue abierta.

## Próximo paso

Al retomar, solicitar la captura actual del panel Tarjetas/Obligaciones y analizar los ciclos, importes y estados antes de recomendar cualquier operación. No realizar cambios de código hasta que la evidencia indique un defecto reproducible.

## Regla de fuente de verdad

Código actual → tests → commits de GitHub → comparación con `main` → documentación → conversaciones históricas. La documentación es auxiliar y nunca prevalece sobre el código o los tests.
