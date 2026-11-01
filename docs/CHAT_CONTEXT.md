# SOFP — Contexto de continuidad

> Snapshot: 01/11/2026. Antes de cualquier cambio, reconstruir el estado actual desde GitHub.

## Proyecto
SOFP (Sistema Operativo Financiero Personal), Java Swing, JPA/Hibernate, H2, Maven y JUnit 5.

## Git
- Repositorio: `agmilevecich/sofp`.
- Rama activa: `feature/swing-shell`.
- Rama estable: `main`.
- SHA de main: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Último commit de código previo a docs: `afc66589c9d32988fa6e011fd0293e71fbf47c39` — `fix: refrescar obligaciones al registrar gastos`.
- Comparación antes de documentación: 457 ahead / 0 behind.
- Nunca modificar ni mergear main automáticamente.

## Últimos tests informados
- Suite `mvn clean test`: **1008/1008**, 0 failures/errors/skipped, `BUILD SUCCESS`, 01/11/2026 18:12:30 -03:00.
- Tests específicos `MainFrameTest,GastosPanelTest,ObligacionesPanelTest`: **18/18**, 01/11/2026 16:34:22 -03:00, `BUILD SUCCESS`.

## Último cambio
Commit `afc6658`, archivo `MainFrame.java`: se conecta un callback de `GastosPanel` a `ObligacionesPanel.refrescar()`. El usuario confirmó que los consumos con tarjeta aparecen inmediatamente en Obligaciones y Tarjetas sin cerrar/reabrir SOFP.

## Situación actual de tarjetas
El código inspeccionado distingue:
- saldo del ciclo seleccionado;
- saldo total de obligaciones;
- obligaciones de otros ciclos;
- obligaciones financiadas, refinanciadas o liquidadas.

La acción **Pago total** del panel de tarjetas cancela el ciclo seleccionado, no necesariamente toda la deuda histórica. No recomendar una operación sin verificar saldos y estados actuales.

## Punto exacto para continuar
La suite completa está aprobada, pero sigue pendiente la validación manual de pagos de tarjeta en la aplicación. El usuario temporalmente no puede adjuntar capturas; esperar a que pueda enviar una captura actual del panel Tarjetas y, si hace falta, Obligaciones. Los importes de capturas antiguas no son saldos actuales.

Al recibirlas:
1. reconstruir importes/ciclos/estados;
2. distinguir deuda del ciclo y deuda histórica;
3. comprobar cuenta pagadora y fondos;
4. indicar la operación adecuada sin duplicar movimientos;
5. verificar egreso, deuda restante y persistencia.

## Reglas de trabajo
Inspeccionar → entender → probar → corregir → volver a probar → documentar → commit.
Antes de modificar: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Cambios mínimos y commits pequeños. No asumir sincronización ni resultados de tests no informados.
