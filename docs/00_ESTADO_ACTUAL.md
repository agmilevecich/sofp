# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 16/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `48cf588652ab9ad1d6f015ead69a6bfecd1c978e`.

No se realizó merge a `main`.

## Último bloque implementado

### Cierre de ciclo desde ObligacionesPanel

Se agregó el botón **Cerrar ciclo** a `ObligacionesPanel`. La UI delega el cierre en `ObligacionService`, utiliza el ciclo persistido de la obligación y refresca el panel.

Se agregó cobertura para:

- cierre desde UI de una obligación multidivisa con valorización histórica;
- error cuando falta la cotización histórica de cierre;
- rollback y ausencia de valorización ante ese error.

Commits:

- `a5e6a86` — `feat: permitir cerrar ciclo desde obligaciones`.
- `48cf588` — `test: cubrir cierre de ciclo desde obligaciones`.

## Validación más reciente

- `mvn test`: **744/744**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizado: **16/09/2026 18:47:51 -03:00**.
- Tiempo total: **10:32 min**.

Validación específica anterior: `ObligacionesPanelTest` **6/6**, `BUILD SUCCESS`, finalizada **16/09/2026 18:22:54 -03:00**.

El usuario verificó `git status`, `git diff` y `git diff --check`: working tree limpio y sin observaciones. La rama local estaba alineada con GitHub y Bitbucket.

## Estado multidivisa

Resuelto:

- saldos y fondos por moneda;
- moneda original y moneda de liquidación;
- `TipoCambio` histórico explícito y persistente;
- liquidación trazable y `saldoLiquidacion`;
- pagos parciales y totales sobre saldo de liquidación;
- valorización histórica de cierre separada de la liquidación;
- utilización de la valorización para crédito disponible;
- ajuste proporcional del crédito utilizado después de pagos parciales;
- cierre de ciclo iniciado desde `ObligacionesPanel`.

Reglas vigentes:

- no hay conversiones implícitas;
- una obligación multidivisa sin cotización histórica necesaria para cierre provoca error y rollback;
- valorización y liquidación siguen siendo conceptos separados;
- el crédito usado por una obligación multidivisa valorizada se calcula proporcionalmente sobre el saldo original pendiente.

## Pendientes inmediatos

1. Definir completamente el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa todavía no valorizada al cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Revisar el comportamiento de consumos extranjeros sobre crédito antes de disponer de valorización de cierre.

## P2 — Robustez

1. Política de eliminación de cuentas con historial financiero.
2. Abstracción `Clock` para determinismo temporal.
3. Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

1. Financiación avanzada.
2. UI específica de tarjetas.
3. Pasivos, patrimonio y análisis.
4. Gestión de entidades financieras.
5. Pulido de consola.

## Estabilización futura — antes del fast-forward a main

No implementar todavía. Para la versión estable se deberá:

- iniciar H2 automáticamente desde Java;
- detener H2 limpiamente al salir;
- ocultar la salida técnica de consola;
- conservar detalle técnico en archivo de log;
- informar fallos de conexión con la base y otros errores de arranque mediante `JOptionPane`;
- evitar mostrar una ventana parcialmente inicializada si el arranque falla.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → documentación → código relacionado → tests → último resultado informado → próximo paso.

No modificar `main` automáticamente. No asumir resultados locales no informados. Antes de considerar cerrado un bloque: tests específicos → relacionados → suite → diff → diff-check → status → documentación.
