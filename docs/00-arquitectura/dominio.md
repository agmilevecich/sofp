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

`OperacionFinanciera` representa el hecho de negocio que el usuario registra, por ejemplo una compra, venta, depósito, retiro, cobro, transferencia o conversión. Describe la intención y el contexto de la operación y puede agrupar sus movimientos resultantes.

En el estado actual del proyecto, los efectos monetarios se registran mediante la entidad `Movimiento`. Un `Movimiento` pertenece a una `Cuenta`, tiene un tipo (`EGRESO` o `INGRESO`), importe, fecha, categoría y descripción, y puede estar asociado a una `OperacionFinanciera`.

Una `OperacionFinanciera` puede tener como máximo dos `Movimiento` asociados. La asociación se mantiene de forma bidireccional y las reglas que protegen esa relación están encapsuladas en el dominio.

El saldo de una cuenta debe obtenerse a partir de sus movimientos. Por lo tanto, el saldo no debe considerarse un valor independiente mantenido manualmente como fuente de verdad.

La futura evolución hacia activos financieros podrá incorporar movimientos específicos de posición, pero esa separación todavía no forma parte del modelo implementado y no debe tratarse como una entidad existente en la versión actual.
