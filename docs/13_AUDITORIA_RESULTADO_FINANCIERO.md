# SOFP — Auditoría de Resultado Financiero

## Estado validado — 02/10/2026

Rama: `feature/swing-shell`.  
HEAD: `323de4617519caad15b7570827af7dfd23c7b830` — `test: cubrir reversion de pago entre periodos`.  
`main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.  
Comparación GitHub: **292 commits ahead / 0 behind**. No se modificó ni mergeó `main`.

### Resultado de la validación

El usuario informó:

- `mvn test`: **969 tests**.
- Failures: **0**.
- Errors: **0**.
- Skipped: **0**.
- **BUILD SUCCESS**.
- Duración: **19:51 min**.
- Finalización: **02/10/2026 15:27:06 -03:00**.

Este resultado reemplaza como última suite completa conocida al anterior **958/958**.

### Alcance auditado

Se revisó `ResultadoFinancieroService` junto con `Movimiento`, `OperacionFinanciera`, `PagoTarjeta`, `PagoTarjetaService`, `TipoCambio`, `TipoCambioRepository`, `MovimientoRepository`, `OperacionFinancieraRepository`, `Obligacion`, `Financiacion`, `ResumenResultadoFinanciero`, `PatrimonioFinancieroService` y `ReportesPanel`.

La implementación actual:

- calcula ingresos y egresos por moneda, sin conversión implícita;
- valida que el perfil financiero pertenezca al usuario autorizado;
- limita funcionalmente los movimientos al perfil consultado;
- respeta los límites inclusivos del período;
- excluye movimientos vinculados a operaciones financieras;
- incluye la compra original con tarjeta como egreso;
- excluye del resultado el pago físico de tarjeta y su reversión;
- mantiene el efecto temporal correcto cuando pago o reversión ocurren en otro período;
- mantiene separados los resultados de distintas monedas;
- devuelve cero con la escala de la moneda cuando no hay movimientos;
- expone mapas inmutables y copias defensivas en `ResumenResultadoFinanciero`.

### Tipo de cambio

`TipoCambioRepository` dispone de búsqueda por fecha y de cotización aplicable por fecha/hora. Se verificó además que, en día hábil, una consulta utiliza la última cotización disponible hasta el instante solicitado.

`ResultadoFinancieroService` actualmente no utiliza tipo de cambio. Esto es coherente con el modelo actual de resultado separado por moneda y no justifica introducir una conversión de presentación sin una regla contable explícita.

### Alcance que no se modifica por inferencia

Las operaciones de compra/venta de activos quedan fuera del resultado mediante `OperacionFinanciera`. El sistema todavía no define una regla explícita de resultado realizado basada en costo de adquisición y precio de venta; no se agrega esa contabilidad durante esta auditoría.

Tampoco se introduce todavía un resultado consolidado en una única moneda. `ReportesPanel` presenta conjuntamente patrimonio y resultado, mientras que el resultado conserva su separación por moneda.

### Cierre

La auditoría de `ResultadoFinancieroService` queda **validada técnicamente** con suite completa verde de 969/969. No se justificó ningún cambio de producción.

La optimización futura de consultas de movimientos con filtros en repositorio puede tratarse como mejora técnica separada, sin mezclarla con reglas contables.

## Regla de continuidad

Antes de iniciar el siguiente bloque: reconstruir desde GitHub rama → últimos commits → comparación con `main` → código relacionado → repositorios → tests → reglas de negocio → documentación → último resultado.

Después de cambios importantes: tests específicos → tests relacionados → suite completa cuando corresponda → `git diff` → `git diff --check` → `git status` → documentación.

No modificar ni mergear `main` automáticamente.
