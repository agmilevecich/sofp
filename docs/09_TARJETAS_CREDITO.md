# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 08/09/2026

**Rama de trabajo:** `feature/swing-shell`

**HEAD de GitHub:** `e8e6cdfce65d2e617c26f037d98f67e3cfe47600` — `docs: cerrar pendientes de integracion de transferencias`.

Este documento registra las decisiones y criterios definidos antes de implementar tarjetas de crédito en SOFP. La documentación describe el diseño previsto; no implica que las funcionalidades aquí indicadas ya estén implementadas.

## 1. Objetivo

Incorporar tarjetas de crédito a SOFP permitiendo registrar cada tarjeta con sus condiciones propias y utilizar esa información para gestionar compras, ciclos de facturación, vencimientos, límite disponible, cuotas, obligaciones y pagos.

La adaptación debe conservar la arquitectura de seguridad y aislamiento de datos ya consolidada en SOFP.

## 2. Antecedente: ControlFinanzas

La implementación histórica revisada en ControlFinanzas, especialmente en la rama `integracionPaneles`, contaba con una entidad `TarjetaCredito` propia.

La tarjeta almacenaba, entre otros datos:

- nombre de la tarjeta;
- límite de crédito;
- día de cierre;
- día de vencimiento;
- banco asociado;
- usuario propietario;
- movimientos asociados;
- compras con tarjeta asociadas.

También existía `CompraTarjeta`, destinada a conservar información específica de la compra:

- comercio;
- monto total;
- cantidad de cuotas;
- interés;
- fecha de compra;
- tarjeta utilizada.

El servicio de tarjetas de ControlFinanzas incluía lógica para determinar el inicio y cierre del ciclo, calcular el próximo vencimiento, consultar movimientos del ciclo, calcular deuda, disponible, saldo a favor y pago mínimo, además de gestionar pagos y financiación.

También existía un mecanismo de alertas de vencimiento de tarjetas.

## 3. Estado actual de SOFP

SOFP ya dispone de una arquitectura diferente y más madura que ControlFinanzas.

La propiedad y autorización siguen la cadena:

`Usuario → PerfilFinanciero → Cuenta / Categoría → Movimientos / Operaciones`

SOFP ya contempla `FormaPago.TARJETA_CREDITO` y posee una entidad `Obligacion` para representar la deuda generada por un movimiento realizado con tarjeta de crédito.

Una `Obligacion` se origina a partir de un `Movimiento` de tipo `EGRESO` cuya forma de pago es `TARJETA_CREDITO`. Conserva el importe original, saldo pendiente y estado de la deuda.

Por este motivo, la tarjeta no debe reemplazar a `Obligacion` ni absorber responsabilidades que corresponden a la deuda.

## 4. Decisión arquitectónica principal

No se copiará literalmente el modelo de ControlFinanzas.

La adaptación propuesta separa tres conceptos:

### TarjetaCredito

Representa el instrumento financiero y sus condiciones permanentes o relativamente estables.

Debe concentrar información como:

- nombre o apodo;
- institución financiera;
- identificador externo o últimos dígitos, si corresponde;
- límite de crédito;
- día de cierre;
- día de vencimiento;
- moneda;
- estado activa/inactiva;
- perfil financiero propietario.

### Compra con tarjeta

Representa la operación comercial realizada con la tarjeta.

Debe permitir conservar información propia de la compra que no corresponde a la obligación, por ejemplo:

- comercio;
- fecha de compra;
- importe;
- cuotas;
- interés, cuando corresponda;
- tarjeta utilizada;
- descripción/categoría según el modelo final.

La necesidad de una entidad específica de compra deberá confirmarse al implementar cuotas y financiación; no se agregará una entidad solamente por copiar ControlFinanzas.

### Obligacion

Representa la deuda que surge de la compra con tarjeta.

Mantiene la responsabilidad que ya tiene en SOFP:

- importe original;
- saldo pendiente;
- estado pendiente/parcial/pagada;
- pagos realizados mediante `registrarPago`.

La información de cierre, vencimiento y límite no debe duplicarse en `Obligacion`.

## 5. Relación conceptual

El modelo previsto es:

`Usuario`

→ `PerfilFinanciero`

→ `TarjetaCredito`

→ compra con tarjeta

→ `Movimiento`

→ `Obligacion`

La tarjeta pertenece al perfil financiero y queda protegida por la cadena de autorización existente.

El `usuarioId` debe utilizarse como actor autorizado en servicios, siguiendo las reglas actuales de SOFP, y no como una duplicación innecesaria de la propiedad en cada entidad.

## 6. Datos de la tarjeta

Como mínimo, la tarjeta debe poder registrarse con:

- nombre;
- institución financiera;
- límite de crédito;
- día de cierre;
- día de vencimiento;
- moneda;
- estado activa/inactiva;
- perfil financiero propietario.

Los días de cierre y vencimiento son reglas de la tarjeta, no atributos de una compra individual.

El límite pertenece a la tarjeta porque determina la capacidad de crédito disponible independientemente de una obligación particular.

## 7. Ciclo de facturación

La fecha de compra debe compararse con el cierre de la tarjeta para determinar a qué ciclo pertenece.

Ejemplo conceptual:

- cierre: día 10;
- vencimiento: día 20;
- compra el día 8 → pertenece al ciclo que cierra el día 10;
- compra el día 11 → pertenece al ciclo siguiente.

El cálculo debe contemplar correctamente meses de distinta duración. Si el día de cierre configurado no existe en un mes concreto, deberá utilizarse el último día válido de ese mes.

La implementación final deberá definir de manera explícita el tratamiento de compras realizadas exactamente el día de cierre.

## 8. Vencimiento

El vencimiento debe calcularse a partir del ciclo correspondiente y del día de vencimiento configurado en la tarjeta.

También debe contemplarse correctamente el cambio de mes y los meses cuyo último día sea anterior al día configurado.

La lógica debe quedar centralizada en un servicio de tarjetas o componente de dominio apropiado, evitando reproducir cálculos de fechas en los paneles Swing.

## 9. Límite y disponible

El límite de crédito pertenece a `TarjetaCredito`.

El disponible debe calcularse a partir del límite y de las obligaciones/consumos que correspondan según las reglas definidas para SOFP.

No se debe asumir que el saldo pendiente de una única `Obligacion` representa toda la utilización de la tarjeta.

Antes de implementar el cálculo definitivo deberán fijarse las reglas para:

- compras en un pago;
- compras en cuotas;
- pagos parciales;
- pagos totales;
- saldo a favor;
- consumos pendientes de cierre;
- consumos ya facturados;
- anulaciones o ajustes, si SOFP los incorpora posteriormente.

## 10. Compras en cuotas

La experiencia de ControlFinanzas contemplaba cantidad de cuotas e interés.

Para SOFP se conservará ese comportamiento conceptual, pero se deberá decidir cómo representar las cuotas sin duplicar artificialmente movimientos u obligaciones.

Antes de programar esta parte deberá definirse:

1. si cada cuota será una obligación independiente o parte de una compra financiada;
2. cómo se calculará el importe de cada cuota;
3. cómo se tratarán los intereses;
4. cómo se asignarán las cuotas a los ciclos de facturación;
5. cómo se registrarán pagos parciales;
6. qué información histórica debe permanecer asociada a la compra original.

No se implementará financiación hasta cerrar estas reglas.

## 11. Pagos de tarjeta

El pago de una tarjeta no debe confundirse con la compra que originó la deuda.

La compra genera el movimiento y la obligación. El pago reduce la obligación y debe producir el movimiento financiero correspondiente sobre la cuenta bancaria utilizada para pagar.

La implementación deberá conservar las reglas actuales de SOFP sobre autorización, saldo disponible y aislamiento por usuario/perfil.

La lógica de pago debe priorizar las obligaciones pendientes de acuerdo con una regla explícita y testeable, evitando depender del orden accidental de una consulta.

## 12. Seguridad y aislamiento de datos

La tarjeta debe respetar el aislamiento de datos ya existente en SOFP.

Un usuario no debe poder:

- registrar una tarjeta en otro perfil;
- consultar tarjetas de otro perfil;
- utilizar una tarjeta perteneciente a otro perfil para registrar una compra;
- pagar obligaciones asociadas a otro usuario mediante una tarjeta propia.

Las validaciones de autorización deben permanecer en servicios/repositorios y no depender exclusivamente de la interfaz Swing.

## 13. Integración con movimientos y obligaciones

Una compra con tarjeta debe seguir utilizando el modelo financiero existente en SOFP.

Conceptualmente:

`Compra con tarjeta`

→ `Movimiento EGRESO`

→ `FormaPago.TARJETA_CREDITO`

→ `Obligacion`

La compra no debe generar una salida inmediata de dinero de la cuenta bancaria como si fuera un gasto pagado en efectivo o débito.

El pago posterior de la tarjeta es el momento en que debe reflejarse la salida de fondos de la cuenta utilizada para pagar.

## 14. Interfaz Swing

La incorporación de tarjetas deberá integrarse al shell existente siguiendo el patrón utilizado para Ingresos, Transferencias y Obligaciones.

La interfaz prevista deberá permitir, progresivamente:

- listar tarjetas del perfil autorizado;
- registrar una tarjeta;
- editar sus datos permitidos;
- activar/desactivar una tarjeta;
- consultar límite y disponible;
- visualizar cierre y próximo vencimiento;
- registrar compras;
- consultar deuda y ciclo;
- registrar pagos.

La interfaz no debe contener reglas complejas de negocio. Los cálculos de ciclos, vencimientos, límites, deuda y pagos deben quedar en dominio/servicios.

## 15. Alertas

ControlFinanzas contaba con alertas específicas para vencimientos de tarjetas.

Se considera una funcionalidad válida para recuperar en SOFP, pero no forma parte del primer corte de implementación.

La prioridad inicial será construir correctamente el modelo y las reglas financieras. Las alertas podrán incorporarse posteriormente utilizando los servicios ya existentes.

## 16. Tests previstos

La implementación deberá cubrir como mínimo:

### Tarjeta

- creación válida;
- parámetros obligatorios nulos;
- límite inválido;
- día de cierre inválido;
- día de vencimiento inválido;
- tarjeta perteneciente a otro perfil;
- tarjeta inactiva cuando corresponda.

### Ciclos

- compra antes del cierre;
- compra después del cierre;
- compra exactamente el día de cierre;
- cierre en febrero;
- cierre en meses de 30 días;
- cierre en meses de 31 días;
- cálculo del próximo vencimiento.

### Compras

- compra válida;
- importe inválido;
- tarjeta inexistente;
- tarjeta de otro usuario;
- tarjeta inactiva;
- generación correcta del movimiento;
- generación correcta de la obligación.

### Cuotas

Cuando se implemente financiación:

- una cuota;
- múltiples cuotas;
- interés;
- distribución por ciclos;
- última cuota con ajuste por redondeo;
- pagos parciales.

### Límite

- cálculo del disponible;
- consumo de crédito;
- liberación del límite mediante pagos;
- saldo a favor, si se adopta la regla de ControlFinanzas;
- límites y valores extremos.

### Pagos

- pago parcial;
- pago total;
- pago superior al saldo;
- pago de obligación inexistente;
- pago de obligación de otro usuario;
- pago con cuenta inexistente;
- fondos insuficientes;
- pago posterior a una obligación ya cancelada.

## 17. Etapas de implementación

La implementación se propone en etapas pequeñas:

### Etapa 1 — Modelo de tarjeta

Crear el modelo de `TarjetaCredito` integrado con `PerfilFinanciero`, `InstitucionFinanciera` y `Moneda`, con sus reglas básicas y persistencia.

### Etapa 2 — Servicio y consultas

Crear repositorio/servicio para registrar, consultar y administrar tarjetas respetando autorización y estado activo.

### Etapa 3 — Ciclos y vencimientos

Incorporar el cálculo de ciclo, cierre y próximo vencimiento con tests exhaustivos de fechas.

### Etapa 4 — Compra con tarjeta

Integrar la tarjeta al flujo existente de movimientos y obligaciones sin romper el modelo actual.

### Etapa 5 — Límite y deuda

Implementar deuda de tarjeta, disponible y reglas de utilización del límite.

### Etapa 6 — Cuotas y financiación

Incorporar cuotas e intereses después de definir formalmente su representación de dominio.

### Etapa 7 — Pago de tarjeta

Integrar el pago con las obligaciones existentes y con las cuentas bancarias.

### Etapa 8 — Swing

Agregar paneles y navegación al shell, con tests de navegación y comportamiento.

### Etapa 9 — Alertas y dashboard

Recuperar progresivamente las alertas de vencimiento y la visualización resumida.

## 18. Decisiones tomadas

1. No copiar literalmente `TarjetaCredito` de ControlFinanzas.
2. La tarjeta será una entidad propia de SOFP.
3. La tarjeta pertenecerá al `PerfilFinanciero`, respetando la cadena de seguridad existente.
4. El límite, cierre y vencimiento pertenecen a la tarjeta.
5. `Obligacion` seguirá representando la deuda y no almacenará datos propios de la tarjeta salvo que una necesidad de dominio posterior lo justifique.
6. La compra con tarjeta seguirá generando un movimiento `EGRESO` con `FormaPago.TARJETA_CREDITO` y su obligación correspondiente.
7. El pago de tarjeta será una operación posterior y distinta de la compra.
8. La lógica financiera no se implementará dentro de los paneles Swing.
9. Cuotas y financiación se definirán antes de programarse.
10. Las alertas de vencimiento quedan como evolución posterior.

## 19. Pendientes de diseño

Antes de comenzar la implementación funcional deben cerrarse especialmente estas decisiones:

- nombre definitivo de la entidad de compra, si se requiere;
- relación exacta entre compra, movimiento y obligación;
- representación de cuotas;
- tratamiento de intereses;
- definición exacta del disponible;
- tratamiento de consumos antes/después del cierre;
- comportamiento el día exacto del cierre;
- reglas para pagos parciales y saldo a favor;
- tratamiento de anulaciones/devoluciones;
- campos identificatorios de la tarjeta que realmente necesita SOFP.

## 20. Criterio de continuidad

Este documento es un diseño previo a la implementación.

Cuando se retome el trabajo, se deberá volver a revisar el código actual de SOFP antes de modificar cualquier clase. Si el código evoluciona de manera diferente a este documento, prevalecerán código y tests; posteriormente se actualizará esta documentación.

El objetivo es que una nueva sesión pueda reconstruir no solo qué se implementó, sino también por qué se tomó cada decisión del modelo de tarjetas de crédito.
