# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 13/09/2026

La fuente de verdad es el código, los tests y Git. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** `e5fbe0f` — `fix: proteger integridad estructural de cuentas`.
**Último commit verificado antes de esta actualización documental:** `00beeb1` — `fix: evitar moneda duplicada en test de integridad`.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 46:17 min, finalizada 13/09/2026 19:00:15 -03:00.

Suite relacionada de Cuenta: **154/154**, `BUILD SUCCESS`, 22:18 min.

Tests específicos `CuentaServiceIntegridadTest,CuentaServiceTest`: **66/66**, `BUILD SUCCESS`, 09:19 min.

Las validaciones de obligaciones/pagos/UI anteriores continúan vigentes: `ObligacionServiceTest` **9/9**, suite relacionada **69/69**, UI de pago **6/6**.

El usuario informó además `git syncsofp`, `git diff`, `git diff --check` y `git status` correctos, con working tree limpio y rama sincronizada.

## Estado consolidado

La Fase 8 Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en `ObligacionesPanel`, `MainFrame` y `Main`.

`ObligacionService` exige `usuarioId` para registrar pagos y valida el perfil propietario.

`CuentaService` ahora protege la integridad estructural: no permite cambiar tipo ni moneda cuando existen movimientos y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

H2 aplicación: `jdbc:h2:tcp://localhost/./database/sofp`. Tests: H2 memoria independiente.

## Integridad de Cuenta — bloque cerrado

Se implementó la regla mínima derivada de la auditoría previa.

Cobertura:

- cuenta con historial: tipo y moneda no pueden modificarse;
- cuenta sin historial: se permite cambio entre tipos no tarjeta;
- transición hacia/desde `TARJETA_CREDITO` por la API genérica: rechazada;
- autorización por usuario: preservada;
- moneda económica extranjera de consumos de tarjeta: preservada.

No se introducen conversiones automáticas ni limpieza/migración implícita de datos de crédito.

## Pendientes reales

P0: ninguno en el bloque de autorización de obligaciones ni en integridad estructural de Cuenta.

P1:

1. Definir reglas de ciclo aplicadas al pago.
2. Definir multidivisa de tarjetas.
3. Definir financiación avanzada.

P2/P3:

4. UI específica de tarjetas.
5. Pasivos, patrimonio, histórico, vencimientos, resúmenes y dashboard.
6. Gestión de entidades financieras.
7. Pulido de consola.

## Reglas

No duplicar reglas de negocio en Swing. Mantener autorización en servicios. No inventar reglas multidivisa o de mora. Cada bloque debe incluir tests y validación de persistencia cuando corresponda.

## Continuidad

Reconstruir siempre desde GitHub antes de cambios: rama → commits → comparación con `main` → documentación → código → tests → último resultado → próximo paso.

La documentación se actualiza sobre la rama activa. No modificar `main` y no asumir resultados locales no informados.
