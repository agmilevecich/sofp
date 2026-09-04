# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Completado y validado:** 433/433, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

## Etapa — Seguridad y aislamiento por usuario

`feature/seguridad-aislamiento-datos` completó la auditoría transversal y fue integrada en `main` mediante fast-forward.

Validación final registrada: `AislamientoDatosServiceTest` **7/7** y suite general **512/512**, `BUILD SUCCESS`.

## Fase 8 — Interfaz Swing

`feature/swing-shell` desarrolló progresivamente el shell Swing y su integración con cuentas, categorías, movimientos, inversiones y reportes.

Componentes conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` utiliza `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes.

## Bloques validados

### Movimientos

`RegistrarMovimientoPanel` se integró al flujo de movimientos con categoría autorizada y activa, tipo, importe, fecha/hora, descripción y `usuarioId`. La fecha utiliza LGoodDatePicker y la hora se obtiene automáticamente con `LocalTime.now()`.

Validación conocida: **57/57 tests en verde**.

### Cuentas

`RegistrarCuentaPanel` permite tipo, institución financiera, moneda e identificador externo y delega a `CuentaService.registrar(cuenta, usuarioId)`. `CuentasPanel` refresca mediante callback.

### Categorías

`CategoriasPanel` permite registrar, modificar, activar/desactivar y eliminar categorías delegando reglas a `CategoriaService`.

Las validaciones conocidas incluyen `CategoriaServiceTest` **22/22** y pruebas UI/navegación. `70c2455` agregó cobertura del rechazo de una categoría sin nombre.

### Inversiones y reportes

`InversionesPanel` muestra posiciones filtradas por usuario/perfil. `ReportesPanel` utiliza `CarteraActivoService` para reportes de movimientos.

Validación conocida: **21/21 tests en verde** para UI/servicios del bloque.

## Criterios funcionales derivados de ControlFinanzas

ControlFinanzas se está utilizando como referencia de funcionalidades y soluciones, no como arquitectura para copiar.

El criterio acordado para SOFP es mantener un núcleo financiero basado en `Movimiento`, alimentado por paneles especializados y servicios específicos. Los futuros paneles podrán cubrir gastos, ingresos, transferencias, inversiones, préstamos/deudas, pagos de tarjeta, historial y dashboard.

También se acordó distinguir **Cuenta** de **Forma/Medio de pago**. Se prevén tarjeta de crédito, tarjeta de débito, QR, transferencia y efectivo. La tarjeta de crédito deberá poder representar una obligación sin salida inmediata de fondos de una cuenta bancaria.

Se prevé evolucionar el modelo para representar activos, pasivos y patrimonio neto, incluyendo préstamos otorgados como derechos de cobro y transferencias propias sin impacto en ingresos/gastos.

Estos criterios son de diseño y roadmap; no deben registrarse como funcionalidades implementadas hasta que existan código y tests.

## Próximos bloques

- control de fondos insuficientes para `EGRESO`;
- tratamiento de categorías con movimientos asociados, evitando borrado físico del historial;
- integración de `FormaPago`;
- especialización de paneles sobre el núcleo común de movimientos;
- pasivos/obligaciones y patrimonio neto;
- análisis mensual/histórico, evolución patrimonial, vencimientos y dashboard, adaptados a SOFP.

## Validación general conocida

Última suite general conocida: **568/568 tests en verde**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

No atribuir una nueva ejecución general sin resultado informado por el usuario.

## Estado Git

`feature/swing-shell` continúa siendo la rama de trabajo. No se modifica ni se integra `main` automáticamente. No se crean ramas nuevas.

Antes de cerrar un bloque: tests específicos, relacionados y suite general cuando corresponda; luego `git diff`, `git diff --check` y `git status`.
