# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo histórica recientemente cerrada:** `feature/reportes-cartera`  
**Estado actual:** `feature/reportes-cartera` fue integrada en `main` mediante fast-forward. `main` y la feature apuntan al mismo commit `0b73e87`.

## Último estado verificado

Último commit funcional/documental:

- `0b73e87` — `docs: cerrar pendientes funcionales de reportes de cartera`

Comparación verificada en GitHub:

- `main` = `0b73e87`;
- `feature/reportes-cartera` = `0b73e87`;
- diferencia: **0 commits adelante / 0 commits detrás**;
- las ramas son idénticas.

El árbol local informado por el usuario quedó limpio y sincronizado:

- `git syncsofp` → sin cambios;
- `git status` → working tree clean;
- `git diff --check` → sin observaciones.

## Validación global más reciente

Suite completa ejecutada desde IntelliJ IDEA el **29/08/2026 13:29:56 -03:00**:

- Tests run: **480**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **12:23 min**

Esta es la validación global más reciente confirmada por el usuario y debe considerarse la validación vigente.

## Estado funcional actual

Los siguientes bloques quedaron cerrados e integrados en `main`:

- operaciones financieras;
- identificación de activos mediante símbolo;
- cartera de activos;
- costo promedio de posición activa;
- valorización de posición activa;
- reportes de cartera;
- evolución histórica del saldo de una cuenta.

El bloque de reportes incluye:

- reporte de cartera de activos;
- composición valorizada de la cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta.

## Cartera de activos y reportes

Implementado y validado:

- listado de movimientos de activos por perfil financiero;
- agrupación de movimientos por activo;
- cálculo de posiciones mediante `CalculadorPosicionActivo`;
- exclusión de posiciones cuya cantidad final es cero;
- separación de movimientos entre perfiles financieros;
- consideración correcta de compras y ventas al consultar movimientos por perfil;
- `ReporteCarteraActivo` para representar la cartera valorizada;
- `DetalleComposicionCarteraActivo` para representar la composición;
- `DetalleMovimientoCarteraActivo` para representar movimientos;
- integración de estos reportes en `CarteraActivoService`;
- cobertura específica de dominio y servicio.

## Evolución histórica de saldo

Implementado y validado:

- `EvolucionSaldoCuenta` representa un punto histórico compuesto por fecha/hora y saldo acumulado;
- `CuentaService.obtenerEvolucionSaldo(Long)` recorre los movimientos de la cuenta en orden cronológico;
- cada movimiento produce un punto con el saldo acumulado inmediatamente después de dicho movimiento;
- los ingresos incrementan el saldo;
- los egresos disminuyen el saldo;
- un identificador de cuenta `null` es rechazado mediante `NullPointerException`;
- cobertura específica mediante `CuentaServiceEvolucionSaldoTest` con **5/5 tests en verde**.

La implementación reutiliza el orden cronológico establecido por `MovimientoRepository.listarPorCuenta(Long)`, que ordena por `fechaHora` e `id`.

## Cuenta y saldo

`CuentaService.calcularSaldo(Long)` mantiene el cálculo actual del saldo acumulando ingresos y restando egresos.

La cobertura existente incluye:

- cuenta sin movimientos;
- ingreso;
- egreso;
- múltiples movimientos;
- rechazo de identificador nulo.

No se modificó la regla existente de cálculo de saldo.

## Costo promedio de posición activa

Implementado y validado:

- acumulación del costo de adquisición de las compras;
- cálculo del precio promedio de la posición;
- mantenimiento del costo de adquisición remanente después de ventas;
- reinicio del costo de adquisición al cerrar completamente la posición;
- rechazo de ventas superiores a la cantidad disponible;
- rechazo de movimientos de otro activo;
- rechazo de movimientos nulos;
- cobertura específica mediante `PosicionActivoTest` con **8/8 tests en verde**.

## Valorización de posición activa

Implementado y validado:

- cálculo del valor actual de una posición a partir de un precio informado;
- cálculo de ganancia o pérdida respecto del costo de adquisición;
- cálculo del rendimiento porcentual;
- valorización de posiciones cerradas;
- aceptación de precio actual cero;
- rechazo de posición nula;
- rechazo de precio actual nulo;
- rechazo de precio actual negativo;
- rendimiento cero cuando no existe costo de adquisición;
- cobertura específica mediante `ValorizacionPosicionActivoTest` con **8/8 tests en verde**.

## Identificación y búsqueda

Implementado y validado:

- `ActivoRepository.buscarPorSimbolo(String)` devuelve `Optional<Activo>`;
- `BonoRepository.buscarPorSimbolo(String)` devuelve `Optional<Bono>`;
- ambos rechazan `null` mediante `NullPointerException`;
- los tests cubren la búsqueda por símbolo;
- la persistencia rechaza símbolos duplicados tanto para `Activo` como para `Bono`;
- la restricción de unicidad se verifica mediante tests específicos de repositorio.

## Persistencia

Repositorios JPA relevantes:

- `OperacionFinancieraRepository`;
- `MovimientoActivoRepository`;
- `ActivoRepository`;
- `BonoRepository`;
- `MovimientoRepository`;
- `CuentaRepository`.

`MovimientoRepository.listarPorCuenta(Long)` ordena los movimientos por `fechaHora` e `id`, permitiendo construir la evolución histórica del saldo en orden determinista.

`MovimientoActivoRepository.listarPorPerfilFinanciero(Long)` contempla las cuentas de origen y destino mediante la consulta correspondiente para incluir compras y ventas.

## Etapas cerradas e integración

Integradas en `main`:

- `feature/operacion-financiera`;
- `feature/identificacion-activo`;
- `feature/cartera-activos`;
- `feature/costo-promedio-activo`;
- `feature/valorizacion-posicion-activo`;
- `feature/reportes-cartera`.

`feature/reportes-cartera` queda como **etapa cerrada**, no como rama activa de desarrollo. Su estado final coincide con `main` en `0b73e87`.

## Últimos cambios de la feature cerrada

La secuencia funcional reciente fue:

- reporte de composición de cartera;
- reporte de movimientos de cartera;
- punto de evolución histórica de saldo;
- integración de evolución histórica de saldo en `CuentaService`;
- tests específicos de evolución histórica;
- actualización de documentación;
- cierre documental de la feature.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

Antes de cualquier nuevo cambio se debe revisar nuevamente el estado real de GitHub, comparar la rama de trabajo con `main`, revisar código relacionado y tests, y mantener el cambio mínimo.

`main` es la rama estable. Una nueva feature debe desarrollarse en su propia rama y no debe modificar `main` directamente.

## Próximo paso

No existe actualmente una feature funcional pendiente de integración.

El próximo trabajo debe definirse a partir del estado real de `main`, revisando código, entidades, repositorios, servicios, tests y reglas de negocio para seleccionar la siguiente evolución funcional mínima.

La parte gráfica continúa como etapa posterior, apoyándose sobre servicios y reglas de dominio estabilizados.
