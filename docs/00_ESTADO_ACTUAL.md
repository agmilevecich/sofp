# SOFP — Estado actual

> Documento de continuidad del proyecto. El código y los tests actuales son la fuente de verdad técnica.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Estado:** `main` contiene las etapas `operacion-financiera`, `identificacion-activo` y `cartera-activos` integradas y validadas.

## Estado funcional actual

El bloque de operaciones financieras quedó cerrado e integrado en `main`.

El bloque de identificación de activos mediante símbolo quedó cerrado e integrado en `main`.

El bloque de cartera de activos quedó cerrado e integrado en `main`.

`Activo` posee:
- `nombre`;
- `simbolo` obligatorio;
- `moneda` obligatoria.

El símbolo está definido como único en persistencia (`unique = true`). `Bono` hereda de `Activo` y utiliza el constructor identificable `nombre + simbolo + moneda`.

## Cartera de activos

Implementado y validado:

- listado de movimientos de activos por perfil financiero;
- agrupación de movimientos por activo;
- cálculo de posiciones mediante `CalculadorPosicionActivo`;
- exclusión de posiciones cuya cantidad final es cero;
- separación de movimientos entre perfiles financieros;
- consideración correcta de compras y ventas al consultar movimientos por perfil;
- cobertura específica mediante `CarteraActivoServiceTest`.

La feature `cartera-activos` fue integrada en `main` mediante fast-forward hasta el commit `e75136b`.

## Identificación y búsqueda

Implementado y validado:

- `ActivoRepository.buscarPorSimbolo(String)` devuelve `Optional<Activo>`;
- `BonoRepository.buscarPorSimbolo(String)` devuelve `Optional<Bono>`;
- ambos rechazan `null` mediante `NullPointerException`;
- los tests cubren la búsqueda por símbolo;
- la persistencia rechaza símbolos duplicados tanto para `Activo` como para `Bono`;
- la restricción de unicidad se verifica mediante tests específicos de repositorio.

## Última validación global conocida

Suite general ejecutada desde IntelliJ IDEA el **28/08/2026 17:47:36 -03:00**:

- Tests run: **446**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- Resultado: **BUILD SUCCESS**
- Duración: **12:34 min**

Además, `CarteraActivoServiceTest` fue ejecutado específicamente y resultó en **5/5 tests en verde**.

## Git

`main` contiene las etapas funcionales cerradas de operaciones financieras, identificación de activos y cartera de activos.

La feature `cartera-activos` quedó integrada mediante fast-forward; no se creó un commit de merge adicional.

Último commit de cierre documental de esta etapa: `docs: cerrar cartera de activos`.

## Persistencia

Repositorios JPA relevantes:

- `OperacionFinancieraRepository`
- `MovimientoActivoRepository`
- `ActivoRepository`
- `BonoRepository`

`MovimientoActivoRepository.listarPorPerfilFinanciero(Long)` contempla las cuentas de origen y destino mediante la consulta correspondiente para incluir compras y ventas.

## Etapas cerradas

- `feature/operacion-financiera`: integrada en `main`.
- `feature/identificacion-activo`: integrada en `main` mediante `0a554fb`.
- `feature/cartera-activos`: integrada en `main` mediante fast-forward hasta `e75136b`.

## Reglas de continuidad

Código y tests son la fuente de verdad técnica. La documentación resume el estado y debe actualizarse al cerrar bloques importantes.

No considerar implementado ningún pendiente hasta contar con código verificable y tests correspondientes.

## Próximo paso

Definir la próxima evolución funcional a partir del código y los casos de uso existentes. Antes de implementar, revisar las entidades, repositorios, servicios y tests actuales para elegir el cambio mínimo y mantener las reglas de negocio existentes.
