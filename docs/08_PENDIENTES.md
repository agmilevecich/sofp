# SOFP — Pendientes

## Estado — 05/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

La comparación verificada antes de la actualización documental indica que `feature/swing-shell` está **274 commits por delante y 2 por detrás** de `main`. No se realizó merge.

## Bloques cerrados

### Fondos insuficientes

**Completado y validado.**

`MovimientoService` rechaza `EGRESO` superior al saldo disponible, permite el egreso igual al saldo y aplica la regla también a modificaciones de importe y tipo.

Pruebas: `MovimientoFondosInsuficientesTest` **6/6**, `MovimientoServiceTest` **57/57**, `RegistrarMovimientoPanelTest` **4/4**.

### Categorías con movimientos

**Completado y validado.**

Las categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.

`CategoriaServiceTest`: **23/23**.

### Gastos — primer corte funcional

**Completado y validado.**

`GastosPanel` registra compras, pagos de servicios y otros egresos básicos mediante `GastoService`, que delega finalmente en `MovimientoService` como `EGRESO`.

El registro queda en el historial común de `Movimientos` y respeta la regla de fondos disponibles.

Suite posterior: **586/586**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## FormaPago — completado y validado

La integración quedó cerrada después de la ejecución de la suite general.

`FormaPago` está integrada a `Movimiento`, `MovimientoService`, `GastoService` y `GastosPanel`.

Opciones actuales:

- `EFECTIVO`;
- `TRANSFERENCIA`;
- `TARJETA_DEBITO`;
- `TARJETA_CREDITO`;
- `QR`.

`GastoService` exige forma de pago.

`TARJETA_CREDITO` se rechaza explícitamente por ahora porque no existe todavía el modelo de obligaciones/pasivos necesario para representar una compra a crédito sin simular una salida inmediata de fondos.

La cobertura incluye selección en UI, persistencia, lectura y modificación en dominio y rechazo de tarjeta de crédito.

## Validación vigente

Última suite general informada por el usuario:

- comando: `mvn test`;
- Tests run: **590**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **11:29 min**;
- finalización: **05/09/2026 13:04:09 -03:00**.

## Criterio funcional adoptado a partir de ControlFinanzas

ControlFinanzas se utiliza como banco de ideas y referencia funcional, no como arquitectura para copiar.

El criterio de SOFP es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

La experiencia Swing debe permitir registrar el hecho financiero desde un panel especializado y verlo luego en el historial consolidado.

## Próximos pasos

1. Diseñar obligaciones/pasivos para tarjeta de crédito antes de habilitar su efecto financiero.
2. Evolucionar ingresos y transferencias mediante el núcleo común.
3. Incorporar progresivamente pasivos y patrimonio neto.
4. Evolucionar resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

Estos puntos son pendientes reales; no se consideran implementados por estar documentados.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.
