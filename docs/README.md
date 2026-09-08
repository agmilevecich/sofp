# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CONTINUIDAD_2026-09-08.md`: fotografía consolidada del estado actual para retomar el proyecto.
- `CHAT_CONTEXT.md`: contexto específico para nuevas conversaciones con ChatGPT.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y próximos bloques.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos del proyecto.

El archivo `CONTINUIDAD_2026-09-05.md` conserva un corte histórico anterior y no debe utilizarse como fuente del estado actual.

## Estado vigente — 08/09/2026

Rama de trabajo: `feature/swing-shell`.

HEAD verificado antes de la actualización documental: `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b`.

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La comparación verificada indica que `feature/swing-shell` está **326 commits por delante y 0 por detrás** de `main`, con merge-base `a4be859`. No se realizó merge.

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

La arquitectura funcional es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` `EGRESO`.

`FormaPago` está integrada y validada. Las cinco opciones son `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

Las compras con `TARJETA_CREDITO` ya cuentan con modelo de obligaciones: `GastoService` crea una `Obligacion` mediante `ObligacionService`. Las obligaciones soportan estados `PENDIENTE`, `PARCIAL` y `PAGADA` y registro de pagos.

La UI de obligaciones/pagos todavía no está implementada.

## Última validación

El usuario informó el **08/09/2026 13:27:36 -03:00**:

`mvn test` → **618 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **21:26 min**.

Antes de la suite general: `GastosPanelTest` + `MainFrameMovimientosTest` → **8/8**.

Validación final local: `git diff`, `git diff --check` y `git status` limpios; working tree limpio y rama sincronizada con `github/feature/swing-shell`.

## Regla para continuar

Antes de cualquier cambio reconstruir el estado desde GitHub: rama → commits → comparación con `main` → README/documentación → código → tests.

No modificar `main` ni crear nuevas ramas salvo indicación explícita.

## Pendientes principales

1. UI Swing de obligaciones y registro de pagos.
2. Integración de esa UI en `MainFrame`/`SidebarPanel` y refresco de datos.
3. Ingresos y transferencias mediante el núcleo común.
4. Ampliación de pasivos y patrimonio neto.
5. Análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
6. Pulido posterior de la salida de consola de la aplicación.
