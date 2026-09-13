# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar y puede quedar desactualizada.

## Estado verificado — 13/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** `e5fbe0f9064841e2a03a671c0bf877e1897e8289` — `fix: proteger integridad estructural de cuentas`.
**Último commit de código/tests verificado:** `00beeb17fcd781e039374dd5cf7f40ca60a39db4` — `fix: evitar moneda duplicada en test de integridad`.

La documentación de esta auditoría se realiza sobre `feature/swing-shell`; no se modifica `main` ni se hace merge automático.

## Último bloque cerrado

### Integridad estructural de `Cuenta`

Se protege la integridad histórica: una cuenta con movimientos no puede cambiar de tipo ni de moneda, y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`. La autorización por usuario se mantiene y no se impone igualdad universal entre moneda estructural de cuenta y moneda económica de movimiento.

## Auditoría completada — ciclo de facturación y pago

Se revisaron `Cuenta`, `CicloFacturacion`, `Cuota`, `Obligacion`, `PagoTarjetaService`, `CuentaService` y los tests disponibles relacionados con tarjeta/obligaciones/pagos.

### Reglas actualmente implementadas

1. Una tarjeta posee `diaCierre` y `diaVencimiento` y valida ambos entre 1 y 31.
2. El ciclo se calcula a partir de la fecha del consumo.
3. Un consumo en o antes del cierre pertenece al ciclo que cierra ese mes; un consumo posterior pertenece al ciclo que cierra el mes siguiente.
4. El inicio del ciclo es el día posterior al cierre anterior.
5. Si el día configurado no existe en un mes, se usa el último día real del mes.
6. El vencimiento se ubica después del cierre; si el día de vencimiento no puede estar en el mismo mes por ser menor/igual al cierre o no existir, pasa al mes siguiente y se ajusta al último día real cuando corresponde.
7. `CicloFacturacion` garantiza `inicio <= cierre < vencimiento`.
8. Al generar cuotas, cada cuota conserva persistentemente inicio, cierre y vencimiento del ciclo calculado.
9. Las cuotas se aplican en orden ascendente y admiten pagos parciales.
10. `PagoTarjetaService` registra el pago como un egreso real de la cuenta pagadora y exige misma moneda entre cuenta pagadora y obligación.

### Hallazgos de la auditoría temporal

**A. No existe todavía una regla temporal en el pago.** `PagoTarjetaService` recibe `fechaHora`, pero actualmente solo la usa como fecha del nuevo movimiento. No comprueba si el pago es anterior al consumo, anterior/al día del vencimiento, posterior al vencimiento o futuro.

**B. No existe concepto de mora.** No hay estado, marca, cálculo ni evento que distinga pago en término de pago tardío.

**C. No existe período de gracia.** No hay atributo ni regla para tolerancia posterior al vencimiento.

**D. No existe calendario de días no hábiles.** El vencimiento se calcula exclusivamente por día de mes. No se desplaza por sábado, domingo ni feriados.

**E. No existe fecha efectiva de pago separada de la fecha/hora del movimiento.** El sistema tiene una única `fechaHora` para el movimiento de pago.

**F. No existe regla de pago futuro.** La API no impide registrar un pago con fecha posterior al momento real de la operación.

**G. El orden de aplicación de cuotas es correcto pero no temporal.** `Obligacion.registrarPago` aplica siempre desde la cuota 1 en adelante, independientemente de la fecha del pago y de los vencimientos individuales. Esto preserva el orden de cuotas existente, pero no representa todavía una política de mora por cuota.

**H. Existe un riesgo de estabilidad histórica del ciclo.** `Cuota` persiste sus fechas, pero `Obligacion.getCicloFacturacion()` vuelve a calcular el ciclo usando la configuración actual de `Cuenta`. Por lo tanto, si después de crear una compra se modificaran `diaCierre` o `diaVencimiento`, el ciclo derivado de la obligación podría cambiar mientras las cuotas ya generadas conservarían sus fechas originales.

**I. La configuración de crédito es mutable sin una protección histórica equivalente a tipo/moneda.** `Cuenta.configurarDatosCredito(...)` puede cambiar límite, cierre y vencimiento. La API pública actual de `CuentaService` no expone una operación específica para esa modificación, pero el dominio permite la mutación directa. Antes de usar el ciclo como dato histórico inmutable debe definirse cómo se protege esta configuración una vez que existen consumos/ciclos.

**J. No se detectó lógica de intereses, punitorios, CFT ni recargos en este bloque.** Esto es correcto como separación de alcance: esas reglas pertenecen al bloque de financiación avanzada y no deben inventarse dentro de la auditoría temporal.

### Resultado de auditoría

El cálculo básico de ciclos y vencimientos está implementado y es coherente para meses cortos y cruces de año, pero **la aplicación temporal del pago todavía no está implementada**. El modelo actual permite registrar pagos sin distinguir en término/tardío, sin gracia y sin calendario de días no hábiles.

No se realizaron cambios de código durante esta auditoría porque las reglas de negocio faltantes no están definidas en el código actual y agregarlas por inferencia introduciría comportamiento financiero inventado.

## Validación más reciente conocida

El usuario ejecutó `mvn test` y obtuvo **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado el 13/09/2026 a las 19:00:15 -03:00.

La suite relacionada de `Cuenta` obtuvo **154/154**, y los tests específicos de integridad **66/66**, todos con `BUILD SUCCESS`.

## Estado de pendientes reales

### P1 — Auditoría temporal completada; implementación pendiente de reglas explícitas

El próximo bloque funcional deberá convertir los hallazgos anteriores en reglas de negocio comprobables y tests. La auditoría ya dejó identificados todos los puntos temporales relevantes: fecha mínima de pago, vencimiento, mora, gracia, días no hábiles, fecha efectiva, pagos futuros y aplicación a cuotas.

### P1 — Multidivisa de tarjetas

Definir el tratamiento definitivo del límite de una tarjeta frente a consumos en monedas diferentes.

### P1 — Financiación avanzada

Intereses, CFT/costo financiero, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.

### P2 — UI específica de tarjetas

Límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos reales.

### P2 — Pasivos, patrimonio y análisis

Ampliar pasivos/patrimonio neto y luego histórico, vencimientos, resúmenes y dashboard.

### P2/P3 — Gestión de entidades financieras

No existe todavía un panel específico para registrar/gestionar entidades financieras.

### P3 — Pulido de consola

Prioridad baja.

## Protocolo de continuidad

Ante una nueva sesión: rama → últimos commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.

Prioridad: **código → tests → commits → `main` → documentación → conversaciones anteriores**.

No modificar `main` automáticamente. No asumir resultados locales no informados. No considerar terminada una tarea solo porque compila.
