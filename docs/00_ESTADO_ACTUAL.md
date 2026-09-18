# SOFP — Estado actual

> Documento de continuidad. La fuente de verdad técnica es el código, los tests y los commits actuales; `docs/` es documentación auxiliar.

## Actualización de continuidad — 18/09/2026 20:35 -03:00

Esta sección supersede cualquier estado, validación o próximo paso anterior cuando exista contradicción.

### Estado Git

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- No se realizó merge a `main`.
- Último commit de código/documentación funcional antes de esta actualización: `0da28cc` — `test: cubrir estado de financiacion`.
- La rama contiene el bloque de financiación de tarjeta implementado y validado.

### Último bloque implementado — Financiación

Se incorporó la entidad persistente `Financiacion`, relacionada con `Obligacion`.

Implementado:

- `Financiacion` con obligación, fecha de inicio, capital original y saldo de capital.
- Validación de obligación, fecha e importe positivo.
- Registro de pagos sobre el capital sin permitir superar el saldo.
- Estados derivados `estaPendiente()` y `estaCancelada()`.
- Relación `Obligacion -> financiaciones` con cascade y orphan removal.
- Persistencia y recuperación de financiaciones.
- Inclusión de `Financiacion` en la unidad de persistencia de tests.
- Cobertura específica de la entidad y de su persistencia.
- Cobertura de los estados pendiente/cancelada.

Commits del bloque:

- `8252cff` — `feat: agregar relación de financiaciones a obligaciones`.
- `7994754` — `feat: agregar entidad de financiacion de tarjeta`.
- `f001f19` — `test: cubrir entidad de financiacion de tarjeta`.
- `a76a949` — `test: cubrir persistencia de financiacion`.
- `7c0c4aa` — `fix: importar financiacion en test de obligaciones`.
- `1a6a071` — `fix: incluir financiacion en unidad de persistencia de tests`.
- `8a4cd0a` — `feat: exponer estado de financiacion`.
- `0da28cc` — `test: cubrir estado de financiacion`.

### Validación más reciente informada por el usuario

`FinanciacionTest`:

- 9/9 tests.
- 0 failures.
- 0 errors.
- 0 skipped.
- `BUILD SUCCESS`.
- Finalizado: 18/09/2026 20:32:49 -03:00.

Suite completa más reciente conocida:

- `mvn test`: 779/779.
- 0 failures.
- 0 errors.
- 0 skipped.
- `BUILD SUCCESS`.
- Finalizada: 18/09/2026 15:04:44 -03:00.

La suite completa de 779 fue ejecutada antes de la validación específica de `FinanciacionTest`. No se registra una nueva suite completa posterior como resultado informado.

### Aislamiento JPA/H2

Se corrigió el aislamiento de `ObligacionRepositoryTest`: los tests cierran ahora el contexto JPA mediante `JpaTestManager.close()`, evitando reutilizar un `EntityManagerFactory` entre tests y los consiguientes conflictos de claves únicas en H2.

### Estado funcional de tarjetas

Resuelto y validado:

- consumos con obligación;
- ciclos, cuotas, vencimiento y gracia;
- valorización histórica de cierre;
- liquidación histórica explícita;
- pagos parciales y totales;
- tarjetas multidivisa;
- cálculo y liberación de crédito utilizado;
- selección histórica de cotización para liquidación;
- persistencia de financiación como modelo de dominio.

Todavía NO está implementado el flujo completo de negocio:

**pago parcial del resumen → creación automática de financiación → incorporación al siguiente resumen → intereses/cargos.**

### Decisiones vigentes para financiación

Por ahora `Financiacion` representa únicamente capital financiado. No se implementaron todavía intereses, TNA, punitorios, CFT ni refinanciación.

La regla de negocio acordada para el próximo bloque es: si un pago de tarjeta deja capital impago del resumen, esa parte podrá convertirse en financiación para el siguiente ciclo. La fecha de inicio propuesta es el día siguiente al vencimiento, pero debe verificarse contra el flujo actual de `PagoTarjetaService` antes de codificarla.

No inventar tasas ni fórmulas. La financiación de intereses se diseñará posteriormente con histórico de tasas, base diaria, fechas efectivas y redondeo explícitos.

### Próximo paso exacto

Antes de modificar código:

1. revisar el estado actual de `PagoTarjetaService`;
2. revisar `Obligacion`, `Financiacion` y los tests de pagos parciales;
3. determinar exactamente cómo queda `saldoPendiente) después de un pago parcial;
4. definir el punto mínimo donde el servicio crea la financiación;
5. agregar primero tests para pago parcial → financiación;
6. agregar test de pago total → sin financiación;
7. validar persistencia si corresponde;
8. ejecutar tests específicos y relacionados antes de una nueva suite completa.

El próximo cambio debe ser mínimo y no debe introducir todavía intereses ni tasas.

### Regla de continuidad

En la próxima sesión reconstruir nuevamente desde GitHub: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo.

No asumir resultados locales no informados. No modificar `main` automáticamente.
