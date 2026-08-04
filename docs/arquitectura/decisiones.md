# Decisiones de arquitectura

Este documento registra las decisiones iniciales de arquitectura (ADR) del proyecto SOFP.

## ADR-001: Sistema multiusuario

**Decisión:** SOFP será un sistema multiusuario.

**Motivo:** Cada usuario debe conservar una administración independiente de su información financiera.

**Consecuencia:** La información financiera deberá estar asociada a un usuario o a su perfil financiero desde el diseño del dominio.

## ADR-002: Uso de PerfilFinanciero

**Decisión:** Se utilizará `PerfilFinanciero` como contexto de pertenencia de la información financiera de un usuario.

**Motivo:** Permite agrupar inversiones, posiciones y movimientos bajo una representación explícita del perfil financiero.

**Consecuencia:** Los futuros elementos del dominio financiero se relacionarán con un perfil financiero.

## ADR-003: Herencia para Activo

**Decisión:** Los instrumentos financieros se modelarán mediante la abstracción `Activo` y sus especializaciones `FondoComun`, `Bono`, `Acción`, `Criptomoneda`, `PlazoFijo` y `Divisa`.

**Motivo:** Los instrumentos comparten conceptos comunes, pero cada uno requiere atributos y reglas propias.

**Consecuencia:** El modelo podrá reutilizar comportamiento común y agregar particularidades por tipo de activo.

## ADR-004: Saldos calculados desde movimientos

**Decisión:** Los saldos de cuentas y las posiciones de activos se calcularán a partir de los movimientos registrados.

**Motivo:** Los movimientos aportan trazabilidad sobre el origen y la variación de cada saldo o posición.

**Consecuencia:** Las consultas de saldo deberán reconstruir o derivar los valores desde el historial de `MovimientoCuenta` y `MovimientoActivo`, en lugar de mantenerlos como fuente primaria independiente.

## ADR-005: Moneda como entidad transversal

**Decisión:** Se incorporará `Moneda` como entidad transversal del dominio.

**Motivo:** Cuentas, activos, operaciones y movimientos necesitan expresar importes o cotizaciones en una unidad monetaria consistente.

**Consecuencia:** Las relaciones monetarias se modelarán mediante `Moneda`, evitando duplicar la definición de una divisa o asumir una moneda implícita en los valores financieros.

## ADR-006: EntidadAuditable como base transversal

**Decisión:** Se utilizará `EntidadAuditable` como clase base de los elementos que requieran auditoría.

**Motivo:** La información financiera necesita trazabilidad de creación y actualización.

**Consecuencia:** Las entidades auditables compartirán los campos `fechaCreacion`, `fechaActualizacion`, `creadoPor` y `actualizadoPor`.

## ADR-007: Separación entre operación y movimientos

**Decisión:** `OperacionFinanciera` representará el hecho de negocio y generará los movimientos que afectan saldos y posiciones. Se distinguirán `MovimientoCuenta` y `MovimientoActivo`.

**Motivo:** Una operación puede impactar simultáneamente fondos de una cuenta y unidades de un activo, por lo que el evento registrado y sus efectos deben mantenerse diferenciados.

**Consecuencia:** El sistema preservará el contexto de cada operación y calculará saldos desde movimientos especializados por tipo de posición.
