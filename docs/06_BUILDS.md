# SOFP — Historial de Builds

## Build 053 — Incorporación de MovimientoActivo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó el modelo de movimientos específicos de activos para separar el efecto sobre la tenencia del efecto monetario de `Movimiento`.

Se incorporaron:

- `TipoMovimientoActivo` con `COMPRA` y `VENTA`.
- `MovimientoActivo` con referencia a `Activo`, tipo de movimiento, cantidad y precio unitario.
- `MovimientoActivoRepository`.
- Persistencia JPA de `MovimientoActivo` en la unidad utilizada por los tests.

La cantidad se almacena como valor positivo y el dominio determina su efecto sobre la tenencia: compra positiva y venta negativa.

Pruebas específicas:

- `MovimientoActivoTest`: **11/11 tests en verde**.
- `MovimientoActivoRepositoryTest`: **6/6 tests en verde**.
- Total de tests nuevos: **17**.

Durante la validación del repositorio se detectó una diferencia de escala en `BigDecimal` al recuperar valores mediante JPA/H2. Se corrigieron las comparaciones de los tests para utilizar comparación numérica, sin modificar el código de producción.

Suite general:

- Tests run: **370**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Finalización: **26/08/2026 10:57:39 -03:00**
- Duración: **24:34 min**

Commits principales:

- `4be2264` — `feat: agregar tipo de movimiento de activo`
- `51d3860` — `feat: incorporar movimiento de activo`
- `381f3a6` — `feat: incorporar repositorio de movimiento de activo`
- `7a54b20` — `test: agregar cobertura de MovimientoActivo`
- `eb57063` — `test: agregar cobertura de MovimientoActivoRepository`
- `d14d114` — `test: registrar MovimientoActivo en persistencia JPA`
- `a579d4e8` — `test: corregir comparacion decimal en MovimientoActivoRepositoryTest`

## Build 052 — Incorporación de Bono como especialización de Activo

**Estado: COMPLETADO Y VALIDADO.**

Se incorporó `Bono` como primera especialización de `Activo`, manteniendo deliberadamente una definición mínima y sin atributos financieros adicionales.

Pruebas específicas: `BonoTest` **5/5** y `BonoRepositoryTest` **6/6**.

Suite general: **353/353 tests en verde**, `BUILD SUCCESS`.

Ejecución general: **25/08/2026 20:14:39 -03:00**. Duración: **16:07 min**.

Commits del bloque: `589acf1`, `c11f1f4`, `6925447`, `74b8fff`, `678c6ea`.

## Builds anteriores

Los Builds 001–051 permanecen registrados en el historial previo de este documento.

## Estado actual

El último Build cerrado es **Build 053**. La última suite confirmada es **370/370 tests en verde**.

La documentación de continuidad se mantiene exclusivamente en `feature/operacion-financiera`.

## Próximo paso

Definir la siguiente evolución del bloque de inversiones a partir de reglas de negocio explícitas, manteniendo las especializaciones y movimientos actuales sin inventar atributos financieros aún no definidos.

## Regla de cierre

Cada Build debe quedar registrado con cambios principales, tests ejecutados, resultado, commit asociado cuando exista y próximo paso. La documentación debe mantenerse sincronizada con el código real y permanecer en la misma rama de trabajo.
