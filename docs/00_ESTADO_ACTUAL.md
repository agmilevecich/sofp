# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 15/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** validación de `Moneda.cantidadDecimales`.
**Últimos commits del bloque:** `d4fdcd9` — `fix: validar decimales no negativos en Moneda`; `5a6de42` — `test: validar decimales no negativos en Moneda`.
**Último commit documental:** `96de871` — `docs: corregir referencia historica de suite`.

## Último bloque funcional cerrado

### Validación de `Moneda.cantidadDecimales`

`Moneda` mantiene el rechazo de `null` mediante `NullPointerException` y ahora rechaza valores negativos mediante `IllegalArgumentException`, tanto al crear la entidad como al modificar la cantidad de decimales.

Validación específica: `MonedaTest` **7/7**.

### Multidivisa — estado actual

La moneda económica de `Movimiento` puede ser distinta de la moneda de `Cuenta`. Ya se corrigieron los cálculos para que el saldo de una cuenta y la validación de fondos de `MovimientoService` trabajen con la moneda correspondiente, evitando mezclar importes incompatibles.

Continúa pendiente el tratamiento completo de tarjetas cuando la moneda del consumo difiere de la moneda de la tarjeta, especialmente crédito disponible y liquidación de pagos.

### Integridad y tarjetas

La integridad estructural de `Cuenta` está protegida: una cuenta con movimientos no puede cambiar de tipo ni moneda y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

El movimiento origen de una obligación queda protegido frente a cambios estructurales incompatibles y eliminación. Las obligaciones, cuotas, pagos coordinados, autorización, ciclos históricos, vencimientos de fin de semana, días de gracia y mora están implementados.

## Validación más reciente informada por el usuario

Suite general:

- `mvn test`: **693/693**
- failures: 0
- errors: 0
- skipped: 0
- `BUILD SUCCESS`
- finalizada: **15/09/2026 12:03:19 -03:00**

Suite relacionada antes de la general:

- `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: **53/53**
- failures: 0
- errors: 0
- skipped: 0
- `BUILD SUCCESS`
- finalizada: **15/09/2026 11:34:38 -03:00**

`MonedaTest`: **7/7**, `BUILD SUCCESS`, 15/09/2026 11:21:20 -03:00.

## Pendientes reales

### P0/P1 — Multidivisa

1. Definir el impacto sobre el límite cuando el consumo y la tarjeta usan monedas distintas.
2. Definir moneda de liquidación, tasa de cambio, fecha/fuente de cotización y trazabilidad.
3. Resolver pagos/liquidaciones entre monedas sin conversiones implícitas.
4. Cubrir con tests los casos cruzados de consumo, límite y pago.

### P2 — Robustez

1. Política de eliminación de cuentas con historial financiero.
2. Abstracción `Clock` para determinismo temporal.
3. Migraciones/versionado formal de esquema para una futura etapa no local.

### P3 — Evolución

1. Financiación avanzada.
2. UI específica de tarjetas.
3. Pasivos, patrimonio y análisis.
4. Gestión de entidades financieras.
5. Pulido de consola.

### Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento e intereses/punitorios/CFT/refinanciación requieren decisiones de negocio antes de implementarse.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → documentación → código relacionado → tests → último resultado informado → próximo paso.

No modificar `main` automáticamente. No asumir resultados locales no informados. Antes de considerar cerrado un bloque: tests específicos → relacionados → suite → diff → diff-check → status → documentación.
