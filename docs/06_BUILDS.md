# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Estado: COMPLETADO Y VALIDADO.**

Se ejecutó la suite general después de completar la cobertura de venta de activo, persistencia de relaciones e integración con posición.

Pruebas específicas previas:

- `OperacionFinancieraTest`: **17/17 tests en verde**.
- `OperacionFinancieraServiceTest`: **22/22 tests en verde**.
- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.

Suite general ejecutada el **27/08/2026 15:24:11 -03:00**:

- Tests run: **433**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **17:35 min**

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y fue integrada en `main` mediante fast-forward.

Validación final registrada: `AislamientoDatosServiceTest` **7/7** y suite general **512/512**, `BUILD SUCCESS`.

## Fase 8 — Interfaz Swing

`feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, categorías, movimientos, inversiones y reportes.

Componentes conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes.

## Bloque — Fondos insuficientes

**Estado: COMPLETADO Y VALIDADO.**

Se implementó en `MovimientoService` la regla financiera que impide registrar un `EGRESO` cuando el importe supera el saldo disponible de la cuenta. Un egreso exactamente igual al saldo disponible está permitido y deja saldo cero.

La validación también se aplica a modificaciones de importe y tipo de movimiento cuando pueden producir un saldo inválido. Para modificaciones se excluye correctamente el movimiento actual del cálculo de fondos disponibles.

Producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

Cobertura específica: `MovimientoFondosInsuficientesTest` **6/6**.

Cobertura relacionada: `MovimientoServiceTest` **57/57** y `RegistrarMovimientoPanelTest` **4/4**.

## Bloque — Categorías con movimientos

**Estado: COMPLETADO Y VALIDADO.**

Una categoría referenciada por movimientos no se elimina físicamente. Se conserva el historial y la categoría se desactiva. La UI informa la situación de forma amigable.

`CategoriaServiceTest`: **23/23 tests en verde**.

Se detectó además un problema de aislamiento de persistencia en la suite general: el fixture podía encontrar una moneda `ARS` ya existente. Se corrigió cerrando `JpaTestManager` antes de crear el `EntityManager` de cada test de `CategoriaServiceTest`.

Commit de corrección: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

## Suite general — 05/09/2026

Se ejecutó `mvn clean test` sobre `feature/swing-shell` después de la corrección de aislamiento.

Resultado:

- Tests run: **580**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **10:58 min**
- Finalización: **05/09/2026 09:49:58 -03:00**

Este es el resultado general vigente. No se debe atribuir esta ejecución a los commits posteriores de `FormaPago`.

## Bloques funcionales cerrados

### Movimientos

`RegistrarMovimientoPanel` se integró al flujo de movimientos con categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. La fecha utiliza LGoodDatePicker y la hora se obtiene automáticamente con `LocalTime.now()`.

Validación conocida: **57/57 tests en verde** en `MovimientoServiceTest` y **4/4** en `RegistrarMovimientoPanelTest`.

### Cuentas

`RegistrarCuentaPanel` permite tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. `CuentasPanel` refresca mediante callback.

### Categorías

`CategoriasPanel` permite registrar, modificar, activar/desactivar y eliminar categorías delegando reglas a `CategoriaService`.

La regla de categorías con movimientos está cerrada y validada con `CategoriaServiceTest` **23/23**.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos.

Validación conocida: **21/21 tests en verde** para UI/servicios del bloque.

## Evolución funcional del Swing

ControlFinanzas se utiliza como referencia funcional, no como arquitectura para copiar.

El criterio acordado para SOFP es mantener un núcleo financiero basado en `Movimiento`, alimentado por paneles especializados y servicios específicos.

La UX objetivo se concreta ahora en un panel **Gastos**, al estilo funcional de ControlFinanzas, donde el usuario pueda registrar compras, pagos de servicios y otros egresos. Esos registros deben terminar reflejados en la tabla/historial común de `Movimientos`.

El flujo esperado es:

**Gastos → servicio específico → `Movimiento` `EGRESO` → `Movimientos` como historial consolidado.**

Esto significa que `Movimientos` es la historia financiera consolidada, mientras que `Gastos` es una interfaz de carga especializada. No deben existir dos fuentes de verdad financieras.

## FormaPago

`FormaPago` fue definida mediante `927c66c` y su test mediante `4ae0a27`.

El test `FormaPagoTest` todavía no tiene resultado de ejecución informado. Por eso la funcionalidad no se considera validada y su integración con `Movimiento` queda pendiente.

## Próximos bloques

1. Primer corte funcional de `GastosPanel` para registrar egresos de negocio y reflejarlos en `Movimientos`.
2. Integración de `FormaPago` en el flujo de Gastos y en el modelo financiero cuando corresponda.
3. Pasivos/obligaciones y patrimonio neto.
4. Análisis mensual/histórico, evolución patrimonial, vencimientos y dashboard, adaptados a SOFP.

Antes de cerrar un bloque: tests específicos, relacionados y suite general cuando corresponda; luego `git diff`, `git diff --check` y `git status`.

## Estado Git — 05/09/2026

`feature/swing-shell` continúa como rama de trabajo y `main` permanece en `a4be859`. No se realizó merge a `main`.

Último commit de la rama: `4ae0a27` — `test: cubrir formas de pago`.

Último cambio funcional/test previo: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

No se debe considerar ejecutada `FormaPagoTest` hasta recibir un resultado explícito.
