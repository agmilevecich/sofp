# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 05/09/2026

**Rama estable:** `main` → `a4be859`.
**Rama de trabajo:** `feature/swing-shell` → `4ae0a27`.

Último commit de la rama: `4ae0a27` — `test: cubrir formas de pago`.
Último cambio funcional/test previo: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

Desde `85b767c` se definió `FormaPago` y se agregó su cobertura de dominio. No se ha informado todavía la ejecución de `FormaPagoTest`.

La rama de trabajo continúa divergida respecto de `main`. No se realizó merge a `main`.

## Estado funcional

La Fase 8 continúa sobre el shell Swing integrado con cuentas, categorías, movimientos, inversiones y reportes. Los cambios conceptuales derivados del análisis de `ControlFinanzas` se adoptan como criterios de diseño para la evolución del Swing.

Criterio central acordado:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

El objetivo de UX es que el usuario registre hechos financieros desde paneles orientados al negocio. En particular, el próximo bloque será un panel **Gastos**, inspirado funcionalmente en la experiencia de `ControlFinanzas`, para registrar compras, pagos de servicios y otros egresos.

El flujo esperado es:

**Gastos → servicio específico → `Movimiento` de tipo `EGRESO` → historial consolidado de `Movimientos`.**

`Movimientos` debe funcionar como la historia financiera común y consolidada, no como una segunda fuente de verdad ni como una tabla paralela a los paneles especializados.

Los paneles especializados no deben crear núcleos financieros paralelos. Deben converger en `Movimiento` y reutilizar las reglas de negocio existentes.

Se adoptó además el criterio de distinguir **Cuenta** de **medio/forma de pago**. `FormaPago` ya está definida en el dominio con efectivo, transferencia, tarjeta de débito, tarjeta de crédito y QR, pero todavía no está integrada a `Movimiento`.

La tarjeta de crédito requiere tratamiento diferenciado: una compra puede generar una obligación/pasivo sin producir inmediatamente una salida de fondos de una cuenta bancaria.

El objetivo funcional de largo plazo es que SOFP pueda representar activos, pasivos y patrimonio neto, además de liquidez, ingresos, gastos, inversiones, préstamos y transferencias. Regla conceptual: `TOTAL ACTIVOS - TOTAL PASIVOS = PATRIMONIO NETO`.

Los préstamos otorgados deben representar un derecho de cobro: disminuyen la liquidez disponible pero no desaparecen del patrimonio.

Las transferencias entre cuentas propias no deben contabilizarse como ingreso ni gasto.

## Regla implementada — Fondos insuficientes

**Estado: completada y validada.**

Un `EGRESO` no puede superar el saldo disponible de la cuenta. Un egreso exactamente igual al saldo disponible está permitido y deja saldo cero.

La regla está implementada en `MovimientoService` y se aplica al registro público de movimientos y a las modificaciones de importe y tipo que puedan producir un saldo inválido. Al modificar un movimiento, el cálculo excluye correctamente el movimiento actual.

Producción: `5dd8372` — `fix: validar fondos disponibles en movimientos`.

## Regla implementada — Categorías con movimientos

**Estado: completada y validada.**

Una categoría referenciada por movimientos no se elimina físicamente. Se conserva el historial y se desactiva la categoría. La UI informa la situación de forma comprensible.

`CategoriaServiceTest`: **23/23**.

Producción/test: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.

## Estado Swing

Implementados y conectados: `MainFrame`, `HeaderPanel`, `SidebarPanel`, `InicioPanel`, `CuentasPanel`, `CategoriasPanel`, `MovimientosPanel`, `InversionesPanel`, `ReportesPanel`, `StatusBarPanel`, `RegistrarCuentaPanel`, `RegistrarMovimientoPanel` y `ui.Main`.

`MainFrame` usa `CardLayout` para Inicio, Cuentas, Categorías, Movimientos, Inversiones y Reportes. La UI integra los servicios existentes y no duplica reglas de negocio.

El formulario actual `RegistrarMovimientoPanel` permite registrar directamente movimientos y utiliza LGoodDatePicker para la fecha y `LocalTime.now()` para la hora.

El próximo avance debe complementar ese formulario con una experiencia especializada de **Gastos**, sin duplicar el núcleo financiero.

## FormaPago

`FormaPago` fue definida en `src/main/java/ar/com/agmilevecich/sofp/domain/FormaPago.java` mediante `927c66c` y su test fue agregado mediante `4ae0a27`.

El test `FormaPagoTest` todavía no tiene resultado de ejecución informado. Por lo tanto, no debe considerarse validado todavía.

## Validación vigente

Pruebas relevantes informadas por el usuario antes de los commits de `FormaPago`:

- `MovimientoFondosInsuficientesTest`: 6/6;
- `MovimientoServiceTest`: 57/57;
- `RegistrarMovimientoPanelTest`: 4/4;
- `RegistrarCuentaPanelTest`: 6/6;
- `CategoriaServiceTest`: **23/23**;
- suite general: **580/580**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.

Comando de suite: `mvn clean test`.
Duración: **10:58 min**.
Finalización: **05/09/2026 09:49:58 -03:00**.

No se deben asumir ejecuciones posteriores sin un nuevo resultado informado.

## Próximo paso

Implementar el primer corte funcional de **GastosPanel**, revisando previamente las clases Swing, `MovimientoService`, `Movimiento`, cuentas, categorías y tests relacionados.

El primer corte debe permitir registrar un egreso de negocio y reflejarlo en el historial común de `Movimientos`. `FormaPago` podrá incorporarse al flujo cuando su integración con el modelo financiero esté definida correctamente.

No se realizó merge a `main` y no se crean ramas nuevas salvo indicación explícita.
