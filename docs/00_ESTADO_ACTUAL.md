# SOFP — Estado actual

> Documento de continuidad. El código y los tests actuales son la fuente de verdad técnica.

## Estado verificado — 31/08/2026

**Rama:** `feature/seguridad-aislamiento-datos`  
**Último commit de trabajo:** `a28f9027` — `fix: cerrar bypass interno de registro de movimiento`  
**Base:** `main`  
**main:** no fue modificada.

## Última validación previa a esta tanda

La última suite general informada fue **505/505 en verde**, `BUILD SUCCESS`. Esa validación corresponde al estado anterior a los cambios actuales de aislamiento de lecturas y altas.

Los cambios realizados desde entonces todavía requieren ejecución local de tests; no se registra todavía una nueva suite como pasada.

## Seguridad y aislamiento implementados

- `PerfilFinancieroService`: lecturas y alta requieren usuario propietario; las lecturas genéricas quedaron internas al paquete.
- `CuentaService`: lectura por ID, listado por perfil, saldo, evolución y alta tienen entrada autorizada por usuario; las APIs genéricas quedaron internas al paquete.
- `CategoriaService`: lectura por ID, listado por perfil y alta requieren usuario propietario; las APIs genéricas quedaron internas al paquete.
- `MovimientoService`: lectura por ID, listados por cuenta/categoría y alta requieren usuario propietario; las APIs genéricas quedaron internas al paquete.
- `PosicionActivoService`: la consulta pública requiere usuario propietario del perfil.
- `CarteraActivoService`: posiciones, valorizaciones, reporte, composición y movimientos requieren usuario propietario del perfil.
- `OperacionFinancieraService`: transferencia, compra y venta ya exigen usuario y validan propiedad de las cuentas/categorías involucradas.

## Cobertura agregada en esta tanda

Se agregó `AislamientoDatosServiceTest` para cubrir lectura propia, rechazo de recursos ajenos, altas con recursos ajenos y aislamiento de perfil/posición/cartera.

## Pendiente inmediato

1. Ejecutar tests específicos de seguridad.
2. Ejecutar suite completa.
3. Corregir cualquier fallo real detectado.
4. Ejecutar `git diff --check` y revisar `git status`.
5. Actualizar este documento con el resultado real.
6. Comparar finalmente la feature con `main`.

No considerar cerrada la etapa hasta tener la suite posterior a estos cambios en verde.
