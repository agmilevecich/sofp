# SOFP — Pendientes

Este documento contiene únicamente trabajo pendiente o por decidir.

## Estado actual — 31/08/2026

La rama de trabajo es `feature/seguridad-aislamiento-datos`.

Último commit funcional/test antes de esta actualización documental:

- `c1f635f` — `test: adaptar MovimientoServiceTest al aislamiento por usuario`.

La rama está 11 commits por delante de `main` y 0 por detrás.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **31/08/2026**, finalizada a las **12:46:20 -03:00**:

- **503/503 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **15:01 min**.

## Seguridad: correcciones realizadas

Ya fueron implementadas y validadas correcciones para:

1. `CuentaService`: autorización de operaciones mutables por propietario.
2. `CategoriaService`: autorización de operaciones mutables por propietario.
3. `MovimientoService`: autorización de modificaciones y eliminación por propietario.
4. `PosicionActivoService`: aislamiento de la posición por perfil financiero mediante consultas filtradas.

Los tests correspondientes fueron adaptados y la suite completa permanece en verde.

## Seguridad: pendientes reales

La auditoría transversal todavía no está cerrada. Permanecen:

1. `OperacionFinancieraService`: incorporar/verificar autorización explícita del usuario solicitante en las operaciones protegidas.
2. Lecturas por ID y listados: revisar cuáles son casos de uso expuestos y garantizar aislamiento de recursos pertenecientes a otros perfiles cuando corresponda.
3. Caminos alternativos de creación de movimientos: verificar que no permitan eludir las reglas de `MovimientoService`.
4. Completar tests de autorización y lectura de recursos ajenos donde el código actual todavía no tenga cobertura específica.
5. Revisar casos límite de activos compartidos entre perfiles y confirmar que las posiciones derivadas siempre respeten el perfil solicitado.

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

La interfaz Swing todavía no debe considerarse iniciada. Queda como siguiente gran etapa **después de cerrar la seguridad transversal** y verificar nuevamente el estado del backend.

Antes de comenzar Swing se deberá:

- terminar los hallazgos de seguridad;
- ejecutar tests específicos;
- ejecutar la suite general;
- revisar diff, `git diff --check` y status;
- comparar la feature con `main`;
- dejar documentado el cierre de seguridad.

## Regla

No convertir una línea de evolución en trabajo realizado hasta que exista implementación verificable y tests correspondientes.
