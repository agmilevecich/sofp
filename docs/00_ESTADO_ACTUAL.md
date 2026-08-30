# SOFP — Estado actual

> Documento de continuidad. El código y los tests actuales son la fuente de verdad técnica.

## Estado verificado

**Rama estable:** `main`  
**HEAD actual verificado:** `7d6632f` — `docs: actualizar revision final de seguridad de perfil`  
**Fecha del estado:** 30/08/2026

La feature `feature/seguridad-perfil-financiero` fue integrada en `main` mediante **fast-forward**. La feature permanece como rama histórica y apunta al mismo commit `7d6632f`.

Los remotos `github/main` y `bitbucket/main` fueron alineados en `7d6632f` según la última verificación local informada por el usuario.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 20:00:23 -03:00**:

- Tests run: **486**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **15:50 min**

Esta es la validación global más reciente confirmada por el usuario y reemplaza a la anterior de 480 tests como referencia vigente.

## Seguridad de PerfilFinanciero

Implementado, probado e integrado:

- `PerfilFinancieroService` verifica el propietario antes de modificar un perfil;
- `cambiarDescripcion`, `activar` y `desactivar` requieren el `usuarioId` propietario;
- `perfilId` nulo → `NullPointerException`;
- `usuarioId` nulo → `NullPointerException`;
- perfil inexistente → `IllegalArgumentException`;
- usuario no propietario → `IllegalArgumentException`;
- usuario propietario → operación permitida;
- `PerfilFinancieroServiceTest`: **19/19 tests en verde**.

La autorización permanece en la capa de servicio porque coordina la relación entre `PerfilFinanciero` y `Usuario`.

Commits de la feature:

- `6478262` — `feat: proteger operaciones de perfil por propietario`;
- `8d3c775` — `test: cubrir autorizacion de operaciones de perfil`;
- `c07393e` — `docs: documentar seguridad de perfil financiero`;
- `b6194ae` — `docs: actualizar estado final de seguridad de perfil`;
- `b2fbcfb` — `docs: corregir conteo de commits de seguridad de perfil`;
- `7d6632f` — `docs: actualizar revision final de seguridad de perfil`.

## Funcionalidades cerradas e integradas

- operaciones financieras;
- identificación de activos mediante símbolo;
- cartera de activos;
- costo promedio de posición activa;
- valorización de posición activa;
- reportes de cartera;
- evolución histórica del saldo de una cuenta;
- seguridad de `PerfilFinanciero`.

## Repositorios y servicios relevantes

Repositorios JPA relevantes:

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

Servicios y componentes funcionales recientes incluyen `PerfilFinancieroService`, `CuentaService`, `CarteraActivoService`, `OperacionFinancieraService`, `PosicionActivoService` y los componentes de valorización y reportes ya integrados.

## Git y continuidad

La estrategia de trabajo mantiene dos remotos (`github` y `bitbucket`) para disponer de una segunda referencia y recuperación ante errores accidentales. `main` es estable y las nuevas funcionalidades se desarrollan en ramas propias.

Para integrar una feature cerrada se utiliza explícitamente **fast-forward** (`git merge --ff-only`), sin merge commit.

Antes de cualquier nuevo cambio:

1. reconstruir el estado desde GitHub;
2. revisar rama y últimos commits;
3. comparar con `main`;
4. revisar código, tests y reglas de negocio relacionadas;
5. hacer el cambio mínimo;
6. validar tests específicos y suite general cuando corresponda;
7. revisar `git diff`, `git diff --check` y `git status`;
8. actualizar esta documentación al cerrar etapas importantes.

## Próximo paso

No hay una feature pendiente de integración. El próximo trabajo debe definirse a partir del código real de `main`, revisando entidades, repositorios, servicios, tests y reglas de negocio antes de crear la siguiente rama de feature.

La interfaz gráfica continúa como evolución posterior, apoyándose sobre el backend ya estabilizado.
