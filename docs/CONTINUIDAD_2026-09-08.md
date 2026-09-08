# SOFP — Continuidad 2026-09-08

## Estado verificado

Este documento registra el corte de continuidad del proyecto al 08/09/2026. La fuente de verdad es el código, los tests y los commits actuales; esta documentación sirve para recuperar contexto y no reemplaza la verificación en GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

HEAD de la rama antes de iniciar esta actualización documental: `7f05cd1ef48576e0bc59a3828cd254e0bd4a9d9b` — `test: actualizar expectativas de gastos con crédito`.

Comparación con `main`: **326 commits por delante, 0 por detrás**. Merge-base: `a4be85913847200cb70976d5266d9cbba10b3100`. No se realizó merge.

## Últimos cambios

Los dos últimos cambios funcionales/correctivos fueron:

- `6c7d70a` — `fix: mantener compatibilidad de MainFrame sin ObligacionService`.
- `7f05cd1` — `test: actualizar expectativas de gastos con crédito`.

El primer cambio mantiene operativos constructores anteriores de `MainFrame` que no reciben `ObligacionService`. El segundo actualiza el test para reflejar que, sin servicio de obligaciones, una compra con tarjeta de crédito produce `IllegalStateException`.

## Estado funcional

La Fase 8 integra el shell Swing con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes mediante `MainFrame` y `CardLayout`.

Arquitectura acordada:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Gastos utiliza:

**`GastosPanel` → `GastoService` → `MovimientoService` → `Movimiento` `EGRESO` → `Movimientos`.**

`FormaPago` está integrada con cinco opciones: `EFECTIVO`, `TRANSFERENCIA`, `TARJETA_DEBITO`, `TARJETA_CREDITO` y `QR`.

## Obligaciones y tarjeta de crédito

El modelado básico de obligaciones ya está implementado. Existen:

- `EstadoObligacion`;
- `Obligacion`;
- `ObligacionRepository`;
- `ObligacionService`;
- persistencia JPA;
- pagos parciales y completos;
- estados `PENDIENTE`, `PARCIAL` y `PAGADA`;
- tests de dominio, JPA y servicio.

`GastoService`, cuando recibe `ObligacionService`, registra el movimiento de egreso y crea la obligación asociada. Sin `ObligacionService`, rechaza una compra con `TARJETA_CREDITO` con `IllegalStateException`.

Por lo tanto, **ya no está pendiente el modelado de obligaciones**. Lo pendiente es su interfaz Swing.

## Reglas financieras vigentes

- Un `EGRESO` superior al saldo disponible se rechaza.
- Un `EGRESO` igual al saldo disponible está permitido y deja saldo cero.
- Las modificaciones de importe y tipo respetan fondos disponibles.
- Categorías con movimientos se conservan y se desactivan en lugar de eliminarse físicamente.
- Cuenta y forma de pago son conceptos distintos.
- Una compra con tarjeta de crédito genera un egreso y una obligación; no simula un pago inmediato de la cuenta.
- Transferencias entre cuentas propias no son ingresos ni gastos y se modelan mediante `OperacionFinanciera`.

## Tests

### Suite general

El usuario ejecutó:

`mvn test`

Resultado el **08/09/2026 13:27:36 -03:00**:

- Tests run: **618**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración: **21:26 min**.

### Tests focalizados

Antes de la suite general:

`GastosPanelTest` + `MainFrameMovimientosTest` → **8/8**, sin fallos ni errores.

La primera suite general del bloque había presentado 1 failure y 2 errors. Las correcciones `6c7d70a` y `7f05cd1` dejaron posteriormente la suite en **618/618**.

### Validación Git local

Después de los tests, el usuario informó:

- `git diff`: limpio;
- `git diff --check`: sin errores;
- `git status`: working tree limpio;
- rama sincronizada con `github/feature/swing-shell`.

## Último resultado de tests conocido

**618/618 — BUILD SUCCESS.**

No debe asumirse ninguna ejecución posterior hasta que el usuario la informe o GitHub/CI la confirme.

## Próximo paso recomendado

El próximo bloque lógico es construir la **UI Swing de obligaciones y pagos**, empezando por revisar `MainFrame`, `SidebarPanel`, `ObligacionService`, `ObligacionRepository`, `Obligacion`, `GastosPanel` y sus tests.

Objetivos de esa etapa:

1. listar obligaciones del perfil/usuario autorizado;
2. mostrar importe original, saldo pendiente, estado y fecha de origen;
3. seleccionar una obligación;
4. registrar un pago mediante `ObligacionService`;
5. refrescar la información después del pago;
6. manejar errores de forma amigable;
7. agregar cobertura Swing sin duplicar reglas de dominio/servicio.

No modificar todavía hasta realizar la revisión completa del estado actual y de las clases relacionadas.

## Protocolo permanente de continuidad

Ante una nueva sesión:

1. revisar la rama de trabajo;
2. revisar últimos commits;
3. comparar con `main`;
4. revisar README y documentación de continuidad;
5. revisar archivos modificados recientemente;
6. revisar tests relacionados;
7. identificar último cambio, último test conocido y próximo paso.

Prioridad:

**código → tests → commits → `main` → documentación → conversaciones anteriores.**

No modificar `main`, no crear ramas nuevas salvo indicación explícita, no asumir sincronizaciones ni resultados de tests y no considerar implementada una funcionalidad solamente porque aparezca documentada.
