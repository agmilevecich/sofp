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

## Validaciones posteriores al Build 059

Se incorporaron y validaron identificación por símbolo, cartera de activos, costo promedio, valorización, reportes, evolución histórica y seguridad de perfil financiero.

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y posteriormente fue integrada en `main` mediante fast-forward.

Se implementaron y validaron:

- autorización de operaciones mutables de `CuentaService`;
- autorización de operaciones mutables de `CategoriaService`;
- autorización de operaciones mutables de `MovimientoService`;
- autorización de `OperacionFinancieraService` por usuario solicitante;
- lecturas por ID y listados protegidos por propietario;
- cálculo de saldo y evolución contextualizados por usuario;
- altas de cuentas, categorías, movimientos y perfiles con validación de propietario;
- aislamiento de posiciones y cartera por usuario/perfil;
- cierre de caminos internos que podían saltar las validaciones públicas;
- cobertura transversal mediante `AislamientoDatosServiceTest`.

## Validación final de seguridad

El **31/08/2026** se ejecutó primero `AislamientoDatosServiceTest`: **7/7 tests en verde**.

Luego se ejecutó la suite general:

- Tests run: **512**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:25 min**

## Fase 8 — Interfaz de usuario Swing

El bloque inicial del shell Swing quedó implementado sobre `feature/swing-shell` y posteriormente se completó su integración con cuentas, movimientos e inversiones.

Se incorporaron:

- `MainFrame` como ventana principal;
- `HeaderPanel`;
- `SidebarPanel` con navegación por Inicio, Cuentas, Movimientos, Inversiones y Reportes;
- área central con tarjetas para Inicio, Cuentas, Movimientos e Inversiones;
- `StatusBarPanel`;
- punto de entrada `ui.Main`;
- integración de cuentas por perfil/usuario;
- integración de movimientos por cuenta/usuario;
- integración de posiciones de inversión por perfil/usuario;
- pruebas de paneles, estructura, layout, navegación e integración.

## Validación final del shell Swing

Suite general ejecutada localmente el **01/09/2026** después de limpiar artefactos compilados obsoletos:

- Tests run: **525**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **32:12 min**

La validación global vigente es **525/525 tests en verde**.

La ejecución inmediatamente anterior había detectado un `NoClassDefFoundError` de `SwingApplicationTest` debido a una clase compilada obsoleta en `target`. Se ejecutó una limpieza de Maven y la suite completa quedó verde. No fue necesario modificar código ni tests para resolver ese problema de entorno de compilación.

## Estado actual

La seguridad transversal está cerrada e integrada en `main`.

La rama `feature/swing-shell` contiene el shell Swing integrado con los servicios existentes y está **46 commits por delante de `main` y 0 por detrás**.

Último commit de la rama: `ca70f28` — `fix: conservar entrada Swing existente en ui`.

## Próximo paso

La feature `feature/swing-shell` queda validada dentro de su alcance actual. El siguiente trabajo de Fase 8 debe comenzar como un nuevo bloque funcional, sin merge automático a `main` y manteniendo cambios pequeños, tests específicos y sin duplicar lógica de negocio.
