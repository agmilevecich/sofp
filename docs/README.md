# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CHAT_CONTEXT.md`: contexto para nuevas conversaciones.
- `CHAT_CONTEXT_FINAL.md`: contexto compacto de continuidad.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y orden de ejecución.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos.
- `09_TARJETAS_CREDITO.md`: diseño, estado y auditoría de tarjetas.
- `CONTINUIDAD_2026-09-05.md`, `CONTINUIDAD_2026-09-08.md`, `CONTINUIDAD_2026-09-09.md` y `CONTINUIDAD_2026-09-11.md`: cortes históricos.
- `CONTINUIDAD_2026-09-12.md`: auditoría vigente y mapa de mejoras.

## Estado vigente — 12/09/2026

Rama de trabajo: `feature/swing-shell`.

Último commit de código: `6c1b896` — `build: configurar jar ejecutable y dependencias`.

`main` → `a4be85913847200cb70976d5266d9cbba10b3100`.

Suite general más reciente informada: **690/690**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Auditoría vigente

La auditoría determinó que el próximo trabajo no es rehacer ciclos de facturación básicos ni el saldo inicial de tarjetas: esos bloques ya están implementados y cubiertos.

El siguiente orden es:

1. proteger movimientos que originan obligaciones;
2. cerrar superficies públicas de `ObligacionService` y autorización;
3. integrar el pago real de tarjeta en la UI mediante `PagoTarjetaService`;
4. proteger cambios estructurales de tipo/moneda de cuentas con historial;
5. definir reglas de ciclo aplicadas al pago;
6. definir multidivisa de tarjetas;
7. financiación avanzada;
8. UI específica de tarjetas;
9. pasivos/patrimonio y análisis;
10. pulido de consola.

## Regla de continuidad

Antes de cualquier cambio reconstruir desde GitHub: rama → commits → comparación con `main` → documentación → código → tests → último resultado conocido → próximo paso.

No modificar `main`, no asumir resultados locales no informados y no considerar cerrado un bloque solamente porque compila.
