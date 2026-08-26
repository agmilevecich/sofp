# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. Las ramas auxiliares creadas durante la implementación de `Bono` fueron eliminadas de los remotos.

## Estado funcional actual

**Bloque actual — Operaciones financieras con movimientos de activos — implementado y validado.**

Se incorporó `MovimientoActivo` para representar el efecto de una operación sobre la tenencia de un activo, separado del efecto monetario representado por `Movimiento`.

`OperacionFinanciera` puede asociar movimientos monetarios y movimientos de activos. De esta forma, una operación de inversión puede conservar en una misma operación el efecto sobre el dinero y el efecto sobre la tenencia del activo.

La arquitectura actual contempla:

```text
Activo
 └── Bono

OperacionFinanciera
 ├── Movimiento
 └── MovimientoActivo
        └── Activo
```

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

Suite general más reciente:

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
- `589acf1` — `feat: incorporar entidad Bono`.
- `6925447` — `feat: incorporar repositorio de Bono`.
- `678c6ea` — `test: registrar Bono en persistencia JPA`.
- `4be2264` — `feat: agregar tipo de movimiento de activo`.
- `51d3860` — `feat: incorporar movimiento de activo`.
- `381f3a6` — `feat: incorporar repositorio de movimiento de activo`.
- `d14d114` — `test: registrar MovimientoActivo en persistencia JPA`.
- `a579d4e` — `test: corregir comparacion decimal en MovimientoActivoRepositoryTest`.
- `8a441ab` — `feat: asociar MovimientoActivo a OperacionFinanciera`.
- `ab51734` — `test: ampliar OperacionFinanciera con movimientos de activo`.
- `bbd3bbe` — `test: ampliar persistencia de OperacionFinanciera con MovimientoActivo`.
- `dcd6f19` — `test: corregir comparacion decimal en OperacionFinancieraRepositoryTest`.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo`, `Bono` y `MovimientoActivo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

`Bono` es la primera especialización de `Activo` y actualmente no incorpora atributos financieros adicionales.

`MovimientoActivo` representa el efecto de una operación sobre la tenencia de un `Activo`, con tipo `COMPRA` o `VENTA`, cantidad y precio unitario. La cantidad se almacena como valor positivo; el tipo determina el efecto sobre la tenencia.

`OperacionFinanciera` permite agrupar el movimiento monetario y el movimiento específico del activo que forman parte de una misma operación.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta`, `TipoMovimiento` y `TipoMovimientoActivo`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository`, `BonoRepository` y `MovimientoActivoRepository`.

Las entidades nuevas están registradas explícitamente en la unidad de persistencia JPA utilizada por los tests.

## Services

La capa `service` contiene `CuentaService`, `MovimientoService`, `CategoriaService`, `PerfilFinancieroService`, `UsuarioService`, `InstitucionFinancieraService`, `MonedaService` y `OperacionFinancieraService`.

Los `ServiceTest` se incorporarán o ampliarán cuando exista lógica de aplicación que deba probarse; no se crean servicios vacíos únicamente para replicar los repositorios.

## Decisiones de dominio relevantes

Las transferencias no se modelan como un tercer `TipoMovimiento`. Una transferencia genera un **EGRESO** en la cuenta origen y un **INGRESO** en la cuenta destino, vinculados mediante una `OperacionFinanciera`.

Una `OperacionFinanciera` puede contener como máximo dos movimientos y cada `Movimiento` puede estar asociado a una única `OperacionFinanciera`.

`Activo` se mantiene deliberadamente como una entidad mínima. Cantidad, precio, cotización y posición se reservan para los bloques de operaciones y posiciones de inversión.

`Bono` se mantiene deliberadamente como una especialización mínima de `Activo`. No se incorporan todavía valor nominal, tasa, vencimiento, cupón, amortización, emisor u otros atributos financieros porque esas reglas de dominio aún no fueron definidas explícitamente.

`MovimientoActivo` almacena una cantidad positiva y utiliza `COMPRA` o `VENTA` para determinar el signo de la variación de tenencia. La posición acumulada no se almacena todavía como entidad independiente.

## Próximo paso

Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas, especialmente el concepto de **posición de activo** y cómo se obtiene a partir de `MovimientoActivo` sin duplicar información ni generar inconsistencias.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
