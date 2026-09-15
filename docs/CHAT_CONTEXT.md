# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 15/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** validación de `Moneda.cantidadDecimales`.
**Commits funcionales/test del bloque:** `d4fdcd9` y `5a6de42`.
**Último bloque documental:** actualización integral de continuidad del 15/09/2026.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario:

- `mvn test`: **693/693**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **15/09/2026 12:03:19 -03:00**.

Validación relacionada:

- `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: **53/53**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: 15/09/2026 11:34:38 -03:00.

`MonedaTest`: **7/7**, `BUILD SUCCESS`, 15/09/2026 11:21:20 -03:00.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`ObligacionService` exige `usuarioId` para registrar pagos y valida el perfil propietario.

`CuentaService` protege la integridad estructural: no permite cambiar tipo ni moneda cuando existen movimientos y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

Los saldos y la disponibilidad de fondos ya se calculan respetando la moneda correspondiente; no se deben mezclar importes ARS y USD.

## Reglas temporales implementadas

- ciclo histórico de la obligación persistido al crearla;
- fechas de ciclo persistidas en cuotas;
- vencimiento de sábado/domingo desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada sobre vencimiento efectivo más gracia;
- pago anterior al consumo rechazado;
- pago futuro rechazado;
- pagos parciales aplicados en orden ascendente de cuotas;
- estabilidad histórica frente a cambios posteriores de configuración cuando existen datos persistidos;
- compatibilidad con obligaciones antiguas mediante campos nullable/fallback.

No están implementados calendario de feriados, fecha efectiva separada del movimiento, intereses, punitorios, CFT ni refinanciación.

## Robustez reciente

`Moneda.cantidadDecimales`:

- `null` sigue rechazado;
- negativos rechazados con `IllegalArgumentException`;
- valores no negativos permitidos;
- la regla se aplica al crear y modificar la moneda.

## Estado de multidivisa

La moneda explícita del movimiento se conserva.

Ya están resueltos:

1. saldo de cuenta separado por moneda;
2. validación de fondos separada por moneda;
3. coexistencia de saldos ARS/USD sin mezclarlos.

Sigue pendiente:

1. impacto de consumos extranjeros sobre el límite de tarjeta;
2. moneda de liquidación;
3. tasa, fecha y fuente de cotización;
4. conversión/liquidación trazable de pagos entre monedas;
5. tests de estos casos una vez definidas las reglas.

No se deben introducir conversiones implícitas.

## Pendientes reales

### P0/P1

1. Resolver multidivisa de tarjetas por moneda.
2. Cubrir crédito, consumo y liquidación multidivisa con tests.

### P2

3. Política de eliminación histórica de cuentas.
4. Abstracción `Clock`.
5. Migraciones/versionado de esquema.

### P3

6. Financiación avanzada.
7. UI específica de tarjetas.
8. Pasivos, patrimonio y análisis.
9. Gestión de entidades financieras.
10. Pulido de consola.

## Protocolo

Antes de cada bloque: reconstruir desde GitHub rama → últimos commits → comparación con `main` → documentación → implementación → clases relacionadas → tests → auditoría vigente → último resultado informado.

Luego: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

La documentación de continuidad se actualiza sobre la rama activa. No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
