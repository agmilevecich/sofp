# SOFP — Historial del proyecto

## 2026-08-31 — Cierre de seguridad y aislamiento de datos

La feature `feature/seguridad-aislamiento-datos` completó la revisión transversal de seguridad y aislamiento de recursos por usuario/perfil.

Correcciones principales:

- `85a4e86` — autorización de operaciones financieras por usuario;
- `b0e6377` — cobertura de aislamiento por usuario;
- `b2484a8` — protección de lecturas de cuenta;
- `a214d16` — protección de lecturas de categoría;
- `5b7de3a` — protección de lecturas y alta de movimientos;
- `d62dbb9` — protección de lecturas de perfil y cartera;
- `2381ea5` — protección del alta de perfil financiero;
- `a28f902` — cierre del bypass interno de registro de movimiento;
- `8f5c07f` — corrección de datos de prueba de aislamiento.

Se agregó `AislamientoDatosServiceTest` para cubrir lectura propia/ajena, altas con recursos ajenos y aislamiento de perfil, posición y cartera.

Validación final local:

- `AislamientoDatosServiceTest`: **7/7 en verde**;
- suite general: **512/512 tests en verde**;
- Failures: 0;
- Errors: 0;
- Skipped: 0;
- `BUILD SUCCESS`;
- duración: **15:25 min**.

El primer intento del test específico tuvo 7 fallos por un código de moneda de prueba de longitud superior a `VARCHAR(10)`. Se corrigió el fixture y la segunda ejecución quedó 7/7 en verde.

La feature fue integrada en `main` mediante fast-forward y publicada en GitHub y Bitbucket.

## Estado actual

**Fases 1 a 7: cerradas.**

`main` es ahora la base estable del proyecto y contiene el cierre de seguridad.

**Siguiente etapa: Fase 8 — Interfaz de usuario Swing.**

La implementación de Swing todavía no comenzó. El trabajo deberá partir del código real existente y mantener separadas la interfaz y la lógica de negocio.
