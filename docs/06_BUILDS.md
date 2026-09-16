# SOFP — Historial de Builds

## Estado documental — 16/09/2026

**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código:** `48cf588` — `test: cubrir cierre de ciclo desde obligaciones`.

## Validación más reciente

- `mvn test`: **744/744**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizado: **16/09/2026 18:47:51 -03:00**.
- Tiempo total: **10:32 min**.

Validación específica inmediatamente anterior: `ObligacionesPanelTest` **6/6**, `BUILD SUCCESS`, finalizada **16/09/2026 18:22:54 -03:00**.

## Bloque implementado

### Cierre de ciclo desde ObligacionesPanel

`ObligacionesPanel` incorpora el botón `Cerrar ciclo` y delega el cierre en `ObligacionService`, usando el ciclo persistido de la obligación y refrescando la UI.

Se cubrieron tanto el cierre multidivisa con cotización histórica como el fallo por ausencia de cotización, verificando rollback y ausencia de valorización.

Commits del bloque:

- `a5e6a86` — `feat: permitir cerrar ciclo desde obligaciones`.
- `48cf588` — `test: cubrir cierre de ciclo desde obligaciones`.

## Bloques multidivisa ya cerrados

- `TipoCambio` histórico.
- moneda original y moneda de liquidación.
- liquidación explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- valorización histórica de cierre separada de liquidación.
- crédito disponible basado en valorización histórica.
- reducción proporcional del crédito después de pagos parciales.
- cierre de ciclo iniciado desde UI.

No se realizan conversiones implícitas.

## Próximo bloque

1. Definir el flujo completo de obtención/registro de valorización de cierre en la aplicación.
2. Definir la regla para obligaciones multidivisa sin valorización al cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Revisar consumos extranjeros sobre crédito antes de la valorización.

## Estabilización futura

Antes del fast-forward a `main`, y no como parte del bloque actual, implementar: arranque automático de H2 desde Java, cierre limpio de H2, consola silenciosa, logging técnico a archivo y errores de arranque/conexión informados mediante `JOptionPane`.

No se modificó `main`.
