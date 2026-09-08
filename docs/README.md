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

Último commit funcional: `aa29d44` — `test: cubrir formulario de transferencias`.

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La comparación verificada en GitHub indicó **377 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias.

La arquitectura funcional es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` `EGRESO`.

Ingresos utiliza `IngresosPanel → IngresoService → MovimientoService → Movimiento` `INGRESO`.

Transferencias utilizan `TransferenciasPanel → OperacionFinancieraService → OperacionFinanciera`, que agrupa un `EGRESO` en origen y un `INGRESO` en destino.

`FormaPago` está integrada y validada. Las cinco opciones son `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

Las compras con `TARJETA_CREDITO` cuentan con modelo de obligaciones. `GastoService` crea una `Obligacion` mediante `ObligacionService`; las obligaciones soportan estados `PENDIENTE`, `PARCIAL` y `PAGADA` y pagos autorizados por usuario.

La UI de obligaciones está implementada mediante `ObligacionesPanel` e integrada en `MainFrame`/`SidebarPanel`.

La UI de Ingresos está implementada mediante `IngresosPanel` e integrada en `MainFrame`/`SidebarPanel`.

La UI de Transferencias está implementada mediante `TransferenciasPanel` e integrada en el shell.

## Última validación

Suite general más reciente, ejecutada por el usuario el **08/09/2026 18:13:55 -03:00**:

`mvn test` → **634 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **11:52 min**.

Validación focalizada de Transferencias, ejecutada el **08/09/2026 17:59:29 -03:00**:

`mvn -Dtest=TransferenciasPanelTest test` → **4 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **01:16 min**.

Validación de Transferencias + navegación, ejecutada el **08/09/2026 18:01:19 -03:00**:

`mvn -Dtest=TransferenciasPanelTest,MainFrameNavigationTest test` → **5 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **39 s**.

## Regla para continuar

Antes de cualquier cambio reconstruir el estado desde GitHub: rama → commits → comparación con `main` → README/documentación → código → tests.

No modificar `main` ni crear nuevas ramas salvo indicación explícita.

No asumir sincronizaciones, resultados de tests ni estado local de `git diff`, `git diff --check` o `git status` que no hayan sido informados o verificados.

## Pendientes principales

1. Ampliar pasivos y patrimonio neto.
2. Análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
3. Pulido posterior de la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.
