# SOFP — Contexto final de continuidad

## Estado — 13/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último cambio funcional:** `e5fbe0f` — `fix: proteger integridad estructural de cuentas`.
**Último commit verificado antes de esta actualización documental:** `00beeb1` — `fix: evitar moneda duplicada en test de integridad`.

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
- no se impone igualdad universal entre moneda de cuenta y moneda de movimiento, preservando consumos de tarjeta en moneda económica extranjera.

La implementación no realiza conversiones automáticas ni limpia/migra datos de crédito implícitamente.

## Pendientes

### P1

1. Definir reglas de ciclo durante el pago.
2. Definir multidivisa de tarjetas.
3. Financiamiento avanzado.

### P2/P3

4. UI específica de tarjetas.
5. Pasivos, patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.

## Orden exacto

1. Reglas de ciclo durante pagos.
2. Multidivisa.
3. Financiamiento.
4. UI específica.
5. Pasivos/patrimonio/análisis.
6. Gestión de entidades financieras.
7. Pulido.

## Protocolo

Antes de cada bloque: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

La documentación de continuidad se actualiza sobre la rama activa. No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
