# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Estado auditado — 14/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último commit funcional/test:** `3a001a57c435237e62ab04f6c09a0657ff24fcb2` — `test: adaptar persistencia de obligaciones a reglas temporales`.
**HEAD actual:** posterior al cierre funcional, se agregaron commits documentales, incluido `b007fce3e22c168b4dca68ec8bc96ae9128ba522` para esta auditoría integral.

## Último bloque funcional cerrado

### Reglas temporales de ciclos y pagos de tarjeta

Quedó implementado y validado el bloque temporal: el ciclo histórico de una obligación queda persistido al crearla; las cuotas conservan sus fechas; el vencimiento se ajusta si cae sábado o domingo; la tarjeta admite días de gracia; la mora se evalúa sobre vencimiento efectivo más gracia; los pagos anteriores al consumo y futuros son rechazados; se mantiene el pago parcial y en orden de cuotas. Los nuevos campos son compatibles con datos existentes mediante valores nulos/fallback.

### Integridad estructural de Cuenta y Movimiento → Obligación

Se protege la integridad histórica: una cuenta con movimientos no puede cambiar de tipo ni de moneda, y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`. El movimiento origen de una obligación queda protegido frente a cambios estructurales incompatibles y eliminación.

## Auditoría integral — resultado

La auditoría integral del 14/09/2026 confirmó que el núcleo del proyecto está consolidado y que no corresponde rehacer la arquitectura. El principal hueco funcional real es la **multidivisa**.

Se detectaron tres problemas concretos que deben resolverse antes de considerar cerrada la multidivisa:

1. `CuentaService.calcularSaldo` mezcla importes de monedas distintas al sumar movimientos de una cuenta.
2. `MovimientoService` utiliza el mismo saldo mezclado al validar fondos.
3. Un consumo de tarjeta en moneda distinta de la moneda de la tarjeta queda fuera del cálculo actual del límite, porque no existe todavía una regla de conversión/liquidación.

Por decisión de seguridad financiera, no se deben introducir conversiones implícitas. Primero deben definirse moneda de liquidación, tasa, fecha/fuente de cotización y tratamiento de saldos por moneda.

Otros hallazgos de robustez: rango de `Moneda.cantidadDecimales`, política de eliminación de cuentas con historial, abstracción `Clock` y eventual versionado formal de esquema.

La auditoría completa quedó registrada en `docs/11_AUDITORIA_INTEGRAL.md`.

## Validación más reciente conocida

El usuario ejecutó `mvn test`: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado el 13/09/2026 a las 22:05:14 -03:00.

También se verificó `mvn -Dtest=ObligacionJpaTest test`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado el 13/09/2026 a las 21:12:49 -03:00.

## Pendientes reales después de la auditoría

### P0/P1

- Cerrar el modelo de multidivisa de tarjetas: saldos por moneda, crédito disponible por moneda y regla explícita de liquidación/conversión.
- Cubrir con tests los casos de cuenta/tarjeta con movimientos y obligaciones en distintas monedas.

### P2

- Validar rango de `Moneda.cantidadDecimales`.
- Definir política de eliminación de cuentas con historial financiero.
- Evaluar abstracción `Clock`.
- Evaluar migraciones/versionado de esquema si el proyecto deja la etapa de desarrollo local.

### P3

- Financiación avanzada.
- UI específica de tarjetas.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

### Fuera del bloque actual

No están implementados calendario de feriados, fecha efectiva separada de la fecha/hora del movimiento ni recargos financieros. No se deben inventar reglas sin decisión de negocio explícita.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/docs → código → tests → auditoría vigente → último resultado conocido → próximo paso.

No modificar `main` automáticamente. No asumir resultados locales no informados. Antes de iniciar un nuevo bloque, consultar el HEAD real de la rama de trabajo en GitHub.
