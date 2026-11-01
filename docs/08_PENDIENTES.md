# SOFP — Pendientes y próximos pasos

> Actualizado: 01/11/2026. El código y los tests de GitHub prevalecen sobre cualquier nota histórica.

## Estado Git

- Rama activa: `feature/swing-shell`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Último commit de código: `afc66589c9d32988fa6e011fd0293e71fbf47c39` — `fix: refrescar obligaciones al registrar gastos`.
- Comparación anterior a los commits documentales: **457 ahead / 0 behind**.
- No hubo merge ni modificación de `main`.

## Última suite completa

`mvn clean test`: **1008/1008**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **01/11/2026 18:12:30 -03:00** (informado por el usuario).

Batería específica previa: `MainFrameTest`, `GastosPanelTest`, `ObligacionesPanelTest`: **18/18**, `BUILD SUCCESS`, 01/11/2026 16:34:22 -03:00.

## Bloques validados

- Shell Swing, login y navegación.
- Persistencia entre reinicios.
- Cuentas, categorías, ingresos, gastos, transferencias y movimientos.
- Tarjetas de crédito, obligaciones, cuotas, financiación y refinanciación.
- Inversiones, cartera y patrimonio financiero.
- Resultado financiero y reportes.
- Aislamiento multiusuario.
- Actualización de Obligaciones al registrar un gasto con tarjeta.

## Pendiente prioritario: validación manual de pagos de tarjeta

El último cambio de código (`afc6658`) refresca automáticamente Obligaciones después de registrar un gasto. El usuario confirmó que el nuevo consumo aparece en Obligaciones y Tarjetas sin reiniciar la aplicación.

Sigue pendiente verificar los pagos reales en la UI:
- saldo del ciclo seleccionado;
- obligaciones de ciclos anteriores/otros ciclos;
- estados de obligaciones (pendiente, financiada, refinanciada, liquidada, pagada);
- pago parcial o total del ciclo;
- movimiento de egreso y saldo de cuenta pagadora;
- persistencia después de cerrar/reabrir.

El usuario no puede adjuntar capturas temporalmente. Esperar a que pueda compartir una captura actual. No utilizar importes de capturas antiguas como saldos vigentes y no recomendar una operación sin reconstruir primero las obligaciones.

### Distinción funcional importante

**Pago total** en el panel de tarjetas se aplica al ciclo seleccionado; no significa cancelar necesariamente toda la deuda histórica. El saldo total de obligaciones es distinto. Las obligaciones financiadas, refinanciadas o liquidadas siguen sus propias reglas.

## Pendientes técnicos no prioritarios

- calendario bancario explícito de feriados;
- abstracción `Clock`;
- migraciones/versionado formal del esquema;
- robustez adicional del ciclo de vida de H2;
- optimizaciones de consultas cuando se justifiquen;
- definiciones financieras aún no formalizadas.

No implementar decisiones contables o financieras por inferencia.

## Próximo paso

Retomar la revisión manual de tarjetas cuando estén disponibles las capturas actuales. Si la evidencia muestra un defecto de código, inspeccionar primero implementación, servicios, repositorios y tests; luego aplicar el cambio mínimo.

## Criterio de cierre

Tests específicos → relacionados → suite completa cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación. No hacer merge a `main` automáticamente.
