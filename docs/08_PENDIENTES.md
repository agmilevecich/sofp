# SOFP — Pendientes

## Estado — 10/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `548063914ad7a3fe6ae028dad606aad57f7ca42e`.

GitHub verifica **443 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

El último commit (`5480639`) es documental. El último cambio funcional es `46290786` — `fix: cerrar contexto JPA de PosicionActivoServiceTest`.

## Último bloque cerrado

### Crédito disponible, límite y aislamiento de tests

**Completado y validado.**

Se implementó el criterio inicial:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

No hay conversión implícita entre monedas. Se valida el límite al registrar consumos con tarjeta y al modificar importe/tipo. Los consumos con tarjeta no afectan el saldo monetario de la cuenta. Los consumos sin obligación se consideran sin doble contabilización.

También se corrigió el aislamiento JPA/H2 de la suite: `JpaTestManager` mantiene el contexto por hilo y `PosicionActivoServiceTest` cierra el `EntityManagerFactory` en `@AfterEach`.

## Ciclos y vencimientos

**Base de dominio ya implementada y testeada.**

`CicloFacturacion` existe como objeto no persistente y `Cuenta.calcularCicloFacturacion(LocalDate)` calcula inicio, cierre y vencimiento.

`CicloFacturacionTest` contiene **9 tests** para cierre exacto, ciclo siguiente, meses cortos, febrero, vencimiento posterior al cierre y cambio de año.

Por lo tanto, ciclos/vencimientos ya no son una tarea de implementación desde cero. El pendiente es integrar esta lógica al flujo de consumos, obligaciones y posteriormente pagos/UI.

## Pendientes en orden

1. **Integrar ciclos de facturación y vencimientos con consumos/obligaciones.**
2. **Unificar el tratamiento del saldo de tarjetas** entre `MovimientoService` y `CuentaService`, evitando que una misma regla tenga resultados distintos.
3. **Profundizar pagos de tarjeta y liberación de crédito**, incluyendo las reglas que dependan del ciclo y moneda.
4. **Definir e implementar cuotas y financiación.**
5. **Construir UI específica de tarjetas**, una vez estabilizadas las reglas del dominio.
6. **Ampliar pasivos y patrimonio neto.**
7. **Análisis histórico, resúmenes, vencimientos y dashboard.**
8. **Pulido de consola**, de baja prioridad.

## Validación actual

`mvn test` — **664/664**, Failures 0, Errors 0, Skipped 0, `BUILD SUCCESS`, ejecutado por el usuario el 09/09/2026 21:58:35 -03:00, duración 10:04 min.

## Estado local informado

Tras `git syncsofp`: rama sincronizada, `git diff` sin cambios versionados, `git diff --check` sin salida y solo `surefire-debug.txt` sin rastrear. Ese archivo es un artefacto local de diagnóstico y no debe agregarse al repositorio.

## Bloques cerrados relevantes

- Seguridad y aislamiento de datos.
- Categorías con movimientos.
- Fondos insuficientes y reglas de saldo.
- Gastos e Ingresos sobre `Movimiento`.
- FormaPago.
- Obligaciones y pagos.
- Autorización de pagos.
- UI Swing de obligaciones, ingresos, gastos y transferencias.
- Transferencias mediante `OperacionFinanciera`.
- Moneda explícita en movimientos y obligaciones.
- Crédito disponible y validación de límite de tarjeta.
- Liberación de crédito mediante pagos.
- Ciclo de facturación en dominio y tests.
- Aislamiento JPA/H2 de la suite.

## Integración

No hacer merge a `main` automáticamente ni crear ramas nuevas salvo indicación explícita.

Antes de cerrar un bloque: tests específicos → relacionados → suite general cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

## Continuidad

En una nueva sesión reconstruir siempre desde GitHub: rama → commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.
