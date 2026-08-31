# SOFP — Contexto para continuar con ChatGPT

Este archivo es el punto de entrada para continuar SOFP en una nueva conversación. La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

## Proyecto

SOFP — Sistema Operativo Financiero Personal.

Aplicación Java de finanzas personales con dominio, persistencia JPA, servicios y tests automatizados.

## Tecnologías

- Java / JDK 23.0.1
- IntelliJ IDEA Community 2026.1.1
- Maven
- Jakarta Persistence / JPA
- Hibernate 6.6.18.Final
- H2 2.3.232
- JUnit 5.11.4
- BigDecimal para dinero

## Estado actual — 31/08/2026

La rama estable es `main`. La rama de trabajo actual es `feature/seguridad-aislamiento-datos`.

Estado verificado contra GitHub:

- rama de trabajo: `feature/seguridad-aislamiento-datos`;
- último commit funcional/test: `c1f635f` — `test: adaptar MovimientoServiceTest al aislamiento por usuario`;
- comparación con `main`: **11 commits por delante, 0 por detrás**;
- `main` no fue modificado durante esta feature.

La documentación de continuidad fue actualizada después de la validación global de esta etapa.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **31/08/2026**, finalizada a las **12:46:20 -03:00**:

- **503 tests**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- Duración: **15:01 min**.

**503/503 tests en verde.**

## Seguridad y aislamiento — estado de la feature

La auditoría exploratoria transversal fue finalizada y sus hallazgos están siendo corregidos en esta rama.

Correcciones implementadas hasta ahora:

- `CuentaService`: autorización por propietario para operaciones mutables relevantes;
- `CategoriaService`: autorización por propietario para operaciones mutables relevantes;
- `MovimientoService`: autorización por propietario para operaciones mutables relevantes;
- `PosicionActivoService`: aislamiento de posición por `PerfilFinanciero`;
- `MovimientoActivoRepository`: consultas filtradas por perfil para soportar el aislamiento de posiciones.

Tests adaptados/cubiertos:

- `CuentaServiceTest`;
- `CategoriaServiceTest`;
- `MovimientoServiceTest`;
- `PosicionActivoServiceTest`.

La suite completa de 503 tests permanece verde.

## Hallazgos pendientes

La seguridad transversal todavía no está cerrada. Quedan por resolver/verificar:

1. autorización explícita del usuario solicitante en `OperacionFinancieraService`;
2. aislamiento de lecturas por ID y listados cuando representen casos de uso accesibles al usuario;
3. caminos alternativos de creación de movimientos que puedan eludir las reglas del servicio;
4. tests específicos restantes de lectura de recursos ajenos y autorización;
5. casos límite de activos compartidos entre perfiles.

## Features cerradas e integradas en `main`

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`;
- `feature/seguridad-perfil-financiero`.

## Dominio actual

Entidades principales conocidas:

- `Usuario`;
- `PerfilFinanciero`;
- `InstitucionFinanciera`;
- `Moneda`;
- `Cuenta`;
- `Categoria`;
- `Movimiento`;
- `OperacionFinanciera`;
- `Activo`;
- `Bono`.

Compra y venta de activos están implementadas y validadas. `PosicionActivo` calcula la posición a partir de movimientos persistidos y, en la rama actual, el servicio contextualiza la consulta por perfil.

## Persistencia

Repositorios relevantes:

- `UsuarioRepository`;
- `PerfilFinancieroRepository`;
- `InstitucionFinancieraRepository`;
- `MonedaRepository`;
- `CuentaRepository`;
- `MovimientoRepository`;
- `CategoriaRepository`;
- `OperacionFinancieraRepository`;
- `ActivoRepository`;
- `BonoRepository`;
- `MovimientoActivoRepository`.

## Git y continuidad

Se mantienen dos remotos (`github` y `bitbucket`) como referencia y recuperación ante errores accidentales.

`main` es estable. Las nuevas funcionalidades/correcciones se desarrollan en ramas propias. No modificar `main` directamente durante una feature.

Antes de cualquier nuevo cambio:

1. reconstruir el estado desde GitHub;
2. revisar rama y últimos commits;
3. comparar con `main`;
4. revisar código, tests y reglas de negocio relacionadas;
5. seleccionar el cambio mínimo;
6. ejecutar tests específicos y suite general cuando corresponda;
7. revisar `git diff`, `git diff --check` y `git status`;
8. actualizar documentación al cerrar etapas importantes.

## Próximo paso

Continuar `feature/seguridad-aislamiento-datos` con `OperacionFinancieraService` y luego revisar las lecturas y caminos alternativos de creación de movimientos.

No comenzar todavía la implementación Swing hasta cerrar la seguridad transversal y realizar la validación final contra `main`.

## Documentación de continuidad

- `docs/00_ESTADO_ACTUAL.md`;
- `docs/06_BUILDS.md`;
- `docs/07_TESTS.md`;
- `docs/08_PENDIENTES.md`;
- `docs/09_HISTORIAL_PROYECTO.md`;
- `docs/10_SEGURIDAD_PERFIL_FINANCIERO.md`;
- `docs/11_AUDITORIA_SEGURIDAD.md`;
- `docs/CHAT_CONTEXT.md`.

No registrar resultados de tests o builds que no hayan sido realmente ejecutados.
