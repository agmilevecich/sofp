# SOFP — Pendientes

## Estado — 07/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

La comparación verificada antes de la actualización documental indica que `feature/swing-shell` está **306 commits por delante y 2 por detrás** de `main`, con merge-base `96f3d99969b0090dda9f502cf2cf999b87650386`. No se realizó merge.

## Último bloque cerrado

### Reglas de saldo de movimientos

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` superior al saldo disponible, permite el egreso igual al saldo y aplica la regla también a modificaciones de importe y tipo.

Se agregó `MovimientoServiceSaldoTest` con tres casos específicos. La validación relacionada más reciente fue:

- `MovimientoServiceSaldoTest`: **3/3**;
- `MovimientoServiceTest`: **50/50**;
- `IngresoServiceTest` y `GastoServiceTest`: incluidos;
- total: **61/61**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **03:09 min**;
- finalización: **07/09/2026 20:12:52 -03:00**.

El fixture de saldo fue corregido en `06a9fd8` para utilizar la API pública de `MovimientoService` pasando el usuario propietario. No se modificó producción.

## Bloques cerrados anteriores

### Categorías con movimientos

**Completado y validado.**

Las categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.

`CategoriaServiceTest`: **23/23**.

### Gastos — primer corte funcional

**Completado y validado.**

`GastosPanel` registra compras, pagos de servicios y otros egresos básicos mediante `GastoService`, que delega finalmente en `MovimientoService` como `EGRESO`.

El registro queda en el historial común de `Movimientos` y respeta la regla de fondos disponibles.

### FormaPago — completado y validado

`FormaPago` está integrada a `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`.

Opciones actuales: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

`GastoService` exige forma de pago.

`TARJETA_CREDITO` se rechaza explícitamente por ahora porque no existe todavía el modelo de obligaciones/pasivos necesario para representar una compra a crédito sin simular una salida inmediata de fondos.

### Pulido visual del shell Swing

**Completado y validado.**

Se realizaron ajustes visuales incrementales en `CuentasPanel`, `MovimientosPanel` y `CategoriasPanel`, manteniendo la lógica existente.

Commits: `5faff68`, `5310ba3` y `26f7f58`.

Pruebas específicas recientes: `CuentasPanelTest` **3/3**, `MovimientosPanelTest` **3/3**, `CategoriasPanelTest` **4/4**.

## Validación general conocida

Última suite general informada por el usuario:

- comando: `mvn test`;
- Tests run: **602**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **10:54 min**;
- finalización: **07/09/2026 14:59:12 -03:00**.

Esta suite completa no fue repetida después de los cambios de saldo. La validación relacionada posterior sí quedó en **61/61**.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia funcional, no como arquitectura para copiar.

El criterio de SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

La experiencia Swing debe permitir registrar el hecho financiero desde un panel especializado y verlo luego en el historial consolidado.

## Próximos pasos reales

1. Diseñar/modelar obligaciones y pasivos para tarjeta de crédito antes de habilitar su efecto financiero.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.
5. Como pulido posterior, limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.

Estos puntos son pendientes reales; no se consideran implementados por estar documentados.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.
