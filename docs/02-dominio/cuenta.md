# Cuenta

## 1. Objetivo

Una `Cuenta` representa cualquier origen o destino de dinero perteneciente a un `PerfilFinanciero`. Centraliza la disponibilidad monetaria con la que se realizan operaciones y permite distinguir los distintos lugares donde un perfil mantiene fondos.

Ejemplos de cuentas que podrá representar SOFP:

- Banco.
- Caja de ahorro.
- Cuenta corriente.
- Billetera virtual.
- Broker.
- Exchange.
- Efectivo.
- Caja fuerte.
- Cuenta en dólares.

## 2. Responsabilidades

Una `Cuenta`:

- Almacena el saldo actual de fondos, cuya fuente de trazabilidad serán los `MovimientoCuenta` registrados.
- Posee una única `Moneda`, que determina la unidad monetaria de sus importes.
- Pertenece a un `PerfilFinanciero` y, por lo tanto, a un contexto financiero independiente de un usuario.
- Registra los movimientos que modifican su saldo.
- Puede participar como origen o destino en transferencias entre cuentas.
- Puede utilizarse para comprar activos, generando el efecto monetario correspondiente.
- Puede utilizarse para vender activos, recibiendo el efecto monetario correspondiente.

El saldo no sustituye el historial de movimientos: deberá conservar coherencia con la suma de los movimientos de cuenta. La definición de si se materializa como valor persistido o se calcula en cada consulta se resolverá durante la implementación, sin modificar que los movimientos son la fuente de trazabilidad.

## 3. Atributos propuestos

No se definen todavía tipos Java ni mapeos de persistencia. Los atributos previstos son:

| Atributo | Justificación |
| --- | --- |
| `id` | Identificador técnico único heredado de `EntidadAuditable`, consistente con la estrategia de identificación del proyecto. |
| `nombre` | Nombre visible con el que el perfil reconoce la cuenta. |
| `alias` | Identificador breve o referencia alternativa, útil para distinguir cuentas similares o registrar un dato provisto por una entidad externa. |
| `descripcion` | Texto libre para documentar detalles operativos de la cuenta. |
| `saldo` | Importe actual disponible en la cuenta, expresado en su moneda y trazable mediante sus movimientos. |
| `activa` | Indica si la cuenta puede continuar participando en nuevas operaciones sin perder su historial. |
| `tipoCuenta` | Clasifica la naturaleza operativa de la cuenta. |
| `moneda` | Referencia a la única moneda en la que opera la cuenta. |
| `perfilFinanciero` | Referencia al perfil financiero propietario de la cuenta. |

## 4. Relaciones

### PerfilFinanciero y Cuenta

Un `PerfilFinanciero` puede administrar cero o múltiples cuentas. Cada `Cuenta` pertenece obligatoriamente a un único `PerfilFinanciero`.

```text
PerfilFinanciero 1 -------- 0..* Cuenta
```

### Cuenta y Moneda

Cada `Cuenta` se opera en una única `Moneda`. Una moneda podrá ser utilizada por múltiples cuentas del sistema.

```text
Cuenta 1 -------- 1 Moneda
```

### Cuenta y MovimientoCuenta

Una `Cuenta` podrá registrar cero o múltiples `MovimientoCuenta`. Cada movimiento afectará a una única cuenta.

```text
Cuenta 1 -------- 0..* MovimientoCuenta
```

## 5. Tipos de cuenta

Se propone un futuro enum `TipoCuenta` con los siguientes valores iniciales:

| Valor | Justificación |
| --- | --- |
| `BANCO` | Representa cuentas bancarias, como cajas de ahorro o cuentas corrientes. |
| `BILLETERA_VIRTUAL` | Distingue fondos administrados por plataformas de pago o billeteras digitales. |
| `BROKER` | Representa la cuenta de efectivo disponible en un intermediario de inversiones. |
| `EXCHANGE` | Identifica los fondos administrados en una plataforma de intercambio de criptoactivos. |
| `EFECTIVO` | Representa dinero físico disponible fuera de una institución financiera. |
| `CAJA_FUERTE` | Distingue fondos físicos resguardados de manera separada, por ejemplo en una caja fuerte. |

La clasificación permitirá aplicar reglas específicas, como admitir o restringir saldos negativos según el tipo, sin fragmentar el concepto central de cuenta.

## 6. Reglas del dominio

- Una cuenta siempre pertenece a un `PerfilFinanciero`.
- Una cuenta tiene una única `Moneda`.
- No existen cuentas sin moneda.
- Una cuenta puede tener saldo negativo dependiendo de su tipo y de las reglas que se definan para ese tipo.
- Las transferencias se realizan entre cuentas y deberán generar los movimientos correspondientes en la cuenta de origen y la cuenta de destino.

## 7. Consideraciones futuras

`Cuenta` será la base monetaria de funcionalidades posteriores:

- **Gastos:** permitirán registrar salidas de fondos desde una cuenta.
- **Ingresos:** permitirán registrar acreditaciones en una cuenta.
- **Inversiones:** permitirán reflejar pagos por compras de activos y cobros por sus ventas.
- **Tarjetas:** podrán asociarse a una cuenta para registrar consumos, pagos y resúmenes.
- **Préstamos:** podrán usar cuentas para desembolsos, cuotas, intereses y cancelaciones.
- **Conciliaciones:** permitirán comparar los movimientos registrados con extractos o saldos informados por bancos, brokers y billeteras.

Estas ampliaciones respetarán la separación existente entre `OperacionFinanciera` y sus efectos, representados por `MovimientoCuenta` y `MovimientoActivo`.


## Posible evolución: Institución

En futuras versiones podrá incorporarse una entidad `Institucion` para representar bancos, billeteras virtuales, brokers, exchanges y otras organizaciones.

La entidad `Cuenta` podrá referenciar una `Institucion`, evitando duplicar información y permitiendo agrupar cuentas pertenecientes a una misma organización.

Esta incorporación no modifica el modelo actual y queda prevista como una evolución natural de la arquitectura.