# SOFP — Estado actual

> Documento de continuidad. El código y los tests actuales son la fuente de verdad técnica.

## Estado verificado — 31/08/2026

**Rama estable:** `main`  
**Último commit integrado:** `75d0a18` — `docs: actualizar contexto tras cierre de seguridad`  
**Feature integrada:** `feature/seguridad-aislamiento-datos` mediante fast-forward.  
**GitHub y Bitbucket:** sincronizados en `75d0a18`.  
**Working tree local:** limpio.

## Validación final de seguridad

La auditoría transversal de seguridad y aislamiento de datos quedó completada antes de integrar la feature en `main`.

Suite general ejecutada localmente el **31/08/2026**:

- Tests run: **512**
- Failures: **0**
- Errors: **0**
- Skipped: **0**
- `BUILD SUCCESS`
- Duración: **15:25 min**

`AislamientoDatosServiceTest`: **7/7 en verde**.

La primera ejecución de ese test tuvo 7 fallos por datos de prueba inválidos: el código de moneda generado excedía `VARCHAR(10)`. Se corrigió el fixture; la segunda ejecución quedó 7/7 en verde.

## Seguridad implementada

- `PerfilFinancieroService`: lecturas y alta protegidas por usuario propietario.
- `CuentaService`: lecturas por ID, listados por perfil, saldo, evolución y alta protegidos por usuario.
- `CategoriaService`: lectura por ID, listado por perfil y alta protegidos por usuario.
- `MovimientoService`: lectura por ID, listados por cuenta/categoría y alta protegidos por usuario.
- `PosicionActivoService`: consulta pública protegida por propietario del perfil.
- `CarteraActivoService`: posiciones, valorizaciones, reporte, composición y movimientos protegidos por propietario del perfil.
- `OperacionFinancieraService`: transferencia, compra y venta exigen usuario y validan propiedad de los recursos involucrados.
- Se cerraron caminos internos que podían permitir saltar validaciones públicas.
- `AislamientoDatosServiceTest` cubre recursos propios y ajenos y los principales caminos de lectura/creación.

## Estado de la interfaz

La implementación de Swing todavía no comenzó.

La siguiente etapa es **Fase 8 — Interfaz de usuario**, partiendo del estado real del código y servicios existentes, sin asumir clases de UI no implementadas.

## Próximo paso

1. Reconstruir desde `main` la estructura actual de `src/main/java`, especialmente paquetes y servicios disponibles para UI.
2. Revisar tests y convenciones existentes antes de crear clases Swing.
3. Definir la arquitectura mínima de la interfaz.
4. Implementar el primer bloque Swing con cambios pequeños y verificables.
5. Mantener documentación y tests actualizados durante la etapa.
