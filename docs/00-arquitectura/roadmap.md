# Roadmap del proyecto

## Fase 1: Infraestructura

Definir la estructura inicial del proyecto, la configuración de persistencia y las verificaciones básicas de conexión.

## Fase 2: Modelo de dominio

Incorporar el modelo financiero, incluyendo usuarios, perfiles financieros, monedas, cuentas, activos y la base transversal de auditoría.

## Fase 3: Persistencia y acceso a datos

Persistir el modelo de dominio y establecer los mecanismos necesarios para consultar y almacenar la información.

## Fase 4: Operaciones financieras

Implementar `OperacionFinanciera` y su asociación con los movimientos resultantes. En el estado actual, los efectos monetarios se registran mediante `Movimiento`, que pertenece a una `Cuenta`. La operación puede agrupar hasta dos movimientos y constituye el contexto de negocio de esos movimientos.

La evolución posterior incorporó movimientos específicos para posiciones de activos mediante `MovimientoActivo`, asociados a `OperacionFinanciera`. Este modelo ya forma parte del dominio implementado.

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

## Fase 6: Reportes

Incorporar consultas y reportes de cartera, composición de activos, movimientos y evolución de saldos.

La Fase 6 ya cuenta con la primera funcionalidad de reportes consolidada: `ReporteCarteraActivo` permite representar el costo total, valor actual total y ganancia o pérdida total de una cartera a partir de sus valorizaciones. `CarteraActivoService` integra esta funcionalidad mediante la obtención del reporte consolidado a partir de las posiciones valorizadas y los precios actuales informados.

### Estado actual de la Fase 6

- Reporte consolidado de cartera: implementado.
- Integración del reporte en `CarteraActivoService`: implementada.
- Pruebas del reporte consolidado: implementadas.
- Pruebas de integración del reporte desde el servicio: implementadas.
- Composición detallada de activos: pendiente.
- Reportes de movimientos: pendiente.
- Evolución histórica de saldos: pendiente.

## Fase 7: Seguridad

Agregar autenticación, autorización y controles de acceso para proteger la información de cada usuario.

## Fase 8: Interfaz de usuario

Incorporar la interfaz Swing cuando el dominio, los servicios y las operaciones principales estén suficientemente consolidados.

## Fase 9: Optimización

Optimizar consultas, cálculo de saldos, rendimiento general y experiencia de uso a medida que aumente el volumen de información.
