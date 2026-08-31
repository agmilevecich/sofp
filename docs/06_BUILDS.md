# SOFP — Historial de Builds

## Build 059 — Suite general posterior a venta y posición

**Estado: COMPLETADO Y VALIDADO.**

Se ejecutó la suite general después de completar la cobertura de venta de activo, persistencia de relaciones e integración con posición.

Pruebas específicas previas:

- `OperacionFinancieraTest`: **17/17 tests en verde**.
- `OperacionFinancieraServiceTest`: **22/22 tests en verde**.
- `OperacionFinancieraCompraServiceTest`: **13/13 tests en verde**.
- `OperacionFinancieraVentaServiceTest`: **13/13 tests en verde**.
- `PosicionActivoServiceTest`: **4/4 tests en verde**.

Suite general ejecutada el **27/08/2026 15:24:11 -03:00**:

- Tests run: **433**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **17:35 min**

## Validaciones posteriores al Build 059

Se incorporaron y validaron identificación por símbolo, cartera de activos, costo promedio, valorización, reportes, evolución histórica y seguridad de perfil financiero.

## Etapa — Seguridad y aislamiento por usuario

La rama `feature/seguridad-aislamiento-datos` completó la auditoría transversal y posteriormente fue integrada en `main` mediante fast-forward.

Se implementaron y validaron:

- autorización de operaciones mutables de `CuentaService`;
- autorización de operaciones mutables de `CategoriaService`;
- autorización de operaciones mutables de `MovimientoService`;
- autorización de `OperacionFinancieraService` por usuario solicitante;
- lecturas por ID y listados protegidos por propietario;
- cálculo de saldo y evolución contextualizados por usuario;
- altas de cuentas, categorías, movimientos y perfiles con validación de propietario;
- aislamiento de posiciones y cartera por usuario/perfil;
- cierre de caminos internos que podían saltar las validaciones públicas;
- cobertura transversal mediante `AislamientoDatosServiceTest`.

## Validación final de seguridad

El **31/08/2026** se ejecutó primero `AislamientoDatosServiceTest`: **7/7 tests en verde**.

Luego se ejecutó la suite general:

- Tests run: **512**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:25 min**

La validación global vigente es **512/512 tests en verde**.

Durante la primera ejecución del test específico hubo 7 fallos por un fixture que generaba un código de moneda de 17 caracteres para una columna de máximo 10. Se corrigió el fixture sin alterar código de negocio y la segunda ejecución quedó completamente verde.

## Estado actual

La seguridad transversal está cerrada y forma parte de `main`.

La última integración de la etapa fue `75d0a18`. La copia local quedó con `working tree clean` y `git diff --check` sin salida.

## Próximo paso

**Fase 8 — Interfaz de usuario Swing.**

Antes de implementar UI se debe reconstruir desde `main` la estructura real de `src/main/java`, revisar servicios y tests, y definir el primer bloque Swing sin duplicar lógica de negocio.
