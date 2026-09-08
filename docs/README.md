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

Último cambio funcional verificado: `f9339db2a500d5530eea95dc39e14e2725f4eb8f` — `test: cubrir navegacion hacia ingresos`.

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La rama de trabajo está 366 commits adelante y 0 atrás respecto de `main`. No se realizó merge a `main`.

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes y Obligaciones.

La arquitectura funcional es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` `EGRESO`.

Ingresos utiliza `IngresosPanel → IngresoService → MovimientoService → Movimiento` `INGRESO`.

`FormaPago` está integrada y validada. Las cinco opciones son `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

Las compras con `TARJETA_CREDITO` cuentan con modelo de obligaciones. `GastoService` crea una `Obligacion` mediante `ObligacionService`; las obligaciones soportan estados `PENDIENTE`, `PARCIAL` y `PAGADA` y registro de pagos autorizados por usuario.

La UI de obligaciones está implementada mediante `ObligacionesPanel` e integrada en `MainFrame`/`SidebarPanel`. Permite consultar obligaciones, seleccionar una, registrar pagos y refrescar conservando la selección.

La UI de Ingresos está implementada mediante `IngresosPanel` e integrada en `MainFrame`/`SidebarPanel`. Permite registrar ingresos usando cuentas y categorías activas del perfil/usuario autorizado.

## Última validación

Suite general más reciente, ejecutada por el usuario el **08/09/2026 15:16:17 -03:00**:

`mvn test` → **630 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **11:55 min**.

Validación focalizada de Ingresos/navegación, ejecutada el **08/09/2026 14:55:35 -03:00**:

`mvn -Dtest=IngresosPanelTest,MainFrameNavigationTest test` → **5 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **01:21 min**.

Validación relacionada, ejecutada el **08/09/2026 14:58:53 -03:00**:

`mvn -Dtest=IngresoServiceTest,IngresosPanelTest,MainFrameNavigationTest,MainFrameObligacionesTest,ObligacionesPanelTest,GastosPanelTest test` → **18 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **01:31 min**.

## Regla para continuar

Antes de cualquier cambio reconstruir el estado desde GitHub: rama → commits → comparación con `main` → README/documentación → código → tests.

No modificar `main` ni crear nuevas ramas salvo indicación explícita.

No asumir sincronizaciones, resultados de tests ni estado local de `git diff`, `git diff --check` o `git status` que no hayan sido informados o verificados.

## Pendientes principales

1. Evolucionar transferencias mediante `OperacionFinanciera`, diferenciándolas de ingresos y gastos.
2. Ampliar pasivos y patrimonio neto.
3. Análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
4. Pulido posterior de la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.
