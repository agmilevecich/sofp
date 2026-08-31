# Roadmap del proyecto

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

Implementar `OperacionFinanciera` y su asociación con los movimientos resultantes. En el estado actual, los efectos monetarios se registran mediante `Movimiento`, que pertenece a una `Cuenta`. La operación puede agrupar hasta dos movimientos y constituye el contexto de negocio de esos movimientos.

La evolución posterior incorporó movimientos específicos para posiciones de activos mediante `MovimientoActivo`, asociados a `OperacionFinanciera`. Este modelo ya forma parte del dominio implementado.

**Estado:** cerrada.

## Fase 5: Saldos y posiciones

Implementar el cálculo de saldos de cuentas a partir de sus movimientos y el cálculo de posiciones de activos a partir de sus movimientos específicos.

El dominio actual incluye `PosicionActivo`, que permite obtener cantidad, costo de adquisición y precio promedio de una posición, y `CarteraActivoService`, que consolida las posiciones de todos los activos de un perfil. También se incorporó `ValorizacionPosicionActivo` y la valorización de las posiciones de una cartera a partir de precios actuales.

### Estado actual de la Fase 5

- Cálculo de posición por activo: implementado.
- Costo promedio de adquisición: implementado.
- Consolidación de posiciones de cartera: implementado.
- Valorización de una posición: implementado.
- Valorización de la cartera: implementado.
- Pruebas unitarias y de integración asociadas: implementadas.
- Aislamiento de posición por perfil financiero: implementado.
- Fase 5 funcional: **cerrada y validada**.

## Fase 6: Reportes

Incorporar consultas y reportes de cartera, composición de activos, movimientos y evolución de saldos.

La Fase 6 cuenta con reporte consolidado, composición detallada, reporte de movimientos de cartera y evolución histórica de saldos. `ReporteCarteraActivo` permite representar el costo total, valor actual total y ganancia o pérdida total de una cartera a partir de sus valorizaciones, y también expone el detalle de composición de cada posición según su participación porcentual sobre el valor actual total. `DetalleMovimientoCarteraActivo` representa cada movimiento de activo con sus datos relevantes e importe calculado. `CarteraActivoService` integra estas funcionalidades mediante la obtención del reporte, la composición y los movimientos del perfil.

La evolución histórica de saldo se representa mediante `EvolucionSaldoCuenta` y se integra en `CuentaService.obtenerEvolucionSaldo(Long)`, generando puntos con el saldo acumulado después de cada movimiento en orden cronológico determinista.

### Estado actual de la Fase 6

- Reporte consolidado de cartera: implementado.
- Integración del reporte en `CarteraActivoService`: implementada.
- Pruebas del reporte consolidado: implementadas.
- Pruebas de integración del reporte desde el servicio: implementadas.
- Composición detallada de activos: implementada.
- Integración de la composición en `CarteraActivoService`: implementada.
- Pruebas de composición detallada: implementadas.
- Integración del reporte de movimientos en `CarteraActivoService`: implementada.
- Evolución histórica de saldos: implementada.
- Suite histórica al cierre de Fase 6: **480/480 tests en verde**.
- Fase 6: **cerrada, validada e integrada en `main` mediante fast-forward**.

## Fase 7: Seguridad

Agregar autenticación, autorización y controles de acceso para proteger la información de cada usuario.

### Estado actual de la Fase 7

La auditoría transversal de seguridad y aislamiento de datos está **cerrada, validada e integrada en `main`**.

Se completaron:

- autorización de operaciones financieras;
- autorización de operaciones mutables de cuentas, categorías y movimientos;
- aislamiento de lecturas por ID y listados;
- protección de altas de cuentas, categorías, movimientos y perfiles;
- aislamiento de posiciones y cartera por perfil/usuario;
- cierre de caminos internos que podían saltar validaciones públicas;
- cobertura transversal mediante `AislamientoDatosServiceTest`.

Validación final local del 31/08/2026:

- `AislamientoDatosServiceTest`: **7/7**;
- suite general: **512/512 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`.

La feature `feature/seguridad-aislamiento-datos` fue integrada en `main` mediante fast-forward hasta `75d0a18` y publicada en GitHub y Bitbucket.

**Fase 7: cerrada.**

## Fase 8: Interfaz de usuario

Incorporar la interfaz Swing sobre el dominio y servicios ya consolidados.

### Estado actual de la Fase 8

La implementación de Swing todavía no comenzó.

El primer bloque deberá partir de la estructura real existente en `src/main/java`, revisar las clases y servicios disponibles, y definir una arquitectura mínima de UI sin duplicar lógica de negocio.

Primer objetivo previsto: shell principal de Swing, navegación y área central para módulos, manteniendo la separación entre UI y servicios.

**Fase 8: próxima etapa.**

## Fase 9: Optimización

Optimizar consultas, cálculo de saldos, rendimiento general y experiencia de uso a medida que aumente el volumen de información.

## Estado del roadmap

Las Fases 1 a 7 están cerradas e integradas en `main` según su evolución documentada.

La validación global vigente es **512/512 tests en verde**.

La etapa activa siguiente es **Fase 8 — Interfaz de usuario Swing**.

Cualquier nueva sesión de trabajo debe reconstruir el estado desde el código, tests, commits y `main` antes de modificar código.
