# Roadmap del proyecto

## Estado auditado — 16/09/2026

La fuente de verdad es el código, los tests y los commits actuales. Este roadmap es documentación auxiliar y debe actualizarse cuando el código avance.

## Fase 1: Infraestructura

Definir estructura inicial, persistencia y verificaciones básicas de conexión.

**Estado:** cerrada.

## Fase 2: Modelo de dominio

Modelo financiero con usuarios, perfiles, monedas, cuentas, activos y auditoría.

**Estado:** cerrada.

## Fase 3: Persistencia y acceso a datos

Persistencia del dominio y mecanismos de consulta/almacenamiento.

**Estado:** cerrada.

## Fase 4: Operaciones financieras

`OperacionFinanciera`, `Movimiento` y posiciones de activos.

**Estado:** cerrada.

## Fase 5: Saldos y posiciones

Saldos de cuentas y posiciones de activos, respetando moneda y perfil.

**Estado:** cerrada y validada.

## Fase 6: Reportes

Reportes de cartera, composición, movimientos y evolución histórica.

**Estado:** cerrada y validada.

## Fase 7: Seguridad

Autenticación, autorización y aislamiento por usuario/perfil.

**Estado:** cerrada.

## Fase 8: Interfaz de usuario Swing

La Fase 8 está activa en `feature/swing-shell` e incluye shell y paneles funcionales integrados con servicios.

Integrados:

- shell principal y navegación;
- cuentas;
- categorías;
- ingresos;
- gastos;
- movimientos;
- inversiones;
- obligaciones;
- pagos de tarjeta desde UI.

La UI específica completa de tarjetas continúa pendiente.

## Bloque transversal: multidivisa de tarjetas

Implementado y validado:

- moneda económica explícita en `Movimiento`;
- moneda original y moneda de liquidación en `Obligacion`;
- `TipoCambio` histórico explícito y persistente;
- liquidación histórica separada de la valorización de cierre;
- `saldoLiquidacion` y pagos multidivisa;
- valorización histórica para crédito disponible;
- cálculo proporcional del crédito después de pagos parciales sobre obligaciones valorizadas.

Pendiente:

- flujo de obtención/registro de la valorización de cierre dentro de la aplicación;
- comportamiento de obligaciones multidivisa todavía no valorizadas;
- completar persistencia/UI del flujo integral de cierre y pago.

No se deben introducir conversiones implícitas antes de fijar reglas de negocio.

## Fase 9: Optimización

Optimizar consultas, cálculo de saldos, rendimiento y experiencia de uso cuando corresponda.

**Estado:** futura.

## Próximos bloques

### P0/P1

1. Definir el flujo de valorización de cierre dentro de la aplicación.
2. Definir el comportamiento de consumos extranjeros sin valorización de cierre.
3. Completar persistencia/UI del cierre y pago multidivisa.

### P2

4. Política de eliminación de cuentas con historial.
5. `Clock` para determinismo temporal.
6. Migraciones/versionado formal de esquema.

### P3

7. Financiación avanzada.
8. UI específica de tarjetas.
9. Pasivos, patrimonio y análisis.
10. Gestión de entidades financieras.
11. Pulido de consola.

## Validación global actual

La última suite informada por el usuario es **740/740**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 16/09/2026 15:54:16 -03:00.

## Regla de continuidad

Cualquier nueva sesión debe reconstruir el estado desde código, tests y commits de GitHub antes de modificar código. `docs/` no reemplaza esa verificación.


## Actualización de continuidad — cierre 17/09/2026 22:51 -03:00

Esta sección supersede cualquier validación anterior de este documento cuando haya contradicción.

- Rama de trabajo: `feature/swing-shell`.
- HEAD actual: `b4a9bc6b4f64ce191f90eb9e4dd4291356e0574c` — `test: corregir expectativas de valorizacion multidivisa`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- Comparación GitHub: `feature/swing-shell` está 847 commits por delante de `main` y 0 por detrás.
- No se realizó merge a `main`.
- Último bloque: corrección de expectativas de tests para reflejar que la valorización de cierre de obligaciones financiadas se almacena en la cuota; no se modificó producción en este último commit.
- Validación específica posterior: 8/8 tests verdes, 0 failures, 0 errors, `BUILD SUCCESS`, informada por el usuario.
- Validación final: `mvn test` con **769/769 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 22:51:16 -03:00**, informada por el usuario.
- Validación Git local final: `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio; rama local al día con `bitbucket/feature/swing-shell`, informado por el usuario.

### Punto exacto para retomar

El cálculo de crédito multidivisa, la valorización histórica de cierre, la liquidación explícita, los pagos antes/después de liquidar y el cierre iniciado desde `ObligacionesPanel` están cubiertos por tests. El siguiente bloque debe comenzar con una revisión de `ObligacionService` y de sus clases relacionadas para definir el flujo de cierre de resumen de tarjeta siguiendo reglas bancarias reales. Antes de modificar código se debe contrastar la cotización de cierre de consumos extranjeros con normativa BCRA y documentación vigente de la entidad financiera de referencia. No inventar una regla de negocio por inferencia.

### Regla de continuidad para la próxima sesión

Reconstruir desde GitHub antes de cualquier cambio: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. No asumir que la documentación histórica representa el estado actual si contradice código o tests.