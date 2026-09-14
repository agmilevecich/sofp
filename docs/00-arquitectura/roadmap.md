# Roadmap del proyecto

## Estado auditado — 14/09/2026

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

El dominio actual incluye posiciones, costo promedio, consolidación, valorización y aislamiento por perfil financiero.

**Estado:** cerrada y validada.

## Fase 6: Reportes

Incorporar reportes de cartera, composición, movimientos y evolución histórica de saldos.

El estado actual incluye reporte consolidado, composición detallada, movimientos de cartera y evolución histórica de saldos.

**Estado:** cerrada y validada.

## Fase 7: Seguridad

Agregar autenticación, autorización y aislamiento de información por usuario/perfil.

La auditoría transversal de seguridad y aislamiento está implementada e integrada en `main`. Las operaciones financieras, cuentas, categorías, movimientos y posiciones cuentan con controles de propietario según corresponda.

**Estado:** cerrada.

## Fase 8: Interfaz de usuario Swing

La Fase 8 **ya está en implementación**. La rama de trabajo `feature/swing-shell` contiene shell Swing y paneles funcionales integrados con servicios.

Ya están integrados, entre otros:

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

## Bloque transversal actual: tarjetas de crédito

El modelo de tarjeta, obligaciones, cuotas simples, pagos coordinados, ciclos históricos, vencimientos de fin de semana, gracia, mora, autorización e integridad histórica están implementados y validados.

La auditoría integral del 14/09/2026 detectó que la **multidivisa todavía no está cerrada**. Los problemas concretos son:

- los cálculos de saldo pueden mezclar monedas;
- la validación de fondos puede mezclar monedas;
- un consumo en moneda distinta de la tarjeta no tiene una regla completa de impacto sobre el límite;
- no existe todavía una regla explícita de liquidación/conversión para pagos entre monedas.

No se deben agregar conversiones implícitas. Primero se debe definir moneda de liquidación, tasa, fecha/fuente de cotización y representación de saldos por moneda.

## Fase 9: Optimización

Optimizar consultas, cálculo de saldos, rendimiento general y experiencia de uso a medida que aumente el volumen de información.

**Estado:** futura.

## Próximos bloques identificados por la auditoría

### P0/P1

1. Resolver multidivisa de tarjetas de forma coherente por moneda.
2. Cubrirla con tests específicos y relacionados.

### P2

3. Robustecer validación de `Moneda.cantidadDecimales`.
4. Definir política de eliminación de cuentas con historial.
5. Evaluar `Clock` para determinismo temporal.
6. Evaluar migraciones/versionado de esquema para una futura etapa no local.

### P3

7. Financiación avanzada.
8. UI específica de tarjetas.
9. Pasivos, patrimonio y análisis.
10. Gestión de entidades financieras.
11. Pulido de consola.

## Validación global conocida

La última suite informada por el usuario es **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 13/09/2026 22:05:14 -03:00.

## Regla de continuidad

Cualquier nueva sesión debe reconstruir el estado desde código, tests y commits de GitHub antes de modificar código. `docs/` no reemplaza esa verificación.
