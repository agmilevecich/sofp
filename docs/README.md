# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CHAT_CONTEXT.md`: contexto completo para nuevas conversaciones.
- `CHAT_CONTEXT_FINAL.md`: contexto compacto de continuidad.
- `CHAT_CONTEXT_NEW.md`: contexto actualizado para iniciar una nueva conversación.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y orden de ejecución.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos.
- `09_TARJETAS_CREDITO.md`: diseño, estado y auditoría de tarjetas.
- `CONTINUIDAD_2026-09-15.md`: corte de continuidad más reciente.
- `CONTINUIDAD_2026-09-12.md` y cortes anteriores: antecedentes históricos.

## Estado vigente — 15/09/2026

Rama de trabajo: `feature/swing-shell`.

Último commit de código validado: `e95585e043290eebb5789f2b628b1edcef8a7344` — `test: cubrir pagos multidivisa en PagoTarjetaService`.

`main` → `a4be85913847200cb70976d5266d9cbba10b3100`.

Suite general actual: **718/718**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **15/09/2026 20:05:19 -03:00**.

## Estado multidivisa

La integración del pago multidivisa está implementada:

- `Obligacion` separa moneda original y moneda de liquidación;
- `TipoCambio` representa cotización histórica;
- `Obligacion` conserva la cotización utilizada;
- `saldoLiquidacion` representa la deuda en moneda de liquidación;
- `PagoTarjetaService` paga ese saldo cuando existe;
- la cuenta pagadora debe estar en la moneda de liquidación;
- no hay conversiones implícitas.

Queda pendiente definir el impacto de consumos extranjeros sobre el límite/crédito disponible y completar la cobertura de persistencia/UI del pago multidivisa.

## Regla de continuidad

Antes de cualquier cambio reconstruir desde GitHub: rama → commits → comparación con `main` → documentación → código → tests → último resultado conocido → próximo paso.

No modificar `main`, no asumir resultados locales no informados y no considerar cerrado un bloque solamente porque compila.
