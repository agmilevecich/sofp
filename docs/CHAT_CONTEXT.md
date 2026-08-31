# SOFP — Contexto para continuar con ChatGPT

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

## Estado — 31/08/2026

Rama estable: `main`.
Último commit integrado al momento de esta actualización: `75d0a18`.
La rama `feature/seguridad-aislamiento-datos` fue integrada en `main` mediante fast-forward.
GitHub y Bitbucket quedaron sincronizados y la copia local informó `working tree clean`.

## Seguridad

La auditoría transversal de seguridad y aislamiento de datos quedó completada e integrada en `main`.

Implementado:

- autorización de operaciones financieras;
- aislamiento de cuentas, categorías y movimientos;
- lecturas por ID y listados con usuario propietario;
- altas protegidas de cuentas, categorías, movimientos y perfiles;
- aislamiento de posiciones y cartera por usuario/perfil;
- cierre de caminos internos que podían saltar validaciones públicas;
- cobertura transversal en `AislamientoDatosServiceTest`.

## Validación final

`AislamientoDatosServiceTest`: **7/7 en verde**.

Suite general ejecutada localmente el **31/08/2026**:

- Tests run: **512**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:25 min**

La validación vigente es **512/512 tests en verde**.

La primera ejecución del test específico tuvo 7 fallos por un dato de prueba que excedía la longitud de la columna `Moneda.codigo`; se corrigió el fixture y la segunda ejecución quedó 7/7 en verde.

## Cierre de seguridad

La auditoría está cerrada funcional y técnicamente. La feature fue integrada en `main` mediante fast-forward y publicada en GitHub y Bitbucket.

No queda pendiente de esta etapa ningún merge de seguridad.

## Próxima etapa

**Fase 8 — Interfaz de usuario Swing.**

Antes de modificar código se debe reconstruir desde `main` la estructura real de `src/main/java`, revisar las clases y servicios disponibles para UI y verificar tests y convenciones existentes.

La implementación de Swing todavía no comenzó.
