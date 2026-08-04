# Dominio del sistema

SOFP será un sistema para administrar información financiera personal. Permitirá registrar cuentas, instrumentos financieros, operaciones y la evolución de los saldos para ofrecer una visión consolidada del patrimonio.

## Sistema multiusuario

El sistema se diseñará como multiusuario. Cada usuario administrará su propia información financiera de manera independiente, sin que los datos de un usuario se mezclen con los de otro.

## PerfilFinanciero

`PerfilFinanciero` representará el contexto financiero de un usuario. Reunirá los datos que permitan organizar sus cuentas, inversiones, posiciones y operaciones. Será el punto de pertenencia de la información financiera que el sistema gestione para cada usuario.

## Moneda

`Moneda` será una entidad transversal que representará una unidad monetaria. Permitirá expresar importes de forma consistente y evitar que los valores financieros queden desvinculados de su denominación.

Las cuentas tendrán una moneda de operación. Los activos podrán estar denominados o cotizar en una moneda, según corresponda a su tipo. Las operaciones financieras y los movimientos resultantes indicarán la moneda de cada importe involucrado. Así, `Moneda` vinculará cuentas, activos y movimientos sin pertenecer exclusivamente a ninguno de ellos.

## Activos financieros

`Activo` será la abstracción común para los instrumentos financieros registrados en un perfil. A partir de ella se modelarán los siguientes tipos de activo:

- `FondoComun`
- `Bono`
- `Acción`
- `Criptomoneda`
- `PlazoFijo`
- `Divisa`

Esta jerarquía permitirá conservar reglas y atributos comunes en `Activo`, y especializar los datos propios de cada tipo de instrumento.

## EntidadAuditable

`EntidadAuditable` será una clase base transversal para los elementos del dominio que requieran trazabilidad. Definirá los campos `fechaCreacion`, `fechaActualizacion`, `creadoPor` y `actualizadoPor`.

Estos campos permitirán conocer cuándo se creó o modificó un registro y qué usuario realizó cada acción. La auditoría apoyará el control, la revisión de cambios y la trazabilidad de la información financiera.

## Operaciones y movimientos

`OperacionFinanciera` representará el hecho de negocio que el usuario registra, por ejemplo una compra, venta, depósito, retiro, cobro o conversión. Describe la intención y el contexto de la operación, sin ser por sí misma la fuente de los saldos.

Una operación podrá producir uno o más movimientos resultantes:

- `MovimientoCuenta` registrará la variación de fondos de una cuenta, expresada en su moneda. Ejemplos: un débito por una compra, un crédito por una venta o un depósito.
- `MovimientoActivo` registrará la variación de la posición de un activo, como el aumento o la disminución de una cantidad de títulos, cuotas o unidades.

La separación permite representar una misma operación con sus efectos monetarios y patrimoniales. Los saldos de cuentas y las posiciones de activos se calcularán a partir de sus movimientos correspondientes, no como valores independientes.
