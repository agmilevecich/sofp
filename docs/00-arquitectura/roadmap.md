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
