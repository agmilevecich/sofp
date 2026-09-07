# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 07/09/2026

La fuente de verdad es el código, los tests y los commits actuales. `docs/` es documentación auxiliar.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

La comparación verificada antes de la actualización documental indica que `feature/swing-shell` está **306 commits por delante y 2 por detrás** de `main`, con merge-base `96f3d99969b0090dda9f502cf2cf999b87650386`. No se realizó merge.

## Último estado funcional

Último commit funcional antes de la actualización documental: `06a9fd849aeef9947ad79b9cd6a9943ec93ee8c3` — `fix: corregir pruebas de saldo con usuario`.

El commit corrigió `MovimientoServiceSaldoTest` para que los registros utilizados en las pruebas de saldo pasaran el `usuarioId` y ejercitaran la API pública de `MovimientoService`. No hubo cambio de producción.

El shell Swing de Fase 8 integra Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes mediante `CardLayout`.

## Arquitectura funcional

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` de tipo `EGRESO`, y el resultado aparece en `Movimientos`.

## FormaPago

Integración **completada y validada**.

`Movimiento` persiste la forma de pago. `MovimientoService` la propaga al registrar. `GastoService` exige una forma de pago.

Formas disponibles: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`TARJETA_CREDITO` continúa temporalmente rechazada hasta implementar obligaciones/pasivos. No se debe simular un egreso inmediato sobre una cuenta para una compra a crédito.

## Reglas de saldo

Los egresos respetan fondos disponibles, incluyendo modificaciones de importe y tipo. Un egreso igual al saldo está permitido y deja saldo cero.

`MovimientoServiceSaldoTest`: **3/3**, cubriendo rechazo de egreso superior al saldo, aceptación de egreso exacto y aumento de importe de un egreso hasta el saldo.

## Última validación conocida

El usuario informó el **07/09/2026 20:12:52 -03:00**:

- comando: `mvn -Dtest=MovimientoServiceSaldoTest,MovimientoServiceTest,IngresoServiceTest,GastoServiceTest test`;
- Tests run: **61**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **03:09 min**.

Detalle: `MovimientoServiceTest` **50/50**, `MovimientoServiceSaldoTest` **3/3** y `IngresoServiceTest`/`GastoServiceTest` incluidos sin fallos.

La última suite general conocida sigue siendo la del **07/09/2026 14:59:12 -03:00**: `mvn test`, **602/602**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **10:54 min**. No fue repetida después de los cambios de saldo.

## Pendientes

1. Diseñar/modelar obligaciones y pasivos para tarjeta de crédito antes de habilitar su efecto financiero.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar resúmenes, análisis histórico, evolución patrimonial, vencimientos y dashboard.
5. Como pulido posterior, limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

## Protocolo para nuevas sesiones

1. Revisar rama actual.
2. Revisar últimos commits.
3. Comparar con `main`.
4. Revisar README y documentación de continuidad.
5. Revisar archivos modificados recientemente.
6. Revisar tests relacionados.
7. Identificar último cambio, último test conocido y próximo paso.

Prioridad: **código → tests → commits → main → documentación**.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir sincronizaciones o resultados de tests no informados.
