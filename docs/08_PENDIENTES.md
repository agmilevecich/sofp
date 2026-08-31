# SOFP — Pendientes

## Estado — 31/08/2026

**Rama estable:** `main`.  
**Último commit integrado:** `75d0a18`.

## Seguridad

La etapa de seguridad y aislamiento de datos está **cerrada e integrada en `main`**.

Se completaron:

1. autorización de `OperacionFinancieraService`;
2. lecturas por ID y listados de recursos propios;
3. caminos alternativos de creación de cuentas, categorías, movimientos y perfiles;
4. aislamiento de posición y cartera por perfil/usuario;
5. cierre de caminos internos que podían saltar validaciones públicas;
6. cobertura específica de recursos propios y ajenos.

## Validación final

La auditoría fue validada localmente:

- `AislamientoDatosServiceTest`: **7/7 en verde**;
- suite general: **512/512 en verde**;
- `Failures: 0`;
- `Errors: 0`;
- `Skipped: 0`;
- `BUILD SUCCESS`;
- duración: **15:25 min**.

El primer intento del test específico tuvo 7 fallos por un dato de prueba inválido: el código de moneda generado excedía `VARCHAR(10)`. Se corrigió el fixture y la segunda ejecución quedó 7/7 en verde.

## Próximo bloque pendiente

**Fase 8 — Interfaz de usuario Swing.**

Antes de implementar UI se debe revisar desde `main` la estructura real de `src/main/java`, las clases y servicios disponibles, los tests y las convenciones existentes.

Primer objetivo previsto: definir e implementar el shell principal de Swing sin duplicar lógica de negocio ni crear abstracciones no justificadas por el código actual.

## Criterio de continuidad

No asumir que una conversación anterior refleja el estado actual. Ante una nueva sesión de SOFP, reconstruir el estado desde GitHub: código → tests → commits → `main` → documentación.
