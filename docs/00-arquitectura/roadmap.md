# Roadmap del proyecto

## Estado auditado — 15/09/2026

La fuente de verdad es el código, los tests y los commits actuales. Este roadmap es documentación auxiliar y debe actualizarse cuando el código avance.

## Fase 1: Infraestructura

Definir la estructura inicial del proyecto, la configuración de persistencia y las verificaciones básicas de conexión.

**Estado:** cerrada.

## Fase 2: Modelo de dominio

Incorporar el modelo financiero, incluyendo usuarios, perfiles financieros, monedas, cuentas, activos y la base transversal de auditoría.

**Estado:** cerrada.

## Fase 3: Persistencia y acceso a datos

Persistir el modelo de dominio y establecer los mecanismos necesarios para consultar y almacenar la información.

**Estado:** cerrada.

## Fase 4: Operaciones financieras

Implementar `OperacionFinanciera` y su asociación con los movimientos resultantes. Los efectos monetarios se registran mediante `Movimiento`, que pertenece a una `Cuenta`. La evolución posterior incorporó `MovimientoActivo` para posiciones de activos.

**Estado:** cerrada.

## Fase 5: Saldos y posiciones

Implementar cálculo de saldos de cuentas y posiciones de activos.

El dominio actual incluye posiciones, costo promedio, consolidación, valorización y aislamiento por perfil financiero. Los saldos de cuenta y disponibilidad de fondos se calculan respetando la moneda correspondiente.

**Estado:** cerrada y validada.

## Fase 6: Reportes

Incorporar reportes de cartera, composición, movimientos y evolución histórica de saldos.

El estado actual incluye reporte consolidado, composición detallada, movimientos de cartera y evolución histórica de saldos.

**Estado:** cerrada y validada.

## Fase 7: Seguridad

Agregar autenticación, autorización y aislamiento de información por usuario/perfil.

La auditoría transversal de seguridad y aislamiento está implementada e integrada. Las operaciones financieras, cuentas, categorías, movimientos y posiciones cuentan con controles de propietario según corresponda.

**Estado:** cerrada.

## Fase 8: Interfaz de usuario Swing

La Fase 8 está implementada en la rama de trabajo `feature/swing-shell` y contiene shell Swing y paneles funcionales integrados con servicios.

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

La separación UI → servicios → dominio/repositorios se mantiene.

### Estado actual de la Fase 8

- Shell Swing: implementado.
- Navegación: implementada.
- Integración con servicios: implementada.
- Gastos e ingresos: implementados.
- Obligaciones y pagos de tarjeta: implementados.
- UI específica completa de tarjetas: pendiente.

**Fase 8: activa.**

## Bloque transversal actual: multidivisa de tarjetas

La moneda económica de `Movimiento` es explícita y puede diferir de la moneda de `Cuenta`. Ya se corrigieron los cálculos generales de saldo y disponibilidad de fondos para no mezclar monedas.

Continúa pendiente el tratamiento específico de tarjetas cuando la moneda del consumo difiere de la moneda de la tarjeta:

- impacto sobre crédito disponible;
- moneda de liquidación;
- tasa de cambio;
- fecha y fuente de cotización;
- trazabilidad de la conversión/liquidación;
- pagos entre monedas.

No se deben agregar conversiones implícitas antes de fijar esas reglas de negocio.

## Fase 9: Optimización

Optimizar consultas, cálculo de saldos, rendimiento general y experiencia de uso a medida que aumente el volumen de información.

**Estado:** futura.

## Próximos bloques

### P0/P1

1. Resolver multidivisa de tarjetas de forma coherente por moneda.
2. Cubrirla con tests específicos y relacionados.

### P2

3. Política de eliminación de cuentas con historial.
4. `Clock` para determinismo temporal.
5. Migraciones/versionado formal de esquema para una futura etapa no local.

### P3

6. Financiación avanzada.
7. UI específica de tarjetas.
8. Pasivos, patrimonio y análisis.
9. Gestión de entidades financieras.
10. Pulido de consola.

## Validación global actual

La última suite informada por el usuario es **693/693**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 15/09/2026 12:03:19 -03:00.

El conteo histórico de 704 no es un objetivo de recuperación; 693/693 es el estado actual válido.

## Regla de continuidad

Cualquier nueva sesión debe reconstruir el estado desde código, tests y commits de GitHub antes de modificar código. `docs/` no reemplaza esa verificación.
