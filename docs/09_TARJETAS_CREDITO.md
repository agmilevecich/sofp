# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 08/09/2026

**Rama de trabajo:** `feature/swing-shell`

Este documento registra las decisiones y criterios definidos para incorporar tarjetas de crédito en SOFP. La documentación distingue explícitamente entre comportamiento ya existente y funcionalidad pendiente de implementación.

## 1. Objetivo

Incorporar tarjetas de crédito a SOFP procurando reproducir el comportamiento de una entidad financiera real, sin copiar literalmente el modelo histórico de ControlFinanzas.

La tarjeta debe permitir gestionar consumos, ciclos de facturación, vencimientos, límite disponible, consumos en distintas monedas, cuotas, obligaciones y pagos, conservando la arquitectura de seguridad y aislamiento de datos de SOFP.

La regla principal es que el modelo financiero debe representar los hechos económicos reales y no utilizar simplificaciones de interfaz para ocultar diferencias de negocio.

## 2. Antecedente: ControlFinanzas

La implementación histórica revisada en ControlFinanzas, especialmente en la rama `integracionPaneles`, contaba con una entidad `TarjetaCredito` propia y una entidad `CompraTarjeta` para información específica de las compras.

Ese modelo se utiliza como antecedente funcional, pero **no se copiará literalmente en SOFP**. La implementación de SOFP debe respetar su arquitectura actual y adoptar únicamente las reglas de negocio que sean compatibles con un sistema financiero coherente.

## 3. Decisión arquitectónica principal

### 3.1 Tarjeta como cuenta especializada

SOFP no incorporará una entidad independiente `TarjetaCredito` para representar la tarjeta como instrumento financiero.

Una tarjeta de crédito será una instancia de `Cuenta` cuyo `TipoCuenta` sea `TARJETA_CREDITO`.

La relación conceptual es:

`Usuario → PerfilFinanciero → Cuenta(TARJETA_CREDITO)`

Esto reutiliza la infraestructura existente de cuentas, autorización, instituciones financieras, monedas y estado activo/inactivo, evitando duplicar una jerarquía paralela.

### 3.2 Datos específicos

La cuenta que representa una tarjeta deberá almacenar los atributos propios del crédito que correspondan, actualmente:

- límite de crédito;
- día de cierre;
- día de vencimiento.

Los atributos generales continúan perteneciendo a `Cuenta`.

## 4. Modelo financiero de la tarjeta

La tarjeta representa el instrumento financiero y su capacidad de crédito. La deuda se representa mediante `Obligacion`.

El flujo conceptual es:

`Cuenta(TARJETA_CREDITO)` → `Movimiento` de compra → consumo de crédito → `Obligacion`

La compra con tarjeta es un hecho distinto del pago posterior.

Una compra con tarjeta consume crédito, genera una obligación, puede pertenecer a un ciclo de facturación y no genera una salida inmediata de dinero de la cuenta bancaria que posteriormente pagará la tarjeta.

El pago genera una salida efectiva de dinero desde una cuenta bancaria, reduce la obligación y libera el crédito correspondiente. El pago no debe volver a registrar la compra ni generar una segunda obligación.

## 5. Regla fundamental sobre monedas

Se incorpora como decisión de negocio que **la moneda del consumo debe conservarse y no debe convertirse automáticamente al cierre de la tarjeta**.

Una tarjeta puede tener consumos en ARS y USD dentro del mismo período.

Por lo tanto, deben distinguirse:

`Moneda del consumo ≠ Moneda de la deuda ≠ Moneda del pago`

Ejemplo:

- compra: USD 100;
- cierre de tarjeta: 10/10;
- resumen: USD 100;
- vencimiento: 20/10.

El cierre no convierte esos USD 100 a ARS.

Si la deuda se paga en USD, se cancela mediante USD 100.

Si la deuda se paga en ARS, la conversión se determina en el momento del pago conforme a la regla de cotización que SOFP defina para ese pago. Esa cotización no debe introducirse artificialmente al momento del cierre.

En consecuencia, una compra en USD debe permanecer identificada como una obligación en USD mientras no sea cancelada o convertida mediante un pago en otra moneda.

## 6. Consecuencia para el modelo de datos

El modelo actual de SOFP tiene una limitación que deberá resolverse antes de implementar completamente las tarjetas multidivisa: `Cuenta` posee una moneda y `Movimiento` actualmente no almacena una moneda propia, por lo que un consumo se encuentra implícitamente asociado a la moneda de la cuenta.

Esto no es suficiente para representar fielmente una tarjeta que pueda registrar simultáneamente consumos en ARS y USD.

Antes de implementar el cálculo definitivo de deuda, disponible y pagos multidivisa se deberá definir el mecanismo mínimo que permita conservar la moneda de cada consumo y de cada obligación, sin introducir entidades redundantes.

No se debe resolver esta limitación convirtiendo todos los consumos a la moneda de `Cuenta`.

## 7. Límite y crédito disponible

El límite de crédito pertenece a la tarjeta, no a una obligación individual.

Una compra válida debe comprobar que existe crédito suficiente antes de registrar el consumo.

El criterio conceptual es:

`crédito disponible = límite − crédito utilizado + saldo a favor`, si finalmente se adopta la regla de saldo a favor.

Una compra financiada consume inicialmente el importe total comprometido del límite, no solamente la primera cuota.

El límite no debe reducirse una segunda vez al generar una obligación, cerrar un ciclo o consultar un resumen.

### Decisión pendiente: tratamiento multidivisa del límite

Todavía no se fija si SOFP utilizará un límite general compartido para todas las monedas, límites independientes por moneda, o una combinación de límite general y conversión para determinar el crédito disponible.

Esta decisión deberá basarse en el comportamiento real de las entidades financieras y quedar documentada antes de implementar el cálculo definitivo del disponible multidivisa.

No se debe asumir que una tarjeta posee automáticamente un límite USD independiente por el solo hecho de admitir consumos en USD.

## 8. Ciclo de facturación

La fecha de compra debe compararse con el cierre de la tarjeta para determinar a qué ciclo pertenece.

Ejemplo conceptual:

- cierre: día 10;
- compra el día 8 → ciclo que cierra el día 10;
- compra el día 11 → ciclo siguiente.

La implementación deberá definir expresamente el tratamiento de compras realizadas exactamente el día de cierre.

También deberá contemplar meses de distinta duración: si el día configurado no existe, se utilizará el último día válido del mes cuando corresponda.

La lógica de ciclos y fechas debe quedar centralizada en dominio/servicio y no en Swing.

## 9. Vencimiento

El vencimiento se calculará a partir del ciclo correspondiente y del día de vencimiento configurado en la tarjeta.

Debe contemplarse correctamente el cambio de mes y los meses cuyo último día sea anterior al día configurado.

La política exacta para casos especiales y días no hábiles deberá definirse cuando se implemente el cálculo de vencimiento.

## 10. Compras en cuotas

Una compra financiada debe conservar el vínculo con su operación original y sus cuotas.

Criterio inicial:

> Una compra de $600.000 en 6 cuotas compromete inicialmente $600.000 de límite, no $100.000.

Al aplicar pagos, se libera el crédito correspondiente al importe que efectivamente deje de estar comprometido, de acuerdo con las reglas definitivas de financiación.

Antes de programar cuotas deberá definirse la representación de la compra y sus cuotas, importe de cada cuota, intereses, asignación a ciclos, pagos parciales, liberación del límite ante pagos/anulaciones/ajustes y conservación del historial de la operación original.

No se implementará financiación completa hasta cerrar estas reglas.

## 11. Pagos de tarjeta

El pago debe ser un hecho financiero independiente de la compra.

El flujo será conceptualmente:

`Pago` → `Movimiento EGRESO` de la cuenta bancaria utilizada → reducción de `Obligacion` → liberación del crédito.

Debe validarse usuario autorizado, pertenencia de la tarjeta al perfil, pertenencia de la cuenta bancaria al mismo perfil, fondos suficientes cuando corresponda, moneda del pago e importe aplicable a la deuda.

En pagos en moneda distinta de la deuda, la conversión deberá efectuarse según una regla de cotización explícita y en el momento del pago, no al cierre de la tarjeta.

La prioridad de aplicación de pagos entre obligaciones deberá ser explícita y testeable.

## 12. Seguridad y aislamiento

La tarjeta debe respetar el aislamiento de datos existente. Un usuario no puede registrar, consultar o consumir una tarjeta de otro perfil, pagar obligaciones de otro perfil ni utilizar una cuenta bancaria de otro perfil para pagar una tarjeta propia.

Estas reglas deben permanecer en servicios/repositorios y no depender exclusivamente de Swing.

## 13. Estado actual de SOFP

SOFP ya dispone de piezas reutilizables:

- `FormaPago.TARJETA_CREDITO`;
- `Obligacion` para representar deuda originada por una compra con tarjeta;
- `GastoService` para coordinar gasto y obligación;
- `MovimientoService` con tratamiento especial para egresos con tarjeta;
- `Cuenta` como entidad financiera central;
- `CuentaService`;
- autorización por `usuarioId` y aislamiento por `PerfilFinanciero`.

La implementación actual de `Cuenta` ya contempla `TipoCuenta.TARJETA_CREDITO` y los datos específicos de crédito de límite, cierre y vencimiento.

Existe además una prueba de persistencia de estos datos en `CuentaRepositoryTest`.

## 14. Inconsistencia pendiente de corregir

Actualmente `MovimientoService` excluye los egresos con `FormaPago.TARJETA_CREDITO` del saldo monetario y de la validación de fondos, mientras que `CuentaService.calcularSaldo()` todavía calcula los movimientos sin aplicar esa misma excepción.

Esta diferencia debe unificarse antes de considerar terminada la integración financiera de tarjetas.

## 15. Interfaz Swing

La tarjeta tendrá una experiencia específica y no deberá aparecer como una opción genérica equivalente a una cuenta bancaria común.

La interfaz prevista separará conceptualmente cuentas y tarjetas de crédito.

El panel específico de tarjetas deberá permitir progresivamente listar tarjetas del perfil autorizado, registrar una tarjeta, consultar límite y disponible, consultar consumos, visualizar deuda separada por moneda, consultar ciclo/cierre/vencimiento, registrar y consultar pagos y posteriormente gestionar cuotas y financiación.

Internamente seguirá creándose una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`.

La interfaz no deberá calcular reglas financieras por sí misma.

## 16. Orden de implementación propuesto

1. Consolidar el modelo de moneda de los movimientos/obligaciones para soportar consumos multidivisa.
2. Resolver el criterio de límite y disponible para ARS/USD.
3. Unificar el cálculo de saldo monetario entre `MovimientoService` y `CuentaService`.
4. Implementar consumo y liberación de crédito con pruebas de dominio y persistencia.
5. Implementar ciclos y vencimientos.
6. Implementar pagos, incluyendo pagos en la misma moneda y conversiones al pagar en otra moneda.
7. Implementar cuotas y financiación una vez cerradas sus reglas.
8. Incorporar el panel Swing específico de tarjetas.
9. Agregar pruebas de seguridad, aislamiento, monedas, ciclos, pagos, límites y casos límite.

## 17. Principios de implementación

- código y tests prevalecen sobre documentación;
- cambios mínimos y coherentes con la arquitectura existente;
- no duplicar entidades sin necesidad de negocio;
- no esconder diferencias monetarias mediante conversiones automáticas;
- no contabilizar dos veces un mismo hecho financiero;
- separar instrumento financiero, consumo, deuda y pago;
- centralizar reglas de negocio fuera de Swing;
- mantener autorización y aislamiento en servicios/repositorios;
- no implementar decisiones todavía abiertas como si fueran definitivas.

## 18. Próximo paso

El siguiente paso técnico no será todavía construir la pantalla Swing.

Primero deberá revisarse el modelo actual de `Movimiento`, `Obligacion`, `Cuenta`, `Moneda` y los servicios relacionados para determinar el cambio mínimo que permita representar correctamente una tarjeta con consumos en ARS y USD, manteniendo la deuda en la moneda original hasta el momento del pago.

Esta decisión será la base para implementar posteriormente el comportamiento de una tarjeta de crédito real en SOFP.
