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

Último commit funcional: `87052df953dbd282a43c5647d05b68d1854c4f17` — `test: cubrir navegacion hacia obligaciones`.

Los commits posteriores al funcional son actualizaciones documentales.

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

No se realizó merge a `main`.

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

La arquitectura funcional es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` `EGRESO`.

`FormaPago` está integrada y validada. Las cinco opciones son `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

Las compras con `TARJETA_CREDITO` cuentan con modelo de obligaciones. `GastoService` crea una `Obligacion` mediante `ObligacionService`; las obligaciones soportan estados `PENDIENTE`, `PARCIAL` y `PAGADA` y registro de pagos autorizados por usuario.

La UI de obligaciones ya está implementada mediante `ObligacionesPanel` e integrada en `MainFrame`/`SidebarPanel`. Permite consultar obligaciones, seleccionar una, registrar pagos y refrescar conservando la selección.

## Última validación

Suite general más reciente conocida, ejecutada el **08/09/2026 13:27:36 -03:00**:

`mvn test` → **618 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **21:26 min**.

Esta suite es anterior a la UI de obligaciones.

Validación focalizada posterior, ejecutada el **08/09/2026 14:14:14 -03:00**:

`mvn -Dtest=MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,ObligacionServiceTest test` → **14 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **01:48 min**.

## Regla para continuar

Antes de cualquier cambio reconstruir el estado desde GitHub: rama → commits → comparación con `main` → README/documentación → código → tests.

No modificar `main` ni crear nuevas ramas salvo indicación explícita.

## Pendientes principales

1. Ejecutar la suite completa `mvn test` sobre el estado actual.
2. Ingresos y transferencias mediante el núcleo común.
3. Ampliación de pasivos y patrimonio neto.
4. Análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
5. Pulido posterior de la salida de consola de la aplicación.
