# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 13/09/2026

La fuente de verdad es el código, los tests y Git. `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** `e5fbe0f` — `fix: proteger integridad estructural de cuentas`.
**Último commit de código/tests verificado:** `00beeb1` — `fix: evitar moneda duplicada en test de integridad`.
**Último commit documental:** `a090d152` — cierre documental de auditoría de ciclos.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 46:17 min, finalizada 13/09/2026 19:00:15 -03:00.

Suite relacionada de Cuenta: **154/154**, `BUILD SUCCESS`.

Tests específicos `CuentaServiceIntegridadTest,CuentaServiceTest`: **66/66**, `BUILD SUCCESS`.

Validaciones anteriores relevantes: `ObligacionServiceTest` **9/9**, suite de obligaciones/pagos/UI **69/69**, UI de pago **6/6**.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`ObligacionService` exige `usuarioId` para registrar pagos y valida el perfil propietario.

`CuentaService` protege la integridad estructural: no permite cambiar tipo ni moneda cuando existen movimientos y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

## Auditoría temporal completada

La auditoría del ciclo de facturación aplicado al pago quedó cerrada documentalmente.

### Existe actualmente

- cálculo de ciclo por fecha de consumo y día de cierre;
- ajuste de fechas al último día real del mes;
- vencimiento posterior al cierre;
- cruce de año;
- fechas de ciclo/vencimiento persistidas en `Cuota`;
- pagos parciales;
- aplicación de pagos en orden ascendente de cuota;
- movimiento real de salida al pagar;
- coincidencia de moneda entre obligación y cuenta pagadora.

### Falta actualmente

- validación de pago anterior al consumo;
- clasificación en término/mora;
- gracia;
- fines de semana/feriados;
- fecha efectiva separada;
- rechazo de fechas futuras;
- regla temporal para pagos parciales sobre cuotas vencidas;
- protección histórica de cierre/vencimiento después de existir consumos;
- estabilidad histórica de `Obligacion.getCicloFacturacion()` frente a cambios posteriores de configuración.

Se detectó específicamente que las cuotas guardan sus fechas, pero `Obligacion.getCicloFacturacion()` recalcula desde la configuración actual de la tarjeta. También se verificó que `Cuenta.configurarDatosCredito(...)` es mutable y no tiene todavía una política histórica equivalente a la protección aplicada a tipo/moneda.

No se modificó código durante la auditoría. No se inventaron reglas de mora, gracia ni días no hábiles. Intereses y financiación avanzada permanecen fuera de alcance.

## Pendientes reales

P1:

1. Implementar las reglas temporales de pago una vez fijadas explícitamente.
2. Definir multidivisa de tarjetas.
3. Definir financiación avanzada.

P2/P3:

4. UI específica de tarjetas.
5. Pasivos, patrimonio, histórico, vencimientos, resúmenes y dashboard.
6. Gestión de entidades financieras.
7. Pulido de consola.

## Reglas

No duplicar reglas de negocio en Swing. Mantener autorización en servicios. No inventar reglas multidivisa, mora, gracia o calendario. Cada bloque debe incluir tests y validación de persistencia cuando corresponda.

## Continuidad

Reconstruir siempre desde GitHub antes de cambios: rama → commits → comparación con `main` → documentación → código → tests → último resultado → próximo paso.

La documentación se actualiza sobre la rama activa. No modificar `main` y no asumir resultados locales no informados.
