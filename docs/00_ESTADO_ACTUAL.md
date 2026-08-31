# SOFP — Estado actual

> Documento de continuidad. El código y los tests actuales son la fuente de verdad técnica.

## Estado verificado — 31/08/2026

**Rama de trabajo:** `feature/seguridad-aislamiento-datos`  
**Último commit funcional/test:** `c1f635f` — `test: adaptar MovimientoServiceTest al aislamiento por usuario`  
**Base:** `main`  
**Situación:** la rama estaba 11 commits por delante de `main` en `c1f635f`; posteriormente se agregaron únicamente commits documentales de continuidad.

La rama de trabajo contiene la corrección progresiva de los hallazgos de la auditoría de seguridad de aislamiento por perfil. `main` no fue modificado durante esta etapa.

## Validación global vigente

Suite completa ejecutada desde IntelliJ IDEA el **31/08/2026**, finalizada a las **12:46:20 -03:00**:

- Tests run: **503**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **15:01 min**

**503/503 tests en verde.** Esta es la validación global vigente y reemplaza a las anteriores de 486 y 480 tests.

## Seguridad y aislamiento por perfil

Durante esta feature se corrigieron y probaron los siguientes bloques:

- `CuentaService`: las operaciones mutables relevantes reciben `usuarioId` y verifican que el usuario sea propietario de la cuenta; se cubrieron las adaptaciones correspondientes en `CuentaServiceTest`.
- `CategoriaService`: las operaciones mutables relevantes reciben `usuarioId` y verifican el propietario; se corrigieron además problemas de persistencia/rollback detectados al adaptar los tests.
- `MovimientoService`: las operaciones mutables reciben `usuarioId` y verifican que el movimiento pertenezca al perfil autorizado; `MovimientoServiceTest` fue adaptado a las nuevas firmas y reglas.
- `PosicionActivoService`: la posición se obtiene mediante `activoId + perfilFinancieroId`, utilizando `MovimientoActivoRepository.listarPorActivoYPerfilFinanciero(...)` para impedir la mezcla de movimientos de distintos perfiles.
- `MovimientoActivoRepository`: se incorporaron consultas filtradas por perfil para soportar el aislamiento de posiciones y cartera.

Commits funcionales y de tests de esta etapa hasta la última validación:

- `e22f236` — `fix: autorizar operaciones mutables de cuenta`;
- `346f64c` — `test: adaptar CuentaServiceTest a autorización por propietario`;
- `98509e2` — `fix: autorizar operaciones mutables de categoria`;
- `f628337` — `fix: corregir rollback en CategoriaService`;
- `7a40f16` — `test: adaptar CategoriaServiceTest al aislamiento por usuario`;
- `cb745a3` — `fix: corregir persistencia de perfiles en CategoriaServiceTest`;
- `573f3a0` — `test: cubrir aislamiento de posicion de activo por perfil`;
- `18b9286` — `fix: autorizar operaciones mutables de movimiento`;
- `c1f635f` — `test: adaptar MovimientoServiceTest al aislamiento por usuario`.

Después de `c1f635f` se realizaron únicamente actualizaciones documentales de continuidad.

## Hallazgos todavía pendientes

La auditoría transversal todavía no está cerrada. Permanecen:

1. `OperacionFinancieraService`: incorporar/verificar autorización explícita del usuario solicitante en las operaciones protegidas.
2. Lecturas por ID y listados: revisar cuáles cruzan la frontera hacia casos de uso y requieren aislamiento por usuario/perfil.
3. Caminos alternativos de creación de movimientos: comprobar que no permitan eludir las reglas aplicadas por `MovimientoService`.
4. Tests específicos restantes de lectura de recursos ajenos y de otros escenarios de autorización.
5. Casos límite de activos compartidos entre perfiles.

La corrección de cuentas, categorías, movimientos y posiciones no implica que la auditoría transversal completa esté cerrada hasta resolver y validar los puntos restantes.

## Funcionalidades cerradas e integradas en `main`

- operaciones financieras;
- identificación de activos mediante símbolo;
- cartera de activos;
- costo promedio de posición activa;
- valorización de posición activa;
- reportes de cartera;
- evolución histórica del saldo de una cuenta;
- seguridad de `PerfilFinanciero`.

La interfaz Swing todavía no fue iniciada como etapa de implementación. Se mantiene como evolución posterior a la estabilización de dominio, servicios y seguridad.

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

Servicios relevantes incluyen `PerfilFinancieroService`, `CuentaService`, `CategoriaService`, `MovimientoService`, `OperacionFinancieraService`, `PosicionActivoService` y `CarteraActivoService`.

## Git y continuidad

Se mantienen dos remotos (`github` y `bitbucket`) como referencia y recuperación ante errores accidentales.

`main` es estable y las nuevas funcionalidades se desarrollan en ramas propias. No se debe modificar `main` directamente durante una feature.

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

Continuar la feature `feature/seguridad-aislamiento-datos` con los hallazgos restantes, comenzando por `OperacionFinancieraService` y la revisión de lecturas/paths alternativos. No pasar todavía a Swing sin decidir primero si la etapa de seguridad transversal queda cerrada con cobertura suficiente.

Al cerrar seguridad, realizar una validación final contra `main` y recién entonces preparar la siguiente etapa de interfaz Swing.
