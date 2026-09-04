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

Se implementó en `MovimientoService` la regla financiera que impide registrar un `EGRESO` cuando el importe supera el saldo disponible de la cuenta. Un egreso exactamente igual al saldo está permitido y deja saldo cero.

La validación también se aplica a modificaciones de importe y tipo de movimiento cuando pueden producir un saldo inválido. Para modificaciones se excluye correctamente el movimiento actual del cálculo de fondos disponibles.

Producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

Cobertura específica: `MovimientoFondosInsuficientesTest` **6/6**.

Cobertura relacionada posterior al ajuste de fixture: `MovimientoServiceTest` **57/57** y `RegistrarMovimientoPanelTest` **4/4**.

Suite general posterior: `mvn clean test` — **577/577 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, duración **11:16 min**, finalizada el **04/09/2026 18:54:20 -03:00**.

El último commit del bloque es `2b2bf3e` — `test: adaptar cambio de tipo a fondos disponibles`, que ajusta el fixture de `MovimientoServiceTest.deberiaModificarTipoMovimiento` agregando un ingreso previo independiente. El ajuste mantiene la intención original del test y la hace compatible con la nueva regla de fondos disponibles.

## Bloques funcionales cerrados

### Movimientos

`RegistrarMovimientoPanel` se integró al flujo de movimientos con categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. La fecha utiliza LGoodDatePicker y la hora se obtiene automáticamente con `LocalTime.now()`.

Validación conocida: **57/57 tests en verde** en `MovimientoServiceTest` y **4/4** en `RegistrarMovimientoPanelTest`.

### Cuentas

`RegistrarCuentaPanel` permite tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. `CuentasPanel` refresca mediante callback.

### Categorías

`CategoriasPanel` permite registrar, modificar, activar/desactivar y eliminar categorías delegando reglas a `CategoriaService`.

Las validaciones conocidas incluyen `CategoriaServiceTest` **22/22** y pruebas UI/navegación. `70c2455` agregó el caso de rechazo de una categoría sin nombre.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos.

Validación conocida: **21/21 tests en verde** para UI/servicios del bloque.

## Criterios funcionales derivados de ControlFinanzas

ControlFinanzas se utiliza como referencia funcional, no como arquitectura para copiar.

El criterio acordado para SOFP es mantener un núcleo financiero basado en `Movimiento`, alimentado por paneles especializados y servicios específicos. Los futuros paneles podrán cubrir gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard.

También se distingue **Cuenta** de **Forma/Medio de pago**. Se prevén tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo. La tarjeta de crédito deberá poder representar una obligación sin salida inmediata de fondos de una cuenta bancaria.

Se prevé evolucionar el modelo para representar activos, pasivos y patrimonio neto, incluyendo préstamos otorgados como derechos de cobro y transferencias propias sin impacto en ingresos/gastos.

Estos criterios son de diseño y roadmap; no deben registrarse como funcionalidades implementadas hasta que existan código y tests.

## Próximos bloques

- tratamiento de categorías con movimientos asociados, evitando borrado físico del historial;
- integración de `FormaPago`;
- especialización de paneles sobre el núcleo común de movimientos;
- pasivos/obligaciones y patrimonio neto;
- análisis mensual/histórico, evolución patrimonial, vencimientos y dashboard, adaptados a SOFP.

## Estado Git

`feature/swing-shell` continúa siendo la rama de trabajo. Su último commit funcional/test conocido es `2b2bf3e`. No se modifica ni se integra `main` automáticamente. No se crean ramas nuevas.

La suite general más reciente conocida en esta rama es **577/577 en verde**. No asumir ejecuciones posteriores sin resultado informado.

Antes de cerrar un bloque: tests específicos, relacionados y suite general cuando corresponda; luego `git diff`, `git diff --check` y `git status`.
