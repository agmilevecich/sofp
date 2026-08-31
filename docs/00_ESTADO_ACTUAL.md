# SOFP — Estado actual

> Documento de continuidad. El código y los tests actuales son la fuente de verdad técnica.

## Estado verificado — 31/08/2026

**Rama estable:** `main`  
**Último commit integrado:** `75d0a18` — `docs: actualizar contexto tras cierre de seguridad`  
**Feature integrada:** `feature/seguridad-aislamiento-datos` mediante fast-forward.  

**Rama de trabajo:** `feature/swing-shell`  
**Último commit:** `c6923eb` — `docs: documentar validacion del shell Swing`  
**Último commit funcional previo:** `0c41a50` — `feat: agregar punto de entrada para shell Swing`.

La rama de trabajo está sincronizada con GitHub y Bitbucket.

## Validación actual

Suite general ejecutada localmente el **31/08/2026**:

- Tests run: **515**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **22:17 min**

La validación global vigente es **515/515 tests en verde**.

`AislamientoDatosServiceTest`: **7/7 en verde**.

La primera ejecución de ese test tuvo 7 fallos por datos de prueba inválidos: el código de moneda generado excedía `VARCHAR(10)`. Se corrigió el fixture; la segunda ejecución quedó 7/7 en verde.

## Seguridad implementada

- `PerfilFinancieroService`: lecturas y alta protegidas por usuario propietario.
- `CuentaService`: lecturas por ID, listados por perfil, saldo, evolución y alta protegidos por usuario.
- `CategoriaService`: lectura por ID, listado por perfil y alta protegidos por usuario.
- `MovimientoService`: lectura por ID, listados por cuenta/categoría y alta protegidos por usuario.
- `PosicionActivoService`: consulta pública protegida por propietario del perfil.
- `CarteraActivoService`: posiciones, valorizaciones, reporte, composición y movimientos protegidos por propietario del perfil.
- `OperacionFinancieraService`: transferencia, compra y venta exigen usuario y validan propiedad de los recursos involucrados.
- Se cerraron caminos internos que podían permitir saltar validaciones públicas.
- `AislamientoDatosServiceTest` cubre recursos propios y ajenos y los principales caminos de lectura/creación.

## Estado de la interfaz — Fase 8

La primera etapa del shell Swing quedó implementada en `feature/swing-shell`.

Componentes actuales:

- `MainFrame` como ventana principal;
- `HeaderPanel`;
- `SidebarPanel` con cinco botones: Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- área central basada en `CardLayout`;
- tarjetas para Inicio, Cuentas, Movimientos e Inversiones;
- `StatusBarPanel`;
- punto de entrada `ar.com.agmilevecich.sofp.ui.Main`.

Tests específicos:

- `MainFrameTest`;
- `MainFrameLayoutTest`;
- `MainFrameNavigationTest`.

El shell fue probado manualmente y la ventana principal abre correctamente con navegación lateral y estado inicial visible.

## Próximo paso

Continuar la Fase 8 con el siguiente bloque funcional de la interfaz, partiendo del shell ya validado y conectando progresivamente la UI con los servicios existentes, sin duplicar lógica de negocio.
