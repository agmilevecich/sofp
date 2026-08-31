# SOFP — Estado actual

> Documento de continuidad. El código y los tests actuales son la fuente de verdad técnica.

## Estado verificado

**Rama estable:** `main`  
**Último commit funcional de la feature de seguridad:** `7d6632f` — `docs: actualizar revision final de seguridad de perfil`  
**Último commit de documentación:** `f9469276` — `docs: cerrar auditoria de seguridad y registrar hallazgos`  
**Fecha del estado:** 31/08/2026

La feature `feature/seguridad-perfil-financiero` fue integrada en `main` mediante **fast-forward**, sin merge commit, y queda como rama histórica.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 20:00:23 -03:00**:

- Tests run: **486**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **15:50 min**

**486/486 tests en verde.** Esta es la validación global vigente y reemplaza a la anterior de 480 tests como referencia.

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

## Auditoría de seguridad

La auditoría transversal de seguridad y aislamiento de datos quedó **exploratoriamente finalizada** y está documentada en `docs/11_AUDITORIA_SEGURIDAD.md`.

Hallazgos confirmados pendientes de corrección:

- `CuentaService`: operaciones mutables sin autorización explícita del propietario;
- `CategoriaService`: operaciones mutables sin autorización uniforme del propietario;
- `MovimientoService`: modificaciones y eliminación sin autorización explícita del propietario;
- `OperacionFinancieraService`: autorización del usuario solicitante no demostrada;
- `PosicionActivoService`: cálculo por activo sin aislamiento explícito por perfil;
- lecturas por ID/listados: revisar cuáles cruzan la frontera hacia casos de uso y requieren aislamiento.

La corrección de estos hallazgos es la próxima etapa de trabajo. No se debe considerar cerrada la seguridad transversal hasta que las correcciones tengan tests específicos y validación global.

## Funcionalidades cerradas e integradas

- operaciones financieras;
- identificación de activos mediante símbolo;
- cartera de activos;
- costo promedio de posición activa;
- valorización de posición activa;
- reportes de cartera;
- evolución histórica del saldo de una cuenta;
- seguridad de `PerfilFinanciero`.

## Persistencia y servicios relevantes

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

Se mantienen dos remotos (`github` y `bitbucket`) como referencia y recuperación ante errores accidentales.

`main` es estable y las nuevas funcionalidades se desarrollan en ramas propias. La integración de features se realiza mediante `git merge --ff-only`, sin merge commit.

Antes de cualquier nuevo cambio:

1. reconstruir el estado desde GitHub;
2. revisar rama y últimos commits;
3. comparar con `main`;
4. revisar código, tests y reglas de negocio relacionadas;
5. hacer el cambio mínimo;
6. validar tests específicos y suite general cuando corresponda;
7. revisar `git diff`, `git diff --check` y `git status`;
8. actualizar la documentación al cerrar etapas importantes.

## Próximo paso

**Corregir los hallazgos de la auditoría de seguridad**, empezando por `PosicionActivoService` y luego aplicar un patrón uniforme de autorización/aislamiento a cuentas, categorías, movimientos y operaciones financieras. Cada corrección deberá validarse con tests específicos y luego con la suite completa.

La interfaz gráfica continúa como evolución posterior, apoyándose sobre el backend estabilizado.
