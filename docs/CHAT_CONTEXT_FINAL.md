# SOFP — Contexto compacto de continuidad

Actualizado 01/11/2026. Reconstruir siempre el estado desde GitHub antes de trabajar.

- Repo: `agmilevecich/sofp`.
- Rama: `feature/swing-shell`; estable: `main`.
- Main SHA: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Último commit de código: `afc6658` — `fix: refrescar obligaciones al registrar gastos`.
- Comparación anterior a docs: 457 ahead / 0 behind; main no fue modificada.
- Suite completa informada: **1008/1008**, 0 failures/errors/skipped, `BUILD SUCCESS`, 01/11/2026 18:12:30 -03:00.
- Batería específica: 18/18 (`MainFrameTest,GastosPanelTest,ObligacionesPanelTest`), 01/11/2026 16:34:22 -03:00.
- El callback agregado en `MainFrame` refresca Obligaciones al registrar gastos. Usuario confirmó que el consumo aparece inmediatamente en Obligaciones y Tarjetas.

## Pendiente exacto
Validación manual de pagos de tarjeta en la UI. **Pago total** aplica al ciclo seleccionado, no necesariamente a toda la deuda histórica. El saldo total de obligaciones es distinto y puede incluir otros ciclos/obligaciones financiadas, refinanciadas o liquidadas.

El usuario no puede adjuntar capturas temporalmente. Esperar una captura actual antes de indicar pagos. No asumir vigentes los importes históricos de $30.000/$45.000. Revisar ciclos, estados, cuenta pagadora y fondos; después comprobar egreso, saldos y persistencia.

No cambiar código sin evidencia reproducible. No modificar ni mergear main. Flujo: GitHub → código/tests → cambio mínimo → commit → usuario sincroniza y prueba → análisis → documentación.
