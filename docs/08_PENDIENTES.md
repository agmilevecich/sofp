# SOFP — Pendientes

## Estado — 31/08/2026

Rama: `feature/seguridad-aislamiento-datos`.

## Seguridad

Los hallazgos previstos de esta etapa fueron abordados en código:

1. autorización de `OperacionFinancieraService`;
2. lecturas por ID y listados de recursos propios;
3. caminos alternativos de creación de cuentas, categorías, movimientos y perfiles;
4. aislamiento de posición y cartera por perfil/usuario;
5. cobertura específica de recursos propios y ajenos.

## Validación final

La auditoría fue validada localmente:

- `AislamientoDatosServiceTest`: **7/7 en verde**;
- suite general: **512/512 en verde**;
- `Failures: 0`;
- `Errors: 0`;
- `Skipped: 0`;
- `BUILD SUCCESS`.

El primer intento del test específico tuvo 7 fallos por un dato de prueba inválido: el código de moneda generado excedía `VARCHAR(10)`. Se corrigió únicamente el fixture y la segunda ejecución quedó 7/7 en verde.

## Estado de la auditoría

**IMPLEMENTACIÓN Y TESTS COMPLETADOS.**

Queda únicamente el cierre técnico del repositorio: sincronizar los commits documentales en la copia local, verificar `git status`, `git diff --check` y comparar nuevamente con `main`.

Swing continúa bloqueado hasta realizar ese cierre técnico. No hacer merge a `main` automáticamente.
