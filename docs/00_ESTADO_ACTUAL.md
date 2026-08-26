# SOFP — Estado actual

> Documento de continuidad del proyecto. Debe actualizarse al cerrar cada bloque importante.

## Identidad del proyecto

**Nombre:** SOFP — Sistema Operativo Financiero Personal  
**Repositorio:** agmilevecich/sofp  
**Rama principal:** `main`  
**Rama única de trabajo y continuidad:** `feature/operacion-financiera`  

La documentación de continuidad se mantiene en `docs/` dentro de la misma rama de trabajo. Las ramas auxiliares creadas durante la implementación de `Bono` fueron eliminadas de los remotos.

## Estado funcional actual

**Bloque actual — Posición de activo — concepto de dominio y cálculo implementados y validados.**

Se incorporó `PosicionActivo` como concepto de dominio no persistente para representar la tenencia acumulada de un activo a partir de sus `MovimientoActivo`.

Se incorporó `CalculadorPosicionActivo` como componente de dominio responsable de construir una posición a partir de una colección ordenada de `MovimientoActivo`.

La arquitectura actual contempla:

```text
Activo
 └── Bono

OperacionFinanciera
 ├── Movimiento
 └── MovimientoActivo
        └── Activo

MovimientoActivo
      ↓
CalculadorPosicionActivo
      ↓
PosicionActivo
```

`PosicionActivo` no duplica información persistente: calcula y mantiene únicamente la cantidad acumulada durante el uso del objeto. La posición se obtiene sumando las compras y restando las ventas.

`CalculadorPosicionActivo` valida el activo y la colección de movimientos, respeta el orden recibido y delega en `PosicionActivo` la aplicación de cada movimiento. Rechaza movimientos de otro activo y secuencias que produzcan una posición negativa.

## Última validación — Build 054

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
- `CalculadorPosicionActivoTest`: **7/7**.

Suite general más reciente:

```text
Tests run: 393
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

- Ejecución: **26/08/2026 13:39:19 -03:00**.
- Duración: **24:00 min**.

Durante la validación de persistencia se detectó una diferencia de escala en `BigDecimal` al recuperar valores mediante JPA/H2. Se corrigieron las comparaciones de los tests para utilizar comparación numérica (`compareTo`), sin modificar código de producción.

La suite general mostró además el mensaje de Surefire sobre la finalización de la JVM fork después de `System.exit(0)`, pero la ejecución terminó correctamente con `BUILD SUCCESS`. No se modifica la configuración de Surefire en esta etapa.

## Git

- Rama única de trabajo y continuidad: `feature/operacion-financiera`.
- `main` permanece separado y no fue modificado.
- `a08972f` — `fix: corregir cierre de assertThrows en test de posicion`.
- `ae5c997` — `fix: corregir nombre de test de posicion de activo`.
- `c491ed6` — `test: agregar cobertura de CalculadorPosicionActivo`.
- `9985b55` — `feat: incorporar calculador de posicion de activo`.
- `169b2be` — `docs: registrar PosicionActivo en continuidad`.
- `796b256` — `test: agregar cobertura de PosicionActivo`.
- `c8b0057` — `feat: incorporar concepto de PosicionActivo`.

## Dominio construido

Entidades principales: `Usuario`, `PerfilFinanciero`, `InstitucionFinanciera`, `Moneda`, `Cuenta`, `Categoria`, `Movimiento`, `OperacionFinanciera`, `Activo`, `Bono` y `MovimientoActivo`.

`Activo` representa la base común para futuros instrumentos financieros y actualmente contiene `nombre` y `moneda` como datos propios del activo.

`Bono` es la primera especialización de `Activo` y actualmente no incorpora atributos financieros adicionales.

`MovimientoActivo` representa el efecto de una operación sobre la tenencia de un `Activo`, con tipo `COMPRA` o `VENTA`, cantidad y precio unitario. La cantidad se almacena como valor positivo; el tipo determina el efecto sobre la tenencia.

`OperacionFinanciera` permite agrupar el movimiento monetario y el movimiento específico del activo que forman parte de una misma operación.

`PosicionActivo` representa la cantidad acumulada de un activo a partir de sus movimientos. Una compra incrementa la posición y una venta la disminuye. Actualmente es un concepto de dominio no persistente.

`CalculadorPosicionActivo` construye una `PosicionActivo` desde una colección ordenada de `MovimientoActivo`, sin introducir persistencia ni una nueva entidad.

Enumeraciones principales: `TipoInstitucionFinanciera`, `TipoMoneda`, `TipoCuenta`, `TipoMovimiento` y `TipoMovimientoActivo`.

## Persistencia

Repositorios JPA: `UsuarioRepository`, `PerfilFinancieroRepository`, `InstitucionFinancieraRepository`, `MonedaRepository`, `CuentaRepository`, `MovimientoRepository`, `CategoriaRepository`, `OperacionFinancieraRepository`, `ActivoRepository`, `BonoRepository` y `MovimientoActivoRepository`.

`PosicionActivo` y `CalculadorPosicionActivo` no poseen repositorio ni tabla propia en esta etapa, deliberadamente.

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

`CalculadorPosicionActivo` es deliberadamente un componente de dominio, no una entidad persistente ni un servicio vacío. La obtención de movimientos desde persistencia podrá coordinarse mediante un servicio de aplicación cuando exista un caso de uso que lo requiera.

## Próximo paso

Revisar el modelo actual de inversiones y definir el siguiente caso de uso que necesite la posición de un activo. Antes de incorporar precio promedio, valuación, cotización, comisiones, resultado de la inversión u otras reglas financieras, definirlas explícitamente en el dominio.

Evaluar la necesidad de un servicio de aplicación únicamente cuando exista un flujo que deba coordinar repositorios y dominio.

Antes de implementar un nuevo componente, revisar el dominio, repositorios, servicios y tests relacionados y mantener el cambio mínimo necesario.

## Regla de continuidad

Cada bloque importante debe terminar con código funcionando, tests en verde, commit identificable cuando corresponda, actualización de documentación y registro del próximo paso. Código, tests y documentación de continuidad deben permanecer en la misma rama `feature/operacion-financiera`.
