# SOFP — Documentación

La documentación acompaña al código, pero la fuente de verdad es siempre el estado actual de Git, el código y los tests.

## Continuidad actual

- `CHAT_CONTEXT.md`: contexto para nuevas conversaciones.
- `CHAT_CONTEXT_FINAL.md`: contexto compacto de continuidad.
- `00_ESTADO_ACTUAL.md`: estado funcional y técnico vigente.
- `05_DECISIONES.md`: decisiones arquitectónicas y de negocio permanentes.
- `06_BUILDS.md`: historial de Builds y validaciones.
- `07_TESTS.md`: estado y cobertura de tests.
- `08_PENDIENTES.md`: pendientes reales y próximos bloques.
- `09_HISTORIAL_PROYECTO.md`: evolución e hitos.
- `09_TARJETAS_CREDITO.md`: diseño y estado de tarjetas.
- `CONTINUIDAD_2026-09-05.md`, `CONTINUIDAD_2026-09-08.md` y `CONTINUIDAD_2026-09-09.md`: cortes históricos; no deben utilizarse como fuente del estado vigente.
- `CONTINUIDAD_2026-09-11.md`: último corte de continuidad y referencia documental vigente.

## Estado vigente — 11/09/2026

Rama de trabajo: `feature/swing-shell`.

Último commit funcional: `34eb4cc` — `config: conectar SOFP a H2 por TCP`.

`main` → `a4be85913847200cb70976d5266d9cbba10b3100`.

Antes de la actualización documental, GitHub verificó **495 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Persistencia

La aplicación usa H2 por TCP:

`jdbc:h2:tcp://localhost/./database/sofp`

H2 Server funciona en `localhost:9092` y H2 Console en `localhost:8082`. SOFP y H2 Console fueron comprobados simultáneamente sobre la misma base persistente.

Los tests utilizan un `persistence.xml` separado con H2 en memoria.

## Estado funcional

La Fase 8 integra Inicio, Cuentas, Categorías, Ingresos, Gastos, Movimientos, Inversiones, Reportes, Obligaciones y Transferencias mediante `MainFrame`, `SidebarPanel` y `CardLayout`.

Arquitectura: **paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

Las compras con tarjeta de crédito generan obligaciones y cuotas automáticas. La moneda del consumo se conserva en la obligación. El crédito disponible inicial se calcula como límite menos consumos pendientes en la moneda de la tarjeta, sin conversiones implícitas.

`CicloFacturacion` ya está implementado como objeto de dominio no persistente y queda pendiente su integración completa con consumos, obligaciones y pagos.

## Validación más reciente

`mvn test` — **687/687**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, ejecutado por el usuario el **11/09/2026 13:14:41 -03:00**, duración **18:31 min**.

## Próximo trabajo

1. Selección explícita de tarjeta de crédito en `GastosPanel`.
2. Tests para tarjetas activas, cuentas de otros tipos y selección correcta.
3. Integración de ciclos con consumos y obligaciones.
4. Unificación del saldo monetario de tarjetas.
5. Profundizar pagos/liberación de crédito y reglas multidivisa.
6. Pasivos/patrimonio, análisis y dashboard.
7. Pulido de consola.

## Regla de continuidad

Antes de cualquier cambio reconstruir desde GitHub: rama → commits → comparación con `main` → README/docs → código → tests → último resultado conocido → próximo paso.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir resultados locales no informados.
