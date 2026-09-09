# SOFP — Continuidad 2026-09-09

## Estado

- Rama de trabajo: `feature/swing-shell`
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`
- Antes de este cierre, la rama estaba 428 commits adelante de `main` y 0 atrás. GitHub es la fuente de verdad para el conteo actual.
- Último bloque funcional: límite y crédito disponible de tarjetas de crédito.

## Últimos cambios

Se implementó el criterio inicial de crédito disponible para tarjetas:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

No se realizan conversiones implícitas entre monedas.

Se incorporó:

- `Cuenta.calcularCreditoDisponible(...)`.
- `CuentaService.calcularCreditoDisponible(...)` con autorización y filtrado por moneda.
- Validación de límite al registrar consumos con `FormaPago.TARJETA_CREDITO`.
- Validación al modificar importe o tipo de movimiento.
- Exclusión de consumos con tarjeta de crédito del saldo monetario de la cuenta.
- Pruebas para consumo parcial, límite exacto, exceso de límite y consumo en moneda diferente.

## Commits del bloque

- `923e6bf` — `feat: calcular credito disponible de tarjeta`
- `6c4049d` — `feat: exponer credito disponible de tarjeta`
- `9f9925c` — `feat: validar limite de tarjeta en consumos`
- `4a57efe` — `test: cubrir credito disponible de tarjeta`
- `fab7fd1` — `test: cubrir limite y credito disponible de tarjeta`
- `63dde05` — `fix: restaurar rollback original en CuentaService`

El último commit correctivo restauró una diferencia accidental y no relacionada con la funcionalidad: `modificarMoneda` vuelve a utilizar la variable `transaction` para hacer rollback, manteniendo la convención previa.

## Tests

Validación específica informada por el usuario:

- `CuentaTest` + `MovimientoServiceSaldoTest`: 23/23, 0 fallos, 0 errores.

Suite completa informada por el usuario:

- **650/650 tests**
- 0 fallos
- 0 errores
- BUILD SUCCESS
- 09/09/2026 14:35:11 -03:00

El usuario indicó además `Skipped 1 message` en la salida de Maven; el resumen de resultados reportó 0 tests omitidos.

## Auditoría del bloque

La revisión de los diffs de GitHub confirmó que los cambios funcionales principales son aditivos y están localizados en los puntos esperados: dominio `Cuenta`, servicios de cuenta/movimiento y tests correspondientes.

Se detectó y corrigió una modificación accidental no relacionada en el rollback de `CuentaService`.

## Próximo paso

Antes de construir la pantalla Swing específica de tarjetas, continuar con las reglas financieras del dominio.

El siguiente bloque candidato es definir e implementar ciclos de facturación y vencimientos, verificando primero `Cuenta`, `Movimiento`, `Obligacion`, servicios y tests actuales.

Después deberán resolverse pagos y liberación de crédito, y finalmente cuotas/financiación y UI específica de tarjetas.

No hacer merge a `main` automáticamente.
