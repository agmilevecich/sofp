# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

## Estado — 31/08/2026

Rama de trabajo: `feature/seguridad-aislamiento-datos`.
Último commit: `a28f9027` — `fix: cerrar bypass interno de registro de movimiento`.
`main` no fue modificada.

## Seguridad

La auditoría transversal avanzó desde operaciones mutables hasta lecturas y caminos alternativos de creación.

Implementado:

- autorización de operaciones financieras;
- aislamiento de cuentas, categorías y movimientos;
- lecturas por ID/listados con usuario propietario;
- altas protegidas de cuentas, categorías, movimientos y perfiles;
- aislamiento de posiciones y cartera por usuario/perfil;
- cobertura específica en `AislamientoDatosServiceTest`.

## Última validación conocida

Antes de los cambios actuales, la suite general informada fue **505/505 en verde**. No se asume que ese resultado cubra los cambios posteriores.

## Próximo paso obligatorio

Ejecutar los tests sobre el estado actual, corregir cualquier fallo, ejecutar la suite completa, revisar `git diff --check`, `git status` y comparar con `main`.

No comenzar Swing hasta cerrar formalmente esta validación.
