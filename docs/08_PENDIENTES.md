# SOFP — Pendientes

## Estado auditado — 17/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `d11e778480d319c1912142a94126fce2a2020e84` como último commit de código.

Última validación informada: **750/750**, 0 fallos, 0 errores, 0 omitidos, `BUILD SUCCESS`, finalizada **17/09/2026 13:29:26 -03:00**.

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
- Registro de cotizaciones históricas desde la aplicación.
- Persistencia transaccional del registro de cotizaciones.
- Formulario Swing para registrar cotización histórica.
- Moneda original y moneda de liquidación de `Obligacion`.
- Liquidación histórica explícita y trazable.
- `saldoLiquidacion` y pagos parciales/totales.
- Valorización histórica de cierre separada de la liquidación.
- Obtención de la cotización histórica necesaria al cerrar el ciclo.
- Uso de la valorización para crédito disponible.
- Corrección proporcional del crédito después de pagos parciales.
- Cierre de ciclo iniciado desde `ObligacionesPanel`.

## Pendientes inmediatos — multidivisa

1. Definir el comportamiento de una obligación multidivisa que ya fue valorizada al cierre y continúa hacia liquidación y pago.
2. Completar el flujo integral de cierre → liquidación → pago multidivisa en persistencia y UI.
3. Revisar el comportamiento de consumos extranjeros sobre crédito antes de disponer de valorización de cierre.

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
