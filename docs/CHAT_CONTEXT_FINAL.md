# SOFP — Contexto final de continuidad

## Estado — 13/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Últimos commits funcionales:** `a2a96bb` y `2813fa3`, cierre de autorización de pagos de `ObligacionService`. Los commits posteriores registrados en esta etapa son de documentación.

## Validación más reciente

Suite general informada: **696/696**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite `ObligacionServiceTest`: **9/9**, `BUILD SUCCESS`.

Suite relacionada de obligaciones/pagos/UI: **69/69**, `BUILD SUCCESS`.

Tests específicos de UI de pago: **6/6**, `BUILD SUCCESS`.

La suite relacionada mostró un warning de Surefire por demora en la terminación de la JVM después de `System.exit(0)`, pero terminó correctamente.

## Auditoría de Cuenta — terminada el 13/09/2026

Se completó la revisión del eje `Cuenta`: dominio, servicio, repositorio, tipos, moneda, movimientos, gastos, operaciones financieras y tests relacionados. No se modificó código durante la auditoría.

Conclusiones definitivas:

- tipo y moneda de `Cuenta` son mutables y actualmente no se verifica historial antes de modificarlos;
- una cuenta puede convertirse a `TARJETA_CREDITO` sin configurar automáticamente sus datos de crédito obligatorios;
- no existe política explícita para los datos de crédito al salir de tarjeta;
- cambiar moneda con historial requiere protección para no alterar la interpretación histórica;
- `Movimiento` tiene moneda propia y los consumos de tarjeta pueden conservar una moneda económica distinta de la cuenta;
- las transferencias entre cuentas ya exigen misma moneda;
- el repositorio de cuentas no introduce reglas de negocio.

Regla base para el próximo cambio: bloquear cambios estructurales de tipo/moneda cuando exista historial financiero, y definir explícitamente la transición hacia/desde tarjeta. No introducir conversiones automáticas ni romper el soporte de consumos de tarjeta en moneda extranjera.

## Pendientes

### P1 — Integridad de Cuenta

Implementar la regla de la auditoría con tests de cuenta sin historial, con historial, tarjeta, persistencia y autorización.

### P1 — Ciclo aplicado al pago

Definir vencimiento, mora, gracia y días no hábiles antes de implementar.

### P1 — Multidivisa

Definir tratamiento del límite de tarjeta frente a consumos en monedas distintas.

### P1 — Financiación avanzada

Intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones y ajustes.

### P2/P3

UI específica de tarjetas; pasivos/patrimonio/análisis; gestión de entidades financieras; pulido de consola.

## Orden exacto

1. Integridad de `Cuenta`.
2. Reglas de ciclo durante pagos.
3. Multidivisa.
4. Financiación.
5. UI específica.
6. Pasivos/patrimonio/análisis.
7. Gestión de entidades financieras.
8. Pulido.

## Protocolo

Antes de cada bloque: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

La documentación de continuidad se actualiza sobre la rama activa. No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
