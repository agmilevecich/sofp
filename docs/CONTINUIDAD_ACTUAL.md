# SOFP — Continuidad canónica actual

> Actualizado: 01/11/2026. La documentación es auxiliar. La fuente de verdad es el código, los tests y los commits actuales de GitHub.

## Estado Git reconstruido

- Repositorio: `agmilevecich/sofp`.
- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- Último commit de código antes de esta actualización documental: `afc66589c9d32988fa6e011fd0293e71fbf47c39` — `fix: refrescar obligaciones al registrar gastos`.
- SHA de `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Comparación consultada antes de actualizar docs: **457 commits ahead / 0 behind**.
- No se modificó ni mergeó `main`.

## Último resultado de tests informado por el usuario

`mvn clean test`:
- **1008 tests**.
- 0 failures, 0 errors, 0 skipped.
- `BUILD SUCCESS`.
- Finalización: **01/11/2026 18:12:30 -03:00**.

Batería específica anterior:
- `MainFrameTest`, `GastosPanelTest`, `ObligacionesPanelTest`: **18/18**, 0 failures/errors/skipped, `BUILD SUCCESS`, 01/11/2026 16:34:22 -03:00.

Estos resultados fueron comunicados por el usuario. Los commits documentales no requieren ejecutar tests de producción; no se afirma una nueva ejecución local.

## Último cambio de código: refresco de Obligaciones

Commit `afc6658`:
- Archivo: `src/main/java/ar/com/agmilevecich/sofp/ui/MainFrame.java`.
- Se conserva una referencia a `ObligacionesPanel`.
- `GastosPanel` recibe una acción de callback para refrescar obligaciones.
- `MainFrame.refrescarObligaciones()` ejecuta `obligacionesPanel.refrescar()` si el panel está inicializado.
- El usuario confirmó manualmente que un gasto con tarjeta se refleja inmediatamente en Obligaciones y Tarjetas, sin cerrar/reabrir SOFP.

## Interpretación actual del flujo de pagos de tarjeta

La inspección del código de `TarjetasCreditoPanel.java`, `PagoTarjetaService.java` y `Obligacion.java` confirmó:
- La acción **Pago total** del panel de tarjetas cancela el saldo correspondiente al ciclo seleccionado; no equivale necesariamente a cancelar toda la deuda histórica.
- El saldo total de obligaciones es una magnitud distinta del saldo del ciclo.
- Obligaciones de otros ciclos y obligaciones financiadas, refinanciadas o liquidadas tienen tratamiento propio y pueden continuar pendientes.
- No recomendar pagos ni registrar movimientos hasta identificar importes, ciclos y estados actuales.

## Punto exacto para continuar

La suite completa está verde (**1008/1008**), y el refresco automático de Obligaciones al registrar gastos está validado. Sigue pendiente cerrar la prueba manual del pago de tarjeta en uso real.

El usuario informó que temporalmente no puede adjuntar capturas y debe esperar aproximadamente 21 horas. Cuando pueda, solicitar una captura actual del panel Tarjetas y, si hace falta, de Obligaciones. No usar como valores vigentes los importes históricos de capturas anteriores (por ejemplo, $30.000 y $45.000).

### Secuencia de validación manual pendiente

1. Revisar tarjeta, ciclo, fecha de cierre/vencimiento y saldos visibles.
2. Identificar cada obligación pendiente y su estado.
3. Distinguir saldo del ciclo de saldo total histórico.
4. Comprobar qué acción corresponde sin afectar otros ciclos.
5. Verificar cuenta pagadora y fondos disponibles antes de cualquier pago.
6. Después de la operación controlada, comprobar egreso, deuda restante, otros ciclos y persistencia al reiniciar.

No considerar cerrada RC1 hasta documentar el resultado de esta prueba manual.

## Otros pendientes técnicos conocidos

No convertirlos en trabajo inmediato sin priorización y revisión de código:
- calendario bancario explícito para feriados;
- abstracción `Clock` para fechas dependientes del reloj;
- migraciones/versionado formal del esquema;
- estabilización adicional del ciclo de vida de H2;
- optimizaciones de consultas solo cuando aporten valor;
- decisiones financieras no modeladas explícitamente.

## Flujo obligatorio de trabajo

Antes de cualquier cambio: GitHub → rama → últimos commits → comparación con `main` → implementación relacionada → repositorios → tests → reglas de negocio → documentación → cambio mínimo.

Después de cambios importantes: tests específicos → tests relacionados → suite completa cuando corresponda → `git diff` → `git diff --check` → `git status`.

Nunca asumir que el usuario sincronizó o ejecutó tests sin que lo informe. No modificar ni mergear `main` automáticamente.


## ACTUALIZACIÓN — 03/11/2026: pago total de tarjeta

Se confirmó como regla de negocio que **Pago total** debe cancelar toda la deuda pendiente de la tarjeta, sin limitarse al ciclo actual.

### Cambio aplicado
- TarjetasCreditoPanel.registrarPagoTotal() ahora utiliza PagoTarjetaService.registrarPagoTotalTarjeta(...), que distribuye el pago entre todas las obligaciones pendientes de esa tarjeta, incluyendo las correspondientes a otros ciclos y los saldos gestionados por financiación/refinanciación según las reglas del servicio.
- El importe informado por la operación es el total calculado por el servicio.
- Al seleccionar Pago total, el campo indica que el importe se calcula al confirmar; evita mostrar como total una cifra del resumen que no necesariamente incluye todos los saldos financiados/refinanciados. El importe efectivamente debitado se calcula en el servicio.
- Pago mínimo y Pago parcial conservan el flujo de pago del ciclo actual.

### Cobertura agregada
Se agregó una prueba de servicio que registra consumos en dos ciclos distintos y verifica que Pago total cancela ambas obligaciones, registra dos pagos trazables y debita el total de la cuenta pagadora.

### Validación pendiente
Los cambios fueron publicados en feature/swing-shell. El usuario debe sincronizar y ejecutar primero:
- mvn -Dtest=PagoTarjetaServiceTest test
- mvn -Dtest=TarjetasCreditoPanelTest test
- mvn clean test

No se declara todavía ningún resultado de tests sobre este HEAD. Revisar además git diff, git diff --check y git status en el entorno local. No modificar ni mergear main.
