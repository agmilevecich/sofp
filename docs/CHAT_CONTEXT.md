# SOFP — Contexto para continuar con ChatGPT

## Estado auditado — 14/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** reglas temporales de ciclos y pagos de tarjeta.
**Último commit funcional/test:** `3a001a57` — `test: adaptar persistencia de obligaciones a reglas temporales`.
**Último bloque documental:** auditoría integral posterior al cierre temporal.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 13/09/2026 22:05:14 -03:00.

`ObligacionJpaTest`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 13/09/2026 21:12:49 -03:00.

Validaciones anteriores relevantes: integridad de `Cuenta` **66/66** específicos y **154/154** relacionados; `ObligacionServiceTest` **9/9**; suite de obligaciones/pagos/UI **69/69**; UI de pago **6/6**.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`ObligacionService` exige `usuarioId` para registrar pagos y valida el perfil propietario.

`CuentaService` protege la integridad estructural: no permite cambiar tipo ni moneda cuando existen movimientos y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

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

## Resultado de auditoría integral

La arquitectura general está consolidada y no requiere rehacerse.

El principal hueco funcional es la multidivisa. Se confirmaron:

1. saldos de cuenta que pueden mezclar monedas;
2. validación de fondos que utiliza ese saldo mezclado;
3. consumos de tarjeta en moneda distinta que no tienen una regla completa de impacto sobre el límite;
4. pagos que exigen misma moneda y todavía no cuentan con liquidación/conversión explícita.

No se deben introducir conversiones implícitas. Primero deben definirse moneda de liquidación, tasa, fecha/fuente de cotización y representación de saldos por moneda.

Hallazgos secundarios: validar rango de `Moneda.cantidadDecimales`, definir política de eliminación de cuentas con historial, evaluar `Clock` y migraciones/versionado de esquema para una futura etapa no local.

La auditoría completa está en `docs/11_AUDITORIA_INTEGRAL.md`.

## Pendientes reales

P0/P1:

1. Resolver multidivisa de tarjetas por moneda.
2. Cubrir saldos, fondos, crédito y pagos multidivisa con tests.

P2:

3. Robustez de `Moneda`.
4. Política de eliminación histórica de cuentas.
5. Abstracción `Clock`.
6. Migraciones/versionado de esquema si corresponde.

P3:

7. Financiación avanzada.
8. UI específica de tarjetas.
9. Pasivos, patrimonio y análisis.
10. Gestión de entidades financieras.
11. Pulido de consola.

## Protocolo

Antes de cada bloque: reconstruir desde GitHub rama → últimos commits → comparación con `main` → documentación → implementación → clases relacionadas → tests → auditoría vigente → último resultado informado. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

La documentación de continuidad se actualiza sobre la rama activa. No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
