# SOFP — Pendientes

## Estado auditado — 14/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** reglas temporales de ciclos y pagos, implementadas y validadas.
**Última suite completa informada:** **704/704**, BUILD SUCCESS.

## Bloques cerrados

- Selección explícita de tarjeta activa en `GastosPanel`.
- Generación de cuotas y cruce de año.
- Atomicidad de compra con tarjeta.
- Pago coordinado y pago real desde UI.
- Integración de `PagoTarjetaService` en shell.
- Protección de movimientos origen de obligaciones.
- Autorización del registro de pagos.
- Integridad estructural de `Cuenta`.
- Auditoría temporal de ciclos y pagos.
- Implementación temporal de pagos.
- Adaptación de persistencia de obligaciones a las nuevas reglas temporales.
- Auditoría integral del estado técnico y de documentación.

## Hallazgos de la auditoría integral

### P0/P1 — Multidivisa

La moneda económica de un movimiento puede ser explícita y distinta de la moneda de la cuenta. Esto permite representar consumos extranjeros, pero todavía no existe un modelo completo de liquidación multidivisa.

Hallazgos confirmados:

1. El saldo de una cuenta mezcla importes de distintas monedas.
2. La validación de fondos de `MovimientoService` utiliza ese saldo mezclado.
3. Un consumo de tarjeta en moneda distinta de la moneda de la tarjeta no entra en el criterio actual del límite.
4. El pago coordinado exige misma moneda entre deuda y cuenta pagadora y no dispone todavía de conversión.

Antes de implementar conversiones se deben definir moneda de liquidación, tasa de cambio, fecha/fuente de cotización y cómo se representa el saldo por moneda.

### P2 — Robustez

- `Moneda.cantidadDecimales` admite actualmente valores negativos porque solo se valida nulidad.
- La eliminación de `Cuenta` con historial financiero no tiene una política de dominio explícita y el test revisado cubre una cuenta sin historial.
- `PagoTarjetaService` usa `LocalDateTime.now()` directamente; una abstracción `Clock` mejoraría el determinismo de tests.
- `hibernate.hbm2ddl.auto=update` sirve para desarrollo actual, pero no reemplaza un esquema versionado/migraciones para una futura etapa de distribución.

## Implementación temporal cerrada

1. Pago anterior al consumo: rechazado.
2. Fecha futura: rechazada.
3. Vencimiento en sábado/domingo: desplazado al lunes.
4. Días de gracia configurables, por defecto 0.
5. Mora derivada del vencimiento efectivo más gracia.
6. Obligaciones con cuotas: fecha límite basada en la primera cuota pendiente.
7. Ciclo histórico congelado en la obligación al crearla.
8. Compatibilidad con datos existentes mediante campos nuevos nullable y fallback.
9. Tests de persistencia adaptados a las reglas temporales.
10. Suite completa validada después de los cambios: 704/704.

No se agregaron intereses ni punitorios.

## Pendientes reales en orden

### P0/P1

- Cerrar multidivisa de tarjetas sin conversiones implícitas.
- Corregir saldos y disponibilidad de fondos por moneda.
- Definir y cubrir el límite de crédito ante consumos en moneda distinta.
- Definir liquidación/conversión de pagos cuando corresponda.

### P2

- Validación de rango de decimales de `Moneda`.
- Política de eliminación de cuentas con historial.
- Abstracción `Clock`.
- Migraciones/versionado de esquema para una futura etapa no local.

### P3

- Financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.
- UI específica de tarjetas: límite/disponible, consumos, ciclos, cierres, vencimientos, deuda y pagos.
- Pasivos, patrimonio y análisis.
- Gestión de entidades financieras.
- Pulido de consola.

## Fuera de alcance actual

Calendario de feriados, fecha efectiva separada del movimiento y recargos financieros no están implementados. Requieren decisiones de negocio antes de codificar.

## Regla de cierre

Tests específicos → relacionados → suite general → `git diff` → `git diff --check` → `git status` → documentación.

La documentación de continuidad debe reflejar el último estado real de GitHub y nunca reemplazar la verificación del código y los tests.
