# SOFP — Historial de Builds

## Build 060 — Crédito, ciclos y aislamiento JPA

**Estado: COMPLETADO Y VALIDADO.**

El bloque reciente consolidó el crédito disponible de tarjetas, la validación de límites, la conservación de consumos sin obligación y el aislamiento del contexto JPA/H2 en tests.

Regla actual:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

No se realizan conversiones implícitas entre monedas.

`CicloFacturacion` y `Cuenta.calcularCicloFacturacion(LocalDate)` también quedaron incorporados al dominio. `CicloFacturacionTest` contiene **9 tests** para cierre, ciclo siguiente, meses cortos, vencimientos y cambio de año.

## Validación general — 09/09/2026 21:58:35 -03:00

- `mvn test`;
- **664/664**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **10:04 min**.

Validaciones focalizadas conocidas:

- `TarjetaCreditoPagoCreditoTest`: **5/5**;
- `CicloFacturacionTest`: **9 tests** incluidos en la suite general.

## Diagnóstico JPA/H2

El fallo previo de duplicación de `ARS` en `TarjetaCreditoPagoCreditoTest` se explicó por reutilización del contexto JPA/H2 entre tests. `JpaTestManager` fue aislado por hilo y `PosicionActivoServiceTest` ahora cierra el `EntityManagerFactory` mediante `@AfterEach`.

Commits relevantes:

- `e8f6fdb` — `fix: aislar contexto JPA entre hilos de test`.
- `46290786` — `fix: cerrar contexto JPA de PosicionActivoServiceTest`.

## Fase 8 — Swing

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta y ciclos básicos de facturación.

## Próximo bloque

Integrar los ciclos ya existentes con consumos y obligaciones; revisar y unificar el cálculo de saldo de tarjetas entre `MovimientoService` y `CuentaService`. Después profundizar pagos, cuotas/financiación y UI específica de tarjetas.

## Estado Git

`main` → `a4be859...`.
`feature/swing-shell` → `548063914ad7a3fe6ae028dad606aad57f7ca42e`.
GitHub verifica **443 adelante / 0 atrás**.

No hacer merge a `main` automáticamente.
