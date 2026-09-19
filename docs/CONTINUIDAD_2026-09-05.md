# SOFP — Continuidad 2026-09-07

> **Corte histórico.** Este documento conserva el estado de ese momento y no debe utilizarse como fuente actual. El estado vigente al 11/09/2026 está consolidado en `docs/CONTINUIDAD_2026-09-11.md`, `docs/00_ESTADO_ACTUAL.md` y `docs/CHAT_CONTEXT_FINAL.md`.

## Estado histórico

Rama estable: `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
Rama de trabajo: `feature/swing-shell`.

La comparación y los commits descritos a continuación corresponden al corte histórico del documento.

## Últimos cambios funcionales históricos

Se cerró el bloque específico de reglas de saldo de movimientos.

`MovimientoServiceSaldoTest` agregó casos para rechazo de egreso superior al saldo, aceptación de egreso igual al saldo y aumento de importe hasta el saldo disponible.

El primer intento falló porque el fixture utilizaba un overload interno de `MovimientoService.registrar`; se corrigió pasando `usuario.getId()` en los registros correspondientes. No fue necesario modificar producción.

## Estado funcional histórico

La Fase 8 continuaba sobre el shell Swing integrado con Inicio, Cuentas, Categorías, Gastos, Movimientos, Inversiones y Reportes.

La arquitectura funcional acordada era:

**paneles especializados → servicios específicos → núcleo financiero central basado en `Movimiento`.**

## FormaPago — histórico

`FormaPago` estaba integrada al dominio de `Movimiento` y al flujo de Gastos. En ese corte, `TARJETA_CREDITO` todavía se rechazaba porque el modelo de obligaciones/pasivos aún no estaba terminado.

## Reglas de continuidad

La fuente de verdad vigente es siempre el código, tests y commits actuales. `docs/` es documentación auxiliar.

Antes de cualquier cambio: revisar rama, últimos commits, comparación con `main`, implementación, clases relacionadas, servicios, repositorios, tests y reglas de negocio.

No modificar `main`, no crear ramas nuevas salvo indicación explícita y no asumir tests ejecutados sin resultado informado.

Después de cambios importantes: tests específicos, relacionados y suite completa cuando corresponda; `git diff`, `git diff --check` y `git status`.

## Referencia vigente

Para continuar el proyecto, utilizar el estado del 11/09/2026 en `docs/CONTINUIDAD_2026-09-11.md` y reconstruir siempre desde GitHub.
