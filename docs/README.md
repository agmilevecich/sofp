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

HEAD: `c74717ab0e97398764a7ef6b4c9cb55cb93f20c5` — `test: persistir liquidacion historica de Obligacion`.

`main` → `a4be85913847200cb70976d5266d9cbba10b3100`.

La rama de trabajo está 677 commits adelante de `main` y 0 atrás. No se realizó merge.

Suite general más reciente informada: **712/712**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 15/09/2026 18:05:46 -03:00.

## Estado multidivisa

La primera etapa de liquidación histórica está implementada:

- `Obligacion` separa moneda original y moneda de liquidación;
- `TipoCambio` representa cotización histórica con origen, destino, fecha/hora y fuente;
- la obligación conserva la cotización utilizada;
- la liquidación es explícita y trazable;
- no hay conversiones implícitas;
- la persistencia está cubierta por tests JPA.

El próximo bloque es integrar esta liquidación en `PagoTarjetaService` y definir el impacto de consumos en moneda distinta sobre crédito disponible.

## Regla de continuidad

Antes de cualquier cambio reconstruir desde GitHub: rama → commits → comparación con `main` → documentación → código → tests → último resultado conocido → próximo paso.

No modificar `main`, no asumir resultados locales no informados y no considerar cerrado un bloque solamente porque compila.
