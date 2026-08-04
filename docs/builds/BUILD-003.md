# Build 003: Documentación base de arquitectura

## Objetivo

Establecer y consolidar la documentación inicial de arquitectura de SOFP antes de incorporar el modelo de dominio y nuevas funcionalidades.

## Entregables

- Documento de visión del dominio financiero.
- Registro de decisiones de arquitectura iniciales (ADR).
- Roadmap de fases previstas del proyecto.
- Definición de los conceptos transversales `Moneda` y `EntidadAuditable`.
- Rediseño conceptual de operaciones financieras y movimientos.

## Decisiones tomadas

- SOFP se definió como sistema multiusuario.
- `PerfilFinanciero` será el contexto de la información financiera de cada usuario.
- Los instrumentos se organizarán bajo la abstracción `Activo` y sus tipos especializados.
- `Moneda` será una entidad transversal relacionada con cuentas, activos, operaciones y movimientos.
- `EntidadAuditable` centralizará los campos de creación y actualización para aportar trazabilidad.
- `OperacionFinanciera` se separará de sus efectos: `MovimientoCuenta` y `MovimientoActivo`.
- Los saldos y posiciones se derivarán de los movimientos registrados para mantener trazabilidad.

## Alcance del build

Este build incorpora exclusivamente documentación Markdown. No agrega código Java, entidades, repositorios, servicios, dependencias ni cambios de configuración.
