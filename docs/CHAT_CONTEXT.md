# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 12/09/2026

La fuente de verdad es el código, los tests y Git. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit de código: `6c1b896` — `build: configurar jar ejecutable y dependencias`.

Suite general más reciente informada: **690/690**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Estado consolidado

La Fase 8 Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas en una operación coordinada. El pago coordinado existe en `PagoTarjetaService`. Los ciclos de facturación y cuotas ya cubren cambio de año.

H2 aplicación: `jdbc:h2:tcp://localhost/./database/sofp`. Tests: H2 memoria independiente.

## Auditoría y próximos cambios

P0:

1. Proteger movimientos origen de obligaciones frente a cambios de importe, fecha/hora, tipo y eliminación.
2. Revisar operaciones públicas de `ObligacionService` que no reciben `usuarioId`.
3. Integrar `PagoTarjetaService` en `ObligacionesPanel` para registrar salida real de fondos junto con el pago de deuda.

P1:

4. Proteger cambios de tipo/moneda de `Cuenta` cuando exista historial financiero.
5. Definir reglas de ciclo aplicadas al pago: vencimiento, mora, gracia y días no hábiles.
6. Definir multidivisa de tarjetas sin conversiones implícitas.
7. Definir financiación avanzada.

P2/P3:

8. UI específica de tarjetas.
9. Pasivos, patrimonio, histórico, vencimientos, resúmenes y dashboard.
10. Pulido de consola.

## Reglas

No duplicar reglas de negocio en Swing. Mantener autorización en servicios/repositorios. No inventar reglas multidivisa o de mora. Cada bloque debe incluir tests y validación de persistencia cuando corresponda.

## Continuidad

Reconstruir siempre desde GitHub antes de cambios: rama → commits → comparación con `main` → documentación → código → tests → último resultado → próximo paso.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque porque compila.