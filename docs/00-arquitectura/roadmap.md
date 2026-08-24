# Roadmap del proyecto

## Fase 1: Infraestructura

Definir la estructura inicial del proyecto, la configuración de persistencia y las verificaciones básicas de conexión.

## Fase 2: Modelo de dominio

Incorporar el modelo financiero, incluyendo usuarios, perfiles financieros, monedas, cuentas, activos y la base transversal de auditoría.

## Fase 3: Persistencia y acceso a datos

Persistir el modelo de dominio y establecer los mecanismos necesarios para consultar y almacenar la información.

## Fase 4: Operaciones financieras

Implementar `OperacionFinanciera` y su asociación con los movimientos resultantes. En el estado actual, los efectos monetarios se registran mediante `Movimiento`, que pertenece a una `Cuenta`. La operación puede agrupar hasta dos movimientos y constituye el contexto de negocio de esos movimientos.

La evolución hacia movimientos específicos para posiciones de activos (`MovimientoActivo`) queda como una etapa futura y no debe considerarse parte del modelo actualmente implementado.

## Fase 5: Saldos y posiciones

Implementar el cálculo de saldos de cuentas a partir de sus movimientos y, posteriormente, el cálculo de posiciones de activos a partir de sus movimientos específicos cuando ese modelo sea incorporado.

## Fase 6: Reportes

Incorporar consultas y reportes de cartera, composición de activos, movimientos y evolución de saldos.

## Fase 7: Seguridad

Agregar autenticación, autorización y controles de acceso para proteger la información de cada usuario.

## Fase 8: Interfaz de usuario

Incorporar la interfaz Swing cuando el dominio, los servicios y las operaciones principales estén suficientemente consolidados.

## Fase 9: Optimización

Optimizar consultas, cálculo de saldos, rendimiento general y experiencia de uso a medida que aumente el volumen de información.
