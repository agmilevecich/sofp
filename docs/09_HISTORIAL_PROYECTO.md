# SOFP — Historial del proyecto

## 2026-08-31 — Seguridad y aislamiento de datos

La feature `feature/seguridad-aislamiento-datos` avanzó desde la autorización de operaciones financieras hacia una revisión transversal de lecturas y caminos de creación.

Correcciones principales:

- `85a4e86` — autorización de operaciones financieras por usuario;
- `b0e6377` — cobertura de aislamiento por usuario;
- `b2484a8` — protección de lecturas de cuenta;
- `a214d16` — protección de lecturas de categoría;
- `5b7de3a` — protección de lecturas y alta de movimientos;
- `d62dbb9` — protección de lecturas de perfil y cartera;
- `2381ea5` — protección del alta de perfil financiero;
- `a28f902` — cierre del bypass interno de registro de movimiento.

Se agregó `AislamientoDatosServiceTest` con cobertura específica de lectura propia/ajena, altas con recursos ajenos y aislamiento de perfil, posición y cartera.

La última suite general conocida antes de estos cambios fue **505/505 en verde**. La nueva tanda aún requiere ejecución local y no se registra como validada hasta disponer de ese resultado.

## Estado

`main` permanece estable. La siguiente decisión depende de la validación local de los cambios actuales.
