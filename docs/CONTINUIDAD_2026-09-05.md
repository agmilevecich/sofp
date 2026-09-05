# SOFP — Continuidad consolidada

## Estado verificado — 05/09/2026

La fuente de verdad técnica es el código, los tests y los commits actuales de GitHub. `docs/` es documentación auxiliar.

- Rama estable: `main` → `a4be859`.
- Rama de trabajo: `feature/swing-shell` → `4ae0a27`.
- Último commit: `4ae0a27` — `test: cubrir formas de pago`.
- Último cambio funcional/test previo: `85b767c` — `test: aislar persistencia en CategoriaServiceTest`.
- Suite general más reciente informada: **580/580 tests**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`.
- Comando: `mvn clean test`.
- Duración: **10:58 min**.
- Finalización informada: **05/09/2026 09:49:58 -03:00**.

La suite general corresponde al estado anterior a los commits de `FormaPago`. No se ha informado todavía una ejecución posterior.

## Últimos cambios

`CategoriaServiceTest` fue aislado explícitamente cerrando `JpaTestManager` antes de crear el `EntityManager` de cada test. Commit: `85b767c`.

Luego se agregó `FormaPago` al dominio mediante `927c66c` y se agregó `FormaPagoTest` mediante `4ae0a27`. El test aún no tiene resultado de ejecución informado.

## Estado funcional

La Fase 8 — Swing continúa implementada e integrada con cuentas, categorías, movimientos, inversiones y reportes. La UI mantiene las reglas de negocio en los servicios.

La regla de fondos insuficientes está implementada: un `EGRESO` no puede superar el saldo disponible; un egreso igual al saldo es válido. También se valida al modificar importe o tipo cuando corresponde.

La gestión de categorías con movimientos quedó implementada: no se elimina físicamente una categoría referenciada por movimientos; se conserva el historial mediante desactivación y la UI informa la situación de forma amigable.

## Decisión funcional actual del Swing

El usuario definió que la experiencia del Swing debe aproximarse funcionalmente a ControlFinanzas: debe existir un panel de **Gastos** donde se registren compras, pagos de servicios y otros egresos, y esos registros deben reflejarse en la tabla/historial común de `Movimientos`.

La arquitectura acordada es:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

El flujo esperado es:

**Gastos → servicio específico → `Movimiento` `EGRESO` → `Movimientos` como historial consolidado.**

`Movimientos` es la historia financiera común y consolidada. Los paneles especializados no deben crear una segunda fuente de verdad financiera.

## FormaPago

`FormaPago` ya está definida con efectivo, transferencia, tarjeta de débito, tarjeta de crédito y QR.

La distinción conceptual entre `Cuenta` y `FormaPago` queda vigente. La integración de `FormaPago` con `Movimiento` no debe hacerse de forma aislada: se incorporará dentro del flujo de Gastos cuando corresponda.

La tarjeta de crédito requiere posteriormente representar obligaciones/pasivos sin asumir una salida inmediata de fondos de una cuenta bancaria.

## Validación acumulada relevante

- `MovimientoFondosInsuficientesTest`: 6/6.
- `MovimientoServiceTest`: 57/57.
- `RegistrarMovimientoPanelTest`: 4/4.
- `RegistrarCuentaPanelTest`: 6/6.
- `CategoriaServiceTest`: 23/23.
- Suite general: **580/580**.

## Próximo paso

El próximo bloque funcional es el primer corte de **GastosPanel**.

Debe comenzar por el flujo básico de registrar un egreso de negocio y hacer que aparezca en el historial común de `Movimientos`. Antes de modificar código hay que reconstruir el estado real y revisar las clases Swing, `MovimientoService`, `Movimiento`, cuentas, categorías y tests relacionados.

Después de ese primer corte se podrá integrar `FormaPago` al flujo y avanzar hacia tarjetas de crédito, pasivos y el resto de paneles especializados.

No hacer merge automático a `main`. No crear ramas nuevas salvo indicación explícita.
