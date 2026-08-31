# SOFP — Auditoría de seguridad y aislamiento de datos

## Estado

**AUDITORÍA TRANSVERSAL COMPLETADA Y VALIDADA.**

La auditoría abordó los puntos de aislamiento por usuario/perfil en operaciones financieras, lecturas, listados y caminos alternativos de creación.

## Correcciones implementadas

- `OperacionFinancieraService`: usuario solicitante obligatorio y validación de propiedad.
- `CuentaService`: lecturas, saldo, evolución, listados por perfil y alta protegidos por usuario.
- `CategoriaService`: lecturas, listados por perfil y alta protegidos por usuario.
- `MovimientoService`: lecturas por ID/cuenta/categoría y alta protegidas por usuario.
- `PerfilFinancieroService`: lectura por ID y alta protegidas por propietario.
- `PosicionActivoService`: acceso contextualizado por usuario propietario del perfil.
- `CarteraActivoService`: posiciones, valorizaciones, reporte, composición y movimientos contextualizados por usuario.
- Se cerraron caminos internos que podían permitir saltar las validaciones públicas de autorización.

## Cobertura

Se agregó `AislamientoDatosServiceTest` para cubrir recursos propios, recursos ajenos, lecturas por ID, listados, altas y caminos alternativos de acceso/creación.

Validación específica final:

- `AislamientoDatosServiceTest`: **7/7 en verde**.

## Validación general

Suite completa ejecutada localmente el **31/08/2026**:

- Tests run: **512**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:25 min**

La implementación actual queda validada con **512/512 tests en verde**.

## Incidencia durante la validación

La primera ejecución de `AislamientoDatosServiceTest` presentó 7 fallos durante `setUp()` porque el fixture generaba códigos de moneda de 17 caracteres para una columna limitada a 10. Se corrigió exclusivamente el dato de prueba y la segunda ejecución quedó 7/7 en verde. No se modificaron reglas de negocio por esta incidencia.

## Cierre técnico pendiente

La auditoría funcional y la validación de tests están completas. Para cerrar formalmente la etapa todavía corresponde verificar en la copia local:

- `git status` limpio;
- `git diff --check` sin salida;
- comparación final de la feature contra `main`;
- sincronización de los commits documentales con GitHub y Bitbucket.

No hacer merge a `main` automáticamente.
