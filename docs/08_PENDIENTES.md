# SOFP — Pendientes

## Estado — 31/08/2026

Rama: `feature/seguridad-aislamiento-datos`.

Últimos cambios de seguridad:

- `85a4e86` — autorización de operaciones financieras por usuario;
- `b0e6377` — cobertura de aislamiento por usuario;
- `b2484a8` — protección de lecturas de cuenta;
- `a214d16` — protección de lecturas de categoría;
- `5b7de3a` — protección de lecturas y alta de movimientos;
- `d62dbb9` — protección de lecturas de perfil y cartera;
- `2381ea5` — protección del alta de perfil financiero;
- `a28f902` — cierre del bypass interno de registro de movimiento.

## Seguridad

Los hallazgos previstos de esta etapa fueron abordados en código:

1. autorización de `OperacionFinancieraService`;
2. lecturas por ID y listados de recursos propios;
3. caminos alternativos de creación de cuentas, categorías, movimientos y perfiles;
4. aislamiento de posición y cartera por perfil/usuario;
5. cobertura específica de recursos propios y ajenos.

## Pendiente de validación

La última suite conocida antes de esta tanda fue **505/505 en verde**. Falta ejecutar los tests sobre el estado actual y corregir cualquier fallo que aparezca.

Después de la validación: `git diff --check`, `git status`, comparación con `main` y cierre documental de la auditoría.

Swing continúa bloqueado hasta completar esta validación final.
