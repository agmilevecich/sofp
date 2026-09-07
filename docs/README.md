# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CONTINUIDAD_2026-09-05.md`: contexto consolidado, actualizado ahora al estado del 07/09/2026.
- `CHAT_CONTEXT.md`: contexto específico para nuevas conversaciones con ChatGPT.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y próximos bloques.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos del proyecto.

## Estado vigente — 07/09/2026

Rama de trabajo: `feature/swing-shell`.

Último commit de código: `26f7f5891f1657d6c343d20a378b291c3eb310fd`.

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La comparación verificada indica que la rama de trabajo está **292 commits por delante y 2 por detrás** de `main`, con merge-base `96f3d99969b0090dda9f502cf2cf999b87650386`. No se realizó merge.

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

La arquitectura funcional es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` `EGRESO`.

`FormaPago` está integrada y validada. Se ofrecen `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`. La tarjeta de crédito permanece temporalmente bloqueada hasta disponer del modelo de obligaciones/pasivos.

Los últimos cambios de UI son ajustes visuales de `CuentasPanel`, `MovimientosPanel` y `CategoriasPanel`, en los commits `5faff68`, `5310ba3` y `26f7f58`.

## Última validación

El usuario informó el **07/09/2026 14:59:12 -03:00**:

`mvn test` → **602 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **10:54 min**.

Pruebas específicas recientes: `CuentasPanelTest` **3/3**, `MovimientosPanelTest` **3/3** y `CategoriasPanelTest` **4/4**.

## Regla para continuar

Antes de cualquier cambio reconstruir el estado desde GitHub: rama → commits → comparación con `main` → código → tests → documentación.

No modificar `main` ni crear nuevas ramas salvo indicación explícita.

## Pendientes principales

1. Obligaciones/pasivos para tarjeta de crédito.
2. Ingresos y transferencias mediante el núcleo común.
3. Pasivos y patrimonio neto.
4. Análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Pulido posterior de la salida de consola de la aplicación.
