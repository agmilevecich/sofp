# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 13/09/2026

La fuente de verdad es el código, los tests y Git. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Últimos commits funcionales:**

- `a2a96bb` — `fix: exigir usuario al registrar pagos de obligaciones`;
- `2813fa3` — `test: adaptar pagos de obligaciones a usuario autorizado`.

Los commits posteriores son de documentación de continuidad. No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite específica de `ObligacionService`: **9/9**, `BUILD SUCCESS`.

Suite relacionada: **69/69**, `BUILD SUCCESS`; con warning de Surefire por demora de terminación de JVM, sin fallo de tests.

Tests específicos de UI de pago: **6/6**, `BUILD SUCCESS`.

El usuario informó además `git syncsofp`, `git diff`, `git diff --check` y `git status` correctos, con working tree limpio y rama sincronizada.

## Estado consolidado

La Fase 8 Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en `ObligacionesPanel`, `MainFrame` y `Main`.

`ObligacionService` exige `usuarioId` para registrar pagos y valida el perfil propietario.

H2 aplicación: `jdbc:h2:tcp://localhost/./database/sofp`. Tests: H2 memoria independiente.

## Auditoría de Cuenta completada

Se revisaron `Cuenta`, `CuentaService`, `CuentaRepository`, `TipoCuenta`, `Moneda`, `Movimiento`, `MovimientoRepository`, `MovimientoService`, `GastoService`, `OperacionFinanciera`, `MovimientoActivo` y los tests relacionados.

No se modificó código.

Hallazgos:

1. `CuentaService` permite modificar tipo y moneda sin comprobar historial financiero.
2. Una cuenta puede pasar a `TARJETA_CREDITO` sin garantizar automáticamente límite, cierre y vencimiento.
3. No existe política para los datos de crédito al salir de tarjeta hacia otro tipo.
4. Cambiar moneda con historial puede romper la interpretación del histórico.
5. La moneda de `Movimiento` es propia y puede diferir de la moneda estructural de la cuenta en consumos de tarjeta; no debe eliminarse ese caso válido.
6. Las transferencias entre cuentas ya exigen misma moneda.
7. `CuentaRepository` no contiene reglas de negocio de tipo/moneda.

Regla base para implementar: proteger cambios estructurales con historial y hacer explícita la política de transición hacia/desde tarjeta. No introducir conversiones implícitas ni reglas que impidan consumos de tarjeta en moneda extranjera.

## Pendientes reales

P0: ninguno en el bloque de autorización de obligaciones.

P1:

1. Implementar la integridad de `Cuenta` conforme a la auditoría.
2. Definir reglas de ciclo aplicadas al pago.
3. Definir multidivisa de tarjetas.
4. Definir financiación avanzada.

P2/P3:

5. UI específica de tarjetas.
6. Pasivos, patrimonio, histórico, vencimientos, resúmenes y dashboard.
7. Gestión de entidades financieras.
8. Pulido de consola.

## Reglas

No duplicar reglas de negocio en Swing. Mantener autorización en servicios. No inventar reglas multidivisa o de mora. Cada bloque debe incluir tests y validación de persistencia cuando corresponda.

## Continuidad

Reconstruir siempre desde GitHub antes de cambios: rama → commits → comparación con `main` → documentación → código → tests → último resultado → próximo paso.

La documentación se actualiza sobre la rama activa. No modificar `main` y no asumir resultados locales no informados.
