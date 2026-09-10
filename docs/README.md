# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CHAT_CONTEXT.md`: contexto para nuevas conversaciones.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y próximos bloques.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos.
- `09_TARJETAS_CREDITO.md`: diseño y estado de tarjetas.
- `CONTINUIDAD_2026-09-05.md`, `CONTINUIDAD_2026-09-08.md` y `CONTINUIDAD_2026-09-09.md`: cortes históricos; no deben utilizarse como fuente del estado vigente.

## Estado vigente — 10/09/2026

Rama de trabajo: `feature/swing-shell` → `548063914ad7a3fe6ae028dad606aad57f7ca42e`.

`main` → `a4be85913847200cb70976d5266d9cbba10b3100`.

GitHub verifica **443 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

El commit `5480639` es documental. El último cambio funcional es `46290786` — cierre del contexto JPA de `PosicionActivoServiceTest`.

## Estado funcional

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

Arquitectura: **paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Las compras con tarjeta de crédito generan obligaciones. La moneda del consumo se conserva en la obligación. El crédito disponible inicial se calcula como límite menos consumos pendientes en la moneda de la tarjeta, sin conversiones implícitas.

`CicloFacturacion` ya está implementado como objeto de dominio no persistente y `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento. `CicloFacturacionTest` contiene 9 tests.

## Validación más reciente

`mvn test` — **664/664**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutado por el usuario el **09/09/2026 21:58:35 -03:00**, duración **10:04 min**.

## Próximo trabajo

1. Integrar ciclos con consumos y obligaciones.
2. Unificar el saldo monetario de tarjetas entre `MovimientoService` y `CuentaService`.
3. Profundizar pagos/liberación de crédito y reglas multidivisa explícitas.
4. Cuotas y financiación.
5. UI específica de tarjetas.
6. Pasivos/patrimonio, análisis y dashboard.
7. Pulido de consola.

## Regla de continuidad

Antes de cualquier cambio reconstruir desde GitHub: rama → commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
