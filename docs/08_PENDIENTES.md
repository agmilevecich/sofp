# SOFP — Pendientes

## Estado auditado — 16/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `48cf588` como último commit de código antes de la actualización documental.

Última validación informada: **744/744**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, finalizada **16/09/2026 18:47:51 -03:00**.

## Bloques cerrados

- Shell Swing y navegación.
- Cuentas, categorías, ingresos, gastos, movimientos e inversiones.
- Obligaciones y pagos de tarjeta desde UI.
- Autorización de pagos por usuario.
- Integridad estructural e histórica.
- Ciclos, cuotas, vencimientos, gracia y mora.
- Aislamiento JPA/H2 para tests.
- Saldos y fondos separados por moneda.
- `Moneda.cantidadDecimales` no negativa.
- `TipoCambio` histórico.
- Moneda original y moneda de liquidación de `Obligacion`.
- Liquidación histórica explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- Valorización histórica de cierre separada de la liquidación.
- Uso de la valorización para crédito disponible.
- Corrección proporcional del crédito después de pagos parciales.
- Cierre de ciclo iniciado desde `ObligacionesPanel`.

## Pendientes inmediatos — multidivisa

1. Definir completamente el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa que todavía no tiene valorización de cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Revisar el comportamiento de consumos extranjeros sobre crédito antes de disponer de valorización de cierre.

No se deben introducir conversiones implícitas.

## P2 — Robustez

- Política de eliminación de cuentas con historial financiero.
- Abstracción `Clock`.
- Migraciones/versionado formal de esquema para una futura etapa no local.

## P3 — Evolución

- Financiación avanzada.
- UI específica de tarjetas.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## Estabilización futura — previa al fast-forward a main

Separada del desarrollo funcional actual:

- iniciar H2 automáticamente desde Java al arrancar SOFP;
- detener H2 limpiamente al cerrar;
- ocultar la salida técnica de consola;
- conservar detalle técnico mediante logging a archivo;
- informar fallos de conexión con la base y otros errores de arranque mediante `JOptionPane`;
- evitar mostrar una ventana parcialmente inicializada si el arranque falla.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.
