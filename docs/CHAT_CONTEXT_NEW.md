# SOFP — Contexto para continuar con ChatGPT

## Estado — 12/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y puede quedar desactualizada. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

Último commit de código: `6c1b896` — `build: configurar jar ejecutable y dependencias`.

Suite general más reciente informada: **690/690**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Estado consolidado

La Fase 8 Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. La compra con tarjeta es atómica y tiene cobertura de rollback. `PagoTarjetaService` coordina el pago real de deuda con la salida de fondos. H2 de aplicación usa TCP y los tests usan H2 en memoria aislado. El JAR ejecutable está configurado y probado.

## Auditoría — trabajo pendiente

### P0

1. Proteger movimiento origen de obligación frente a cambio de importe, fecha/hora, tipo y eliminación.
2. Revisar superficies públicas de `ObligacionService` sin `usuarioId`.
3. Integrar `PagoTarjetaService` en `ObligacionesPanel`, con cuenta pagadora y categoría.

### P1

4. Proteger cambios de tipo/moneda de `Cuenta` cuando exista historial.
5. Definir reglas de ciclo aplicadas al pago: vencimiento, mora, gracia y días no hábiles.
6. Definir multidivisa de tarjetas sin conversiones implícitas.
7. Definir financiación avanzada.

### P2/P3

8. UI específica de tarjetas.
9. Pasivos, patrimonio, histórico, vencimientos, resúmenes y dashboard.
10. Pulido de consola.

## Ya implementado

- selección explícita de tarjeta activa en Gastos;
- generación automática de cuotas;
- ciclos de facturación y cruce de año;
- atomicidad de compra con tarjeta;
- pago coordinado en servicio;
- criterio actual de crédito disponible;
- H2 TCP;
- JAR ejecutable.

## Regla de continuidad

Antes de cada cambio: revisar implementación, clases relacionadas, repositorios, tests y reglas de negocio. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main`, no asumir resultados locales no informados y no considerar terminado un bloque solo porque compila.