# Decisiones de arquitectura

Este documento registra las decisiones de arquitectura (ADR) del proyecto SOFP y distingue entre decisiones vigentes e ideas de evolución todavía no implementadas.

## ADR-001: Sistema multiusuario

**Decisión:** SOFP será un sistema multiusuario.

**Motivo:** Cada usuario debe conservar una administración independiente de su información financiera.

**Consecuencia:** La información financiera deberá estar asociada a un usuario o a su perfil financiero desde el diseño del dominio.

## ADR-002: Uso de PerfilFinanciero

**Decisión:** Se utilizará `PerfilFinanciero` como contexto de pertenencia de la información financiera de un usuario.

**Motivo:** Permite agrupar inversiones, posiciones y movimientos bajo una representación explícita del perfil financiero.

**Consecuencia:** Los elementos del dominio financiero se relacionarán con un perfil financiero cuando corresponda.

## ADR-003: Herencia para Activo

**Decisión:** Los instrumentos financieros se modelarán mediante la abstracción `Activo` y sus especializaciones `FondoComun`, `Bono`, `Acción`, `Criptomoneda`, `PlazoFijo` y `Divisa`.

**Motivo:** Los instrumentos comparten conceptos comunes, pero cada uno requiere atributos y reglas propias.

**Consecuencia:** El modelo podrá reutilizar comportamiento común y agregar particularidades por tipo de activo.

## ADR-004: Saldos calculados desde movimientos

**Decisión:** Los saldos de las cuentas se calculan a partir de los `Movimiento` registrados.

**Estado actual:** `CuentaService.calcularSaldo(...)` ya implementa esta regla utilizando los movimientos de la cuenta. Un `INGRESO` suma al saldo y un `EGRESO` resta.

**Motivo:** Los movimientos aportan trazabilidad sobre el origen y la variación del saldo.

**Evolución:** Cuando se incorpore el cálculo de posiciones de activos, deberá definirse el mecanismo correspondiente para esos activos. No se deben presentar `MovimientoCuenta` o `MovimientoActivo` como entidades actualmente implementadas.

## ADR-005: Moneda como entidad transversal

**Decisión:** Se incorporará `Moneda` como entidad transversal del dominio.

**Motivo:** Cuentas, activos, operaciones y movimientos necesitan expresar importes o cotizaciones en una unidad monetaria consistente.

**Consecuencia:** Las relaciones monetarias se modelarán mediante `Moneda`, evitando duplicar la definición de una divisa o asumir una moneda implícita en los valores financieros.

## ADR-006: EntidadAuditable como base transversal

**Decisión:** Se utilizará `EntidadAuditable` como clase base de los elementos que requieran auditoría.

**Motivo:** La información financiera necesita trazabilidad de creación y actualización.

**Consecuencia:** Las entidades auditables compartirán los campos `fechaCreacion`, `fechaActualizacion`, `creadoPor` y `actualizadoPor`.

## ADR-007: Separación entre operación y movimientos

**Decisión vigente:** `OperacionFinanciera` representa el hecho de negocio y puede agrupar los `Movimiento` que genera. En el modelo actual una operación puede tener como máximo dos movimientos.

**Motivo:** La operación conserva el contexto del hecho financiero y los movimientos representan sus efectos monetarios sobre las cuentas.

**Estado actual:** La relación entre `Movimiento` y `OperacionFinanciera` está implementada y validada en Build 049.

**Evolución futura:** La eventual separación entre movimientos de cuentas y movimientos de posiciones de activos podrá evaluarse cuando el dominio de inversiones lo requiera. Esa separación no forma parte del modelo actual.
