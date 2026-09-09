# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CHAT_CONTEXT.md`: contexto específico para nuevas conversaciones con ChatGPT.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y próximos bloques.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos del proyecto.
- `09_TARJETAS_CREDITO.md`: diseño, decisiones y estado de implementación de tarjetas.
- `CONTINUIDAD_2026-09-08.md`: corte histórico del 08/09/2026; no debe utilizarse como fuente del estado vigente.
- `CONTINUIDAD_2026-09-05.md`: corte histórico anterior; no debe utilizarse como fuente del estado vigente.

## Estado vigente — 09/09/2026

Rama de trabajo: `feature/swing-shell`.

Último commit funcional: `13a68fb8429d930b2137b9c9e78f7b884077f33e` — `fix: estabilizar formato de moneda en obligaciones`.

`main`: `a4be85913847200cb70976d5266d9cbba10b3100`.

La comparación verificada en GitHub indica **411 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias.

La arquitectura funcional es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza `GastosPanel → GastoService → MovimientoService → Movimiento` `EGRESO`.

Ingresos utiliza `IngresosPanel → IngresoService → MovimientoService → Movimiento` `INGRESO`.

Transferencias utilizan `TransferenciasPanel → OperacionFinancieraService → OperacionFinanciera`, que agrupa un `EGRESO` en origen y un `INGRESO` en destino.

`FormaPago` está integrada y validada. Las cinco opciones son `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

Las compras con `TARJETA_CREDITO` cuentan con modelo de obligaciones. `GastoService` crea una `Obligacion` mediante `ObligacionService`; las obligaciones soportan estados `PENDIENTE`, `PARCIAL` y `PAGADA` y pagos autorizados por usuario.

La moneda del movimiento puede informarse explícitamente. La obligación conserva la moneda económica del movimiento de origen y `ObligacionesPanel` muestra el código de moneda en importe original y saldo pendiente.

No se realiza conversión automática ARS↔USD al crear la obligación.

## Última validación

Suite general más reciente, ejecutada por el usuario el **09/09/2026 13:15:48 -03:00**:

`mvn test` → **642 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **09:43 min**.

Validación focalizada de moneda en obligaciones, ejecutada el **09/09/2026 13:05:03 -03:00**:

`mvn test -Dtest=ObligacionesPanelTest` → **4 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**, duración **01:20 min**.

## Estado local confirmado

El usuario informó que `git diff`, `git diff --check` y `git status` dejaron el working tree limpio y que `feature/swing-shell` estaba sincronizada con `github/feature/swing-shell`.

## Regla para continuar

Antes de cualquier cambio reconstruir el estado desde GitHub: rama → commits → comparación con `main` → README/documentación → código → tests.

No modificar `main` ni crear nuevas ramas salvo indicación explícita.

No asumir sincronizaciones, resultados de tests ni estado local de `git diff`, `git diff --check` o `git status` que no hayan sido informados o verificados.

## Pendientes principales

1. Ampliar pasivos y patrimonio neto.
2. Resolver progresivamente límite/crédito disponible y tratamiento multidivisa de tarjetas, luego ciclos, vencimientos, pagos y cuotas.
3. Análisis histórico, resúmenes, evolución patrimonial, vencimientos y dashboard.
4. Pulido posterior de la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico.
