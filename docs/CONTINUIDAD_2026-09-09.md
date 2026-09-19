# SOFP — Continuidad 2026-09-09

> **Corte histórico.** El estado vigente al 11/09/2026 está consolidado en `docs/CONTINUIDAD_2026-09-11.md`, `docs/00_ESTADO_ACTUAL.md` y `docs/CHAT_CONTEXT_FINAL.md`.

## Estado histórico

Este documento registra el bloque histórico de límite y crédito disponible de tarjetas. Ese bloque posteriormente evolucionó hacia ciclos de facturación, obligaciones, cuotas, pagos y la integración Swing.

La rama de trabajo `feature/swing-shell` continúa separada de `main`.

## Reglas históricas relevantes

El crédito disponible se definió inicialmente como límite de crédito menos consumos pendientes de tarjeta en la misma moneda, sin conversiones implícitas.

El bloque fue validado y posteriormente ampliado con las reglas de ciclos, obligaciones y cuotas que existen actualmente en el código.

## Referencia vigente

No utilizar los conteos, commits ni pendientes de este corte como fuente actual. Para continuar SOFP, reconstruir desde GitHub y utilizar `docs/CONTINUIDAD_2026-09-11.md` como referencia documental más reciente, siempre subordinada al código y tests actuales.

No hacer merge a `main` automáticamente.
