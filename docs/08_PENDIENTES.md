# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Estado actual

Las siguientes etapas están implementadas, validadas e integradas en `main` mediante fast-forward:

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`;
- `feature/seguridad-perfil-financiero`.

La última feature cerrada fue `feature/seguridad-perfil-financiero`.

## Validación global vigente

Suite completa ejecutada el **29/08/2026 20:00:23 -03:00**:

- **486/486 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **15:50 min**.

## Auditoría de seguridad

La auditoría exploratoria transversal quedó **FINALIZADA** y documentada en `docs/11_AUDITORIA_SEGURIDAD.md`.

Quedan como trabajo pendiente las correcciones de los hallazgos confirmados:

1. `CuentaService`: autorización del propietario en operaciones mutables.
2. `CategoriaService`: autorización del propietario en operaciones mutables.
3. `MovimientoService`: autorización del propietario en modificaciones y eliminación.
4. `OperacionFinancieraService`: autorización explícita del usuario solicitante.
5. `PosicionActivoService`: aislamiento de la posición por perfil financiero.
6. Revisión de lecturas por ID/listados para determinar y garantizar aislamiento cuando crucen la frontera hacia casos de uso.
7. Verificación de caminos alternativos de creación de movimientos.
8. Tests específicos de autorización, lectura de recursos ajenos y activos compartidos.

Estos pendientes son correcciones derivadas de la auditoría; no representan funcionalidades nuevas independientes.

## Seguridad de PerfilFinanciero

La autorización de las operaciones que modifican el perfil está implementada en `PerfilFinancieroService` y cubierta por **19/19 tests en verde**.

La feature está integrada en `main`; no constituye un pendiente.

## Pendientes funcionales

No existe actualmente una feature funcional pendiente de integración.

## Líneas de evolución posibles

Sujetas a revisión del código y decisión explícita antes de implementar:

- evolucionar la valorización desde un precio informado hacia una fuente de precios cuando exista un caso de uso concreto;
- definir reglas específicas de cada instrumento financiero antes de agregar atributos financieros adicionales;
- incorporar DTOs cuando los casos de uso y fronteras de aplicación lo requieran;
- ampliar reportes y cálculos derivados de movimientos;
- incorporar interfaz de usuario cuando dominio y casos de uso estén consolidados.

## Próximo paso

Crear una rama propia para corregir los hallazgos de seguridad, comenzando por `PosicionActivoService`, y luego aplicar el patrón de autorización/aislamiento al resto de recursos pertenecientes a perfiles. No modificar `main` directamente.

## Regla

No convertir una línea de evolución en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
