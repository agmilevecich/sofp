# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Estado actual — 31/08/2026

La rama de trabajo es `feature/seguridad-aislamiento-datos`.

Último commit funcional/test vigente:

- `b0e6377` — `test: cubrir aislamiento de datos por usuario`.
- `85a4e86` — `fix: autorizar operaciones financieras por usuario`.

La rama está sincronizada con los remotos `github` y `bitbucket`.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **31/08/2026**:

- **505/505 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

Este resultado es la última validación global informada para esta etapa.

## Seguridad: correcciones realizadas

Ya fueron implementadas y validadas correcciones para:

1. `CuentaService`: autorización de operaciones mutables por propietario.
2. `CategoriaService`: autorización de operaciones mutables por propietario.
3. `MovimientoService`: autorización de modificaciones y eliminación por propietario.
4. `PosicionActivoService`: aislamiento de la posición por perfil financiero.
5. `OperacionFinancieraService`: autorización explícita del usuario solicitante en las operaciones protegidas.
6. Tests de aislamiento y autorización en operaciones financieras, compra, venta y posiciones de activos.

## Seguridad: pendientes reales

La auditoría transversal todavía no está cerrada. Permanecen:

1. Lecturas por ID y listados: revisar cuáles son casos de uso expuestos y garantizar aislamiento de recursos pertenecientes a otros perfiles cuando corresponda.
2. Caminos alternativos de creación de movimientos: verificar que no permitan eludir las reglas de `MovimientoService`.
3. Completar tests de lectura de recursos ajenos y autorización donde el código actual todavía no tenga cobertura específica.
4. Revisar casos límite de activos compartidos entre perfiles y confirmar que las posiciones derivadas siempre respeten el perfil solicitado.

Estos puntos son correcciones de seguridad, no nuevas funcionalidades independientes.

## Funcionalidades cerradas e integradas en `main`

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`;
- `feature/seguridad-perfil-financiero`.

## Swing

La interfaz Swing todavía no debe considerarse iniciada. Queda como siguiente gran etapa después de cerrar la seguridad transversal y verificar nuevamente el estado del backend.

Antes de comenzar Swing se deberá:

- terminar los hallazgos de seguridad;
- ejecutar tests específicos;
- ejecutar la suite general;
- revisar diff, `git diff --check` y status;
- comparar la feature con `main`;
- dejar documentado el cierre de seguridad.

## Regla

No convertir una línea de evolución en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
