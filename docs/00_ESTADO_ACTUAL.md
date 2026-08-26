# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. Las ramas auxiliares creadas durante la implementación de `Bono` fueron eliminadas de los remotos.

## Estado funcional actual

**Bloque actual — Posición de activo — concepto de dominio implementado y validado.**

Se incorporó `PosicionActivo` como concepto de dominio no persistente para representar la tenencia acumulada de un activo a partir de sus `MovimientoActivo`.

La arquitectura actual contempla:

```text
Activo
 └── Bono

OperacionFinanciera
 ├── Movimiento
 └── MovimientoActivo
        └── Activo

PosicionActivo
 └── acumula MovimientoActivo
```

`PosicionActivo` no duplica información persistente: calcula y mantiene únicamente la cantidad acumulada durante el uso del objeto. La posición se obtiene sumando las compras y restando las ventas.

## Última validación

Los tests específicos del bloque se encuentran en verde:

- `ActivoTest`: verde.
- `ActivoRepositoryTest`: **6/6**.
- `BonoTest`: **5/5**.
- `BonoRepositoryTest`: **6/6**.
- `MovimientoActivoTest`: **11/11**.
- `MovimientoActivoRepositoryTest`: **6/6**.
- `OperacionFinancieraTest`: **20/20**.
- `OperacionFinancieraRepositoryTest`: **12/12**.
- `PosicionActivoTest`: **8/8**.

Suite general más reciente conocida:

```text
Tests run: 370
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

- Ejecución: **26/08/2026 10:57:39 -03:00**.
- Duración: **24:34 min**.

Durante la validación de persistencia se detectó una diferencia de escala en `BigDecimal` al recuperar valores mediante JPA/H2. Se corrigieron las comparaciones de los tests para utilizar comparación numérica (`compareTo`), sin modificar código de producción.

## Git

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- `main` permanece separado y no fue modificado.
- `c8b0057` — `feat: incorporar concepto de PosicionActivo`.
- `796b256` — `test: agregar cobertura de PosicionActivo`.
- `d2e2c2a` — `docs: actualizar continuidad al cierre de operaciones con activos`.
- `dcd6f19` — `test: corregir comparacion decimal en OperacionFinancieraRepositoryTest`.
- `bbd3bbe` — `test: ampliar persistencia de OperacionFinanciera con MovimientoActivo`.
- `ab51734` — `test: ampliar OperacionFinanciera con movimientos de activo`.
- `8a441ab` — `feat: asociar MovimientoActivo a OperacionFinanciera`.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo`, `Bono` y `MovimientoActivo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

`Bono` es la primera especialización de `Activo` y actualmente no incorpora atributos financieros adicionales.

`MovimientoActivo` representa el efecto de una operación sobre la tenencia de un `Activo`, con tipo `COMPRA` o `VENTA`, cantidad y precio unitario. La cantidad se almacena como valor positivo; el tipo determina el efecto sobre la tenencia.

`OperacionFinanciera` permite agrupar el movimiento monetario y el movimiento específico del activo que forman parte de una misma operación.

`PosicionActivo` representa la cantidad acumulada de un activo a partir de sus movimientos. Una compra incrementa la posición y una venta la disminuye. Actualmente es un concepto de dominio no persistente.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta`, `TipoMovimiento` y `TipoMovimientoActivo`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository`, `BonoRepository` y `MovimientoActivoRepository`.

`PosicionActivo` no posee repositorio ni tabla propia en esta etapa, deliberadamente.

Las entidades nuevas están registradas explícitamente en la unidad de persistencia JPA utilizada por los tests.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService` y `OperacionFinancieraService`.

Los `ServiceTest` se incorporarán o ampliarán cuando exista lógica de aplicación que deba probarse; no se crean servicios vacíos únicamente para replicar los repositorios.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservan para los bloques de operaciones y posiciones de inversión.

`Bono` se mantiene deliberadamente como una especialización mínima de `Activo`. No se incorporan todavía valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros atributos financieros porque esas reglas de dominio aún no fueron definidas explícitamente.

`MovimientoActivo` almacena una cantidad positiva y utiliza `COMPRA` o `VENTA` para determinar el signo de la variación de tenencia.

`PosicionActivo` no permite que la cantidad acumulada resulte negativa. Una venta superior a la tenencia se rechaza en el dominio, dejando abierta para una futura decisión explícita la incorporación de posiciones short.

## Próximo paso

Definir cómo `PosicionActivo` se obtiene y utiliza a partir de las operaciones financieras existentes, sin convertirla todavía en una entidad persistente. Antes de incorporar precio promedio, valuación, cotización, comisiones u otras reglas de inversión, definirlas explícitamente en el dominio.

Evaluar también si la posición debe construirse desde una colección de `MovimientoActivo` o mediante un servicio de dominio/aplicación que coordine los movimientos existentes.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
