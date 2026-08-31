# SOFP — Auditoría de seguridad y aislamiento de datos

## Estado

La auditoría transversal identificó y abordó los puntos de aislamiento por usuario/perfil en operaciones financieras, lecturas y altas.

## Correcciones implementadas

- `OperacionFinancieraService`: usuario solicitante obligatorio y validación de propiedad.
- `CuentaService`: lecturas, saldo, evolución, listados por perfil y alta protegidos por usuario.
- `CategoriaService`: lecturas, listados por perfil y alta protegidos por usuario.
- `MovimientoService`: lecturas por ID/cuenta/categoría y alta protegidas por usuario.
- `PerfilFinancieroService`: lectura por ID y alta protegidas por propietario.
- `PosicionActivoService`: acceso público contextualizado por usuario propietario del perfil.
- `CarteraActivoService`: posiciones, valorizaciones, reporte, composición y movimientos contextualizados por usuario.

Las APIs genéricas que podrían saltarse estas verificaciones quedaron internas al paquete cuando eran necesarias para compatibilidad de tests/coordinación interna.

## Cobertura

Se agregó `AislamientoDatosServiceTest` para cubrir recursos propios, recursos ajenos y caminos alternativos de creación.

## Validación

La última suite general informada antes de estos cambios fue **505/505 en verde**. Los cambios actuales todavía no tienen una nueva ejecución registrada.

## Cierre

La implementación de los hallazgos está completada a nivel de código, pero la auditoría no se marcará como cerrada hasta ejecutar y validar los tests sobre este estado, revisar diff/diff-check/status y comparar finalmente con `main`.
