# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 09/09/2026

**Rama de trabajo:** `feature/swing-shell`

Este documento registra las decisiones y criterios definidos para incorporar tarjetas de crédito en SOFP. La documentación distingue explícitamente entre comportamiento ya implementado y funcionalidad pendiente.

## 1. Objetivo

Incorporar tarjetas de crédito a SOFP procurando reproducir el comportamiento de una entidad financiera real, sin copiar literalmente el modelo histórico de ControlFinanzas.

La tarjeta debe permitir gestionar consumos, ciclos de facturación, vencimientos, límite disponible, consumos en distintas monedas, cuotas, obligaciones y pagos, conservando la arquitectura de seguridad y aislamiento de datos de SOFP.

## 2. Decisión arquitectónica principal

Una tarjeta de crédito será una instancia de `Cuenta` cuyo `TipoCuenta` sea `TARJETA_CREDITO`, reutilizando la infraestructura existente de cuentas, autorización, instituciones financieras, monedas y estado activo/inactivo.

Los datos específicos de crédito actualmente previstos son límite, día de cierre y día de vencimiento.

## 3. Modelo financiero

La tarjeta representa el instrumento financiero y su capacidad de crédito. La deuda se representa mediante `Obligacion`.

Flujo conceptual:

`Cuenta(TARJETA_CREDITO)` → `Movimiento` de compra → consumo de crédito → `Obligacion`

La compra con tarjeta es distinta del pago posterior. El pago debe generar la salida efectiva de dinero desde la cuenta utilizada, reducir la obligación y liberar el crédito correspondiente.

## 4. Regla fundamental sobre monedas — IMPLEMENTADA EN EL BLOQUE ACTUAL

La moneda del consumo debe conservarse y no debe convertirse automáticamente al crear la obligación.

Una tarjeta puede tener consumos en ARS y USD. Por lo tanto:

`Moneda del consumo = moneda económica de la obligación originada por ese consumo`, mientras no exista una operación posterior que convierta o cancele la deuda.

Ejemplos:

- compra ARS 120 → obligación ARS 120;
- compra USD 120 → obligación USD 120.

El cierre de tarjeta no convierte automáticamente los USD a ARS.

La implementación actual permite informar una `Moneda` explícita en el movimiento y `Obligacion.getMoneda()` expone la moneda del movimiento de origen.

`ObligacionesPanel` muestra importe original y saldo pendiente junto con el código de moneda.

El formato de la UI utiliza `Locale.ROOT` para que el separador decimal no dependa del locale del entorno.

Commits del bloque:

- `9b92eac` — `feat: exponer moneda de la obligacion`.
- `47ced65` — `feat: mostrar moneda en obligaciones`.
- `fe0aa71` — `test: verificar moneda en obligaciones`.
- `13a68fb` — `fix: estabilizar formato de moneda en obligaciones`.

Validaciones:

- `ObligacionesPanelTest`: **4/4**.
- Suite general: **642/642**.

## 5. Consecuencia para el modelo de datos

La moneda explícita del movimiento resuelve el primer requisito para conservar la moneda económica de consumos y obligaciones. Esto no implica que estén resueltos todos los problemas de una tarjeta multidivisa.

Todavía deben definirse las reglas de conversión de pagos, límite disponible multidivisa, ciclos, cuotas y financiación.

No se debe resolver ninguna de esas cuestiones convirtiendo automáticamente todos los consumos a la moneda de `Cuenta`.

## 6. Límite y crédito disponible

El límite de crédito pertenece a la tarjeta, no a una obligación individual.

Una compra válida debe comprobar que existe crédito suficiente antes de registrar el consumo.

Criterio conceptual:

`crédito disponible = límite − crédito utilizado + saldo a favor`, si finalmente se adopta la regla de saldo a favor.

Una compra financiada compromete inicialmente el importe total correspondiente al crédito, no solamente la primera cuota.

### Decisión pendiente: tratamiento multidivisa del límite

Todavía no se fija si SOFP utilizará un límite general compartido para todas las monedas, límites independientes por moneda, o una combinación con conversión para determinar el crédito disponible.

No se debe asumir que una tarjeta posee automáticamente un límite USD independiente por el solo hecho de admitir consumos en USD.

## 7. Ciclo de facturación

La fecha de compra debe compararse con el cierre de la tarjeta para determinar a qué ciclo pertenece.

Ejemplo conceptual:

- cierre: día 10;
- compra día 8 → ciclo que cierra el día 10;
- compra día 11 → ciclo siguiente.

La implementación deberá definir expresamente el tratamiento del día exacto de cierre y los meses de distinta duración.

La lógica de ciclos y fechas debe quedar centralizada en dominio/servicio y no en Swing.

## 8. Vencimiento

El vencimiento se calculará a partir del ciclo correspondiente y del día de vencimiento configurado en la tarjeta.

Debe contemplarse correctamente el cambio de mes y los meses cuyo último día sea anterior al día configurado.

La política para días no hábiles deberá definirse cuando se implemente el cálculo.

## 9. Compras en cuotas

Una compra financiada debe conservar el vínculo con su operación original y sus cuotas.

Criterio inicial:

> Una compra de $600.000 en 6 cuotas compromete inicialmente $600.000 de límite, no $100.000.

Antes de programar cuotas deberá definirse importe de cada cuota, intereses, asignación a ciclos, pagos parciales, liberación del límite ante pagos/anulaciones/ajustes y conservación del historial.

## 10. Pagos de tarjeta

El pago debe ser un hecho financiero independiente de la compra.

Flujo conceptual:

`Pago` → `Movimiento EGRESO` de la cuenta bancaria utilizada → reducción de `Obligacion` → liberación del crédito.

Debe validarse usuario autorizado, pertenencia de la tarjeta al perfil, pertenencia de la cuenta bancaria al mismo perfil, fondos suficientes cuando corresponda, moneda del pago e importe aplicable a la deuda.

En pagos en moneda distinta de la deuda, la conversión deberá efectuarse según una regla de cotización explícita y en el momento del pago, no al crear la obligación.

La prioridad de aplicación de pagos entre obligaciones deberá ser explícita y testeable.

## 11. Seguridad y aislamiento

La tarjeta debe respetar el aislamiento de datos existente. Un usuario no puede registrar, consultar o consumir una tarjeta de otro perfil, pagar obligaciones de otro perfil ni utilizar una cuenta bancaria de otro perfil para pagar una tarjeta propia.

Estas reglas deben permanecer en servicios/repositorios y no depender exclusivamente de Swing.

## 12. Estado actual de SOFP

Piezas reutilizables actualmente disponibles:

- `FormaPago.TARJETA_CREDITO`;
- `Obligacion` para representar deuda originada por una compra con tarjeta;
- `GastoService` para coordinar gasto y obligación;
- `MovimientoService` con soporte de moneda explícita;
- `Cuenta` como entidad financiera central;
- `CuentaService`;
- autorización por `usuarioId` y aislamiento por `PerfilFinanciero`.

La implementación actual de `Cuenta` contempla `TipoCuenta.TARJETA_CREDITO` y los datos específicos de crédito de límite, cierre y vencimiento.

## 13. Inconsistencia pendiente de corregir

Actualmente `MovimientoService` excluye los egresos con `FormaPago.TARJETA_CREDITO` del saldo monetario y de la validación de fondos, mientras que `CuentaService.calcularSaldo()` todavía calcula los movimientos sin aplicar esa misma excepción.

Esta diferencia debe unificarse antes de considerar terminada la integración financiera de tarjetas.

## 14. Interfaz Swing

La tarjeta tendrá una experiencia específica y no deberá aparecer como una opción genérica equivalente a una cuenta bancaria común.

La interfaz prevista separará conceptualmente cuentas y tarjetas de crédito y permitirá progresivamente listar tarjetas, registrar una tarjeta, consultar límite y disponible, consultar consumos, visualizar deuda separada por moneda, consultar ciclo/cierre/vencimiento, registrar y consultar pagos y posteriormente gestionar cuotas y financiación.

Internamente seguirá creándose una `Cuenta` con `TipoCuenta.TARJETA_CREDITO`.

La interfaz no deberá calcular reglas financieras por sí misma.

## 15. Orden de implementación propuesto

1. ~~Consolidar el modelo de moneda de los movimientos/obligaciones para conservar consumos multidivisa.~~ **COMPLETADO.**
2. Resolver el criterio de límite y disponible para ARS/USD.
3. Unificar el cálculo de saldo monetario entre `MovimientoService` y `CuentaService`.
4. Implementar consumo y liberación de crédito con pruebas de dominio y persistencia.
5. Implementar ciclos y vencimientos.
6. Implementar pagos, incluyendo pagos en la misma moneda y conversiones al pagar en otra moneda.
7. Implementar cuotas y financiación una vez cerradas sus reglas.
8. Incorporar el panel Swing específico de tarjetas.
9. Agregar pruebas de seguridad, aislamiento, monedas, ciclos, pagos, límites y casos límite.

## 16. Principios de implementación

- código y tests prevalecen sobre documentación;
- cambios mínimos y coherentes con la arquitectura existente;
- no duplicar entidades sin necesidad de negocio;
- no esconder diferencias monetarias mediante conversiones automáticas;
- no contabilizar dos veces un mismo hecho financiero;
- separar instrumento financiero, consumo, deuda y pago;
- centralizar reglas de negocio fuera de Swing;
- mantener autorización y aislamiento en servicios/repositorios;
- no implementar decisiones todavía abiertas como si fueran definitivas.

## 17. Próximo paso técnico

El bloque de conservación y visualización de moneda ya está cerrado y validado.

El siguiente trabajo de tarjetas deberá comenzar revisando el código actual de `Movimiento`, `Obligacion`, `Cuenta`, `Moneda`, `MovimientoService`, `GastoService` y `CuentaService`, y sus tests, para resolver el siguiente problema mínimo y bien definido: **unificar el tratamiento del saldo de las tarjetas y establecer la regla de límite/crédito disponible sin introducir conversiones monetarias implícitas**.

No construir todavía la pantalla Swing específica de tarjetas hasta que estas reglas financieras estén definidas y cubiertas por tests.
