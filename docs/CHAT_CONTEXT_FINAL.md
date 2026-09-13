# SOFP — Contexto final de continuidad

## Estado — 13/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** `e5fbe0f` — `fix: proteger integridad estructural de cuentas`.
**Último commit de código/tests verificado:** `00beeb1` — `fix: evitar moneda duplicada en test de integridad`.
**Último commit documental:** `8b3566b5` — corrección final del historial de auditoría.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 13/09/2026 19:00:15 -03:00.

Suite relacionada de Cuenta: **154/154**, `BUILD SUCCESS`.

Tests específicos `CuentaServiceIntegridadTest,CuentaServiceTest`: **66/66**, `BUILD SUCCESS`.

Validaciones anteriores relevantes: `ObligacionServiceTest` **9/9**, suite de obligaciones/pagos/UI **69/69**, UI de pago **6/6**.

## Integridad de Cuenta — bloque cerrado

Se implementó y validó la protección estructural derivada de la auditoría de `Cuenta`.

Reglas vigentes:

- con historial financiero no se puede cambiar el tipo de cuenta;
- con historial financiero no se puede cambiar la moneda;
- la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`;
- se mantiene autorización por usuario;
- no se impone igualdad universal entre moneda de cuenta y moneda de movimiento.

## Auditoría de ciclo y pago — bloque auditado al 100%

La auditoría comprobó que el cálculo básico del ciclo está implementado: fecha de consumo, cierre, inicio, vencimiento, meses de distinta duración y cruce de año. Las cuotas guardan sus fechas y los pagos se aplican en orden ascendente con soporte parcial.

También quedaron identificados y documentados todos los huecos temporales relevantes del código actual:

1. no se valida pago anterior al consumo;
2. no existe estado de pago en término/mora;
3. no existe gracia;
4. no existe calendario de días no hábiles;
5. no existe fecha efectiva separada;
6. se aceptan fechas futuras;
7. no existe regla temporal específica para pagos parciales sobre cuotas vencidas;
8. `Obligacion.getCicloFacturacion()` recalcula con la configuración actual, mientras las cuotas ya generadas conservan fechas persistidas;
9. `Cuenta.configurarDatosCredito(...)` puede mutar cierre/vencimiento sin una política histórica específica.

No se agregaron reglas financieras por inferencia. Intereses, punitorios, CFT y refinanciación pertenecen al bloque de financiación avanzada.

## Pendientes

### P1

1. Implementar las reglas temporales de pago una vez definidas explícitamente.
2. Definir multidivisa de tarjetas.
3. Financiamiento avanzado.

### P2/P3

4. UI específica de tarjetas.
5. Pasivos, patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.

## Orden exacto

1. Reglas temporales de pago.
2. Multidivisa.
3. Financiamiento.
4. UI específica.
5. Pasivos/patrimonio/análisis.
6. Gestión de entidades financieras.
7. Pulido.

## Protocolo

Antes de cada bloque: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

La documentación de continuidad se actualiza sobre la rama activa. No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
