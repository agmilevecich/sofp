# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CONTINUIDAD_2026-09-05.md`: contexto consolidado para continuar el proyecto.
- `CHAT_CONTEXT.md`: contexto específico para nuevas conversaciones con ChatGPT.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y próximos bloques.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos del proyecto.

## Estado vigente — 05/09/2026

Rama de trabajo: `feature/swing-shell`.

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La comparación verificada indica que la rama de trabajo está **274 commits por delante y 2 por detrás** de `main`. No se realizó merge.

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

La arquitectura funcional es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` `EGRESO`.

`FormaPago` está integrada y validada. Se ofrecen `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`. La tarjeta de crédito permanece temporalmente bloqueada hasta disponer del modelo de obligaciones/pasivos.

## Última validación

El usuario informó el **05/09/2026 13:04:09 -03:00**:

`mvn test` → **590 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **11:29 min**.

## Regla para continuar

Antes de cualquier cambio reconstruir el estado desde GitHub: rama → commits → comparación con `main` → código → tests → documentación.

No modificar `main` ni crear nuevas ramas salvo indicación explícita.
