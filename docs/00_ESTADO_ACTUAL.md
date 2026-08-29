# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama de trabajo actual:** `feature/reportes-cartera`  
**Estado:** la rama de trabajo contiene la evolución de reportes de cartera y evolución histórica de saldos. Está **23 commits adelante de `main` y 0 commits detrás**, según la comparación actual de GitHub.

## Último estado de la rama de trabajo

Último commit:

- `3c14bdf` — `docs: completar evolucion historica de saldos en roadmap`

Commits funcionales más recientes:

- `b11533b` — `feat: integrar evolucion historica de saldo en CuentaService`;
- `405cde6` — `feat: agregar punto de evolucion historica de saldo`;
- `d0854fd` — `feat: integrar reporte de movimientos en servicio de cartera`;
- `19fc563` — `feat: agregar detalle de movimientos de cartera`.

El árbol de trabajo local fue sincronizado con GitHub y Bitbucket y quedó limpio.

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

El bloque de operaciones financieras quedó cerrado e integrado en `main`.

El bloque de identificación de activos mediante símbolo quedó cerrado e integrado en `main`.

El bloque de cartera de activos quedó cerrado e integrado en `main`.

El bloque de costo promedio de posición activa quedó cerrado e integrado en `main`.

El bloque de valorización de posición activa quedó cerrado e integrado en `main`.

La rama actual `feature/reportes-cartera` agrega sobre ese estado:

- reporte de cartera de activos;
- composición valorizada de la cartera;
- detalle de movimientos de cartera;
- evolución histórica del saldo de una cuenta.

## Cartera de activos y reportes

Implementado y validado en la rama actual:

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

Implementado y validado en la rama actual:

- `EvolucionSaldoCuenta` representa un punto histórico compuesto por fecha/hora y saldo acumulado;
- `CuentaService.obtenerEvolucionSaldo(Long)` recorre los movimientos de la cuenta en orden cronológico;
- cada movimiento produce un punto con el saldo acumulado inmediatamente después de dicho movimiento;
- los ingresos incrementan el saldo;
- los egresos disminuyen el saldo;
- un identificador de cuenta `null` es rechazado mediante `NullPointerException`;
- cobertura específica mediante `CuentaServiceEvolucionSaldoTest` con **5/5 tests en verde**.

La implementación reutiliza el orden cronológico ya establecido por `MovimientoRepository.listarPorCuenta(Long)`, que ordena por `fechaHora` e `id`.

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
- `feature/valorizacion-posicion-activo`.

En desarrollo actual:

- `feature/reportes-cartera`.

La comparación actual de GitHub indica que `feature/reportes-cartera` está 23 commits adelante de `main` y 0 detrás. No hay commits de `main` pendientes de incorporar a esta rama.

## Últimos cambios de la feature actual

La secuencia reciente es:

- reporte de composición de cartera;
- reporte de movimientos de cartera;
- punto de evolución histórica de saldo;
- integración de evolución histórica de saldo en `CuentaService`;
- tests específicos de evolución histórica;
- actualización del roadmap.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

No modificar `main` mientras se trabaja sobre `feature/reportes-cartera`.

Antes de cualquier nuevo cambio se debe revisar nuevamente el estado real de GitHub, comparar la rama con `main`, revisar código relacionado y tests, y mantener el cambio mínimo.

## Próximo paso

La feature `feature/reportes-cartera` está funcionalmente validada con **480/480 tests en verde**. Antes del merge se debe realizar la validación final de la feature:

1. revisar commits y comparación contra `main`;
2. revisar el diff completo;
3. verificar `git diff --check`;
4. verificar `git status`;
5. confirmar que la documentación (`roadmap.md`, builds, tests y pendientes) refleje los **480 tests** y el cierre de la feature;
6. si todo está correcto, preparar el merge de `feature/reportes-cartera` a `main` sin realizarlo automáticamente.

La parte gráfica continúa como etapa posterior, apoyándose sobre servicios y reglas de dominio estabilizados.
