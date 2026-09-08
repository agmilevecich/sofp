# SOFP — Diseño y adaptación de tarjetas de crédito

## Estado — 08/09/2026

**Rama de trabajo:** `feature/swing-shell`

Este documento registra las decisiones y criterios definidos para incorporar tarjetas de crédito en SOFP. La documentación describe el diseño acordado y distingue explícitamente entre comportamiento ya existente y funcionalidad todavía pendiente de implementación.

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

Este modelo se utiliza como antecedente funcional, pero **no se copiará literalmente en SOFP**.

## 3. Estado actual de SOFP

SOFP ya dispone de una arquitectura diferente y más madura que ControlFinanzas.

La propiedad y autorización siguen la cadena:

`Usuario → PerfilFinanciero → Cuenta / Categoría → Movimientos / Operaciones`

SOFP ya contempla `FormaPago.TARJETA_CREDITO` y posee una entidad `Obligacion` para representar la deuda generada por un movimiento realizado con tarjeta de crédito.

Una `Obligacion` se origina a partir de un `Movimiento` de tipo `EGRESO` cuya forma de pago es `TARJETA_CREDITO`. Conserva el importe original, saldo pendiente y estado de la deuda.

Además, `MovimientoService` ya contiene una regla específica para que un EGRESO con `FormaPago.TARJETA_CREDITO` no requiera fondos disponibles en la cuenta y no se descuente del saldo de efectivo ordinario.

`GastoService` ya coordina la creación del movimiento y, para pagos con tarjeta, la generación de la `Obligacion` correspondiente.

Por este motivo, la tarjeta no debe reemplazar a `Obligacion` ni absorber responsabilidades que corresponden a la deuda.

## 4. Decisión arquitectónica principal

### 4.1 Tarjeta como cuenta especializada

Se adopta como decisión de diseño que **SOFP no incorporará una entidad independiente `TarjetaCredito` para representar la tarjeta como instrumento financiero**.

Una tarjeta de crédito será una instancia de `Cuenta` cuyo `TipoCuenta` sea `TARJETA_CREDITO`.

La relación conceptual queda:

`Usuario → PerfilFinanciero → Cuenta(TARJETA_CREDITO)`

Esto aprovecha la infraestructura existente de cuentas, autorización, movimientos, instituciones financieras, moneda y estado activo/inactivo, evitando duplicar una jerarquía paralela.

El cambio requerido en el modelo será, como mínimo, incorporar `TARJETA_CREDITO` al enum `TipoCuenta` y extender `Cuenta` con los datos específicos de crédito que sean necesarios.

### 4.2 Datos específicos de la tarjeta

La cuenta que representa una tarjeta deberá poder almacenar, además de sus atributos generales, información propia del crédito:

- límite de crédito;
- día de cierre;
- día de vencimiento.

Los atributos generales ya pertenecen naturalmente a `Cuenta`:

- nombre;
- identificador externo;
- institución financiera;
- moneda;
- perfil financiero propietario;
- estado activa/inactiva.

No se debe duplicar en una nueva entidad información que `Cuenta` ya administra correctamente.

### 4.3 Compra con tarjeta

La compra se seguirá representando mediante el modelo financiero existente:

`Cuenta(TARJETA_CREDITO) + Movimiento(EGRESO, TARJETA_CREDITO) + Obligacion`

La necesidad de una entidad específica `CompraTarjeta` queda pendiente y deberá justificarse cuando se implemente financiación, cuotas e información comercial que no pueda representarse adecuadamente mediante `Movimiento` y `Obligacion`.

No se agregará una entidad solamente para reproducir el diseño de ControlFinanzas.

## 5. Relación conceptual definitiva

El modelo acordado pasa a ser:

`Usuario`

→ `PerfilFinanciero`

→ `Cuenta` con `TipoCuenta.TARJETA_CREDITO`

→ `Movimiento` de compra

→ `Obligacion`

La tarjeta pertenece al perfil financiero y queda protegida por la cadena de autorización existente.

El `usuarioId` debe utilizarse como actor autorizado en servicios, siguiendo las reglas actuales de SOFP, y no como una duplicación innecesaria de la propiedad en cada entidad.

La `Obligacion` continúa representando la deuda. La `Cuenta` especializada representa el instrumento financiero y su límite.

## 6. Estado actual frente al diseño nuevo

La arquitectura existente ya aporta varias piezas reutilizables:

### Ya existe

- `FormaPago.TARJETA_CREDITO`.
- `Obligacion` para representar la deuda originada por una compra con tarjeta.
- `GastoService` para coordinar el gasto y crear la obligación.
- `MovimientoService` con tratamiento especial de egresos con tarjeta.
- `Cuenta` como entidad central de las operaciones financieras.
- `CuentaService` para registrar, consultar, modificar, activar/desactivar y calcular saldos de cuentas.
- autorización por `usuarioId` y aislamiento por `PerfilFinanciero`.

### Todavía pendiente

- agregar `TARJETA_CREDITO` a `TipoCuenta`;
- incorporar en `Cuenta` los atributos específicos de crédito;
- definir y validar límite disponible;
- asociar de forma inequívoca el consumo de crédito con la cuenta/tarjeta;
- implementar ciclos y vencimientos;
- completar el flujo de pago de tarjeta;
- hacer que el pago reduzca la obligación y libere crédito;
- incorporar financiación/cuotas una vez cerradas sus reglas;
- crear los tests correspondientes;
- integrar posteriormente la funcionalidad en Swing.

## 7. Regla fundamental de contabilización

Se establece como regla central:

> **Una compra con tarjeta consume crédito, pero no consume dinero de la cuenta bancaria en el momento de la compra.**

La compra genera una deuda con la tarjeta. La salida efectiva de dinero se produce cuando se realiza el pago de la tarjeta desde una cuenta que tenga fondos suficientes.

Por lo tanto, deben distinguirse dos hechos financieros:

`Compra`

→ consumo de crédito

→ deuda / obligación

sin salida de efectivo bancario.

`Pago`

→ salida de efectivo de una cuenta bancaria

→ reducción de deuda

→ liberación del crédito utilizado.

## 8. Límite y disponible

El límite de crédito será un atributo de la `Cuenta` cuando `TipoCuenta == TARJETA_CREDITO`.

Una compra válida debe comprobar que existe crédito suficiente antes de consumirlo.

El criterio conceptual para el disponible es:

`límite de crédito − crédito consumido + saldo a favor`, si finalmente se adopta la regla de saldo a favor.

El consumo se produce al registrar la compra.

El límite **no debe reducirse una segunda vez** al generar la obligación, facturar la compra o consultar el ciclo.

El disponible debe calcularse de forma centralizada en dominio/servicio y no en Swing.

No se debe asumir que el saldo pendiente de una única `Obligacion` representa toda la utilización de la tarjeta.

## 9. Saldo de la cuenta y saldo de crédito

Debe mantenerse una separación estricta entre:

- saldo monetario de una cuenta bancaria;
- crédito utilizado de una tarjeta;
- deuda pendiente de obligaciones.

Una compra con `FormaPago.TARJETA_CREDITO` no debe disminuir el saldo monetario disponible de una cuenta bancaria.

Al mismo tiempo, esa compra sí debe incrementar el crédito utilizado de la cuenta que representa la tarjeta y originar una obligación.

Existe actualmente una diferencia que deberá corregirse durante la implementación: `MovimientoService` ya excluye los egresos con tarjeta al calcular fondos y saldo monetario, mientras que `CuentaService.calcularSaldo()` actualmente suma/resta los movimientos sin esa excepción. La implementación de tarjetas deberá unificar esta regla para evitar resultados inconsistentes.

## 10. Datos de la tarjeta

Como mínimo, una cuenta de tipo tarjeta deberá disponer de:

- nombre;
- institución financiera;
- identificador externo, si corresponde;
- límite de crédito;
- día de cierre;
- día de vencimiento;
- moneda;
- estado activa/inactiva;
- perfil financiero propietario.

Los días de cierre y vencimiento son reglas de la tarjeta, no atributos de una compra individual.

El límite determina la capacidad de crédito disponible y no debe almacenarse en `Obligacion`.

## 11. Ciclo de facturación

La fecha de compra debe compararse con el cierre de la tarjeta para determinar a qué ciclo pertenece.

Ejemplo conceptual:

- cierre: día 10;
- vencimiento: día 20;
- compra el día 8 → pertenece al ciclo que cierra el día 10;
- compra el día 11 → pertenece al ciclo siguiente.

El cálculo debe contemplar correctamente meses de distinta duración. Si el día de cierre configurado no existe en un mes concreto, deberá utilizarse el último día válido de ese mes.

La implementación final deberá definir de manera explícita el tratamiento de compras realizadas exactamente el día de cierre.

La lógica debe quedar centralizada en un servicio o componente de dominio apropiado, evitando reproducir cálculos de fechas en los paneles Swing.

## 12. Vencimiento

El vencimiento debe calcularse a partir del ciclo correspondiente y del día de vencimiento configurado en la tarjeta.

También debe contemplarse correctamente el cambio de mes y los meses cuyo último día sea anterior al día configurado.

No se debe colocar esta lógica en la interfaz.

## 13. Compras en cuotas

La experiencia de ControlFinanzas contemplaba cantidad de cuotas e interés.

Para SOFP se conservará ese comportamiento conceptual, pero se deberá decidir cómo representar las cuotas sin duplicar artificialmente movimientos u obligaciones.

Criterio inicial para el límite: una compra financiada consume el **importe total financiado** del límite al momento de realizarse, no solamente el valor de la primera cuota.

Ejemplo conceptual: una compra de $600.000 en 6 cuotas consume inicialmente $600.000 de límite. Si posteriormente se paga una cuota de $100.000, se libera el crédito correspondiente a esos $100.000, siempre sujeto a las reglas definitivas de financiación, intereses, redondeos y pagos parciales.

Este criterio evita que una compra en cuotas parezca disponer nuevamente del límite que todavía permanece comprometido.

Antes de programar financiación deberá definirse:

1. si cada cuota será una obligación independiente o parte de una compra financiada;
2. cómo se calculará el importe de cada cuota;
3. cómo se tratarán los intereses;
4. cómo se asignarán las cuotas a los ciclos de facturación;
5. cómo se registrarán pagos parciales;
6. cómo se libera exactamente el límite ante pagos parciales, totales, anulaciones o ajustes;
7. qué información histórica debe permanecer asociada a la compra original.

No se implementará financiación hasta cerrar estas reglas.

## 14. Pagos de tarjeta

El pago de una tarjeta no debe confundirse con la compra que originó la deuda.

La compra genera el consumo de crédito, el movimiento asociado y la obligación. **No genera en ese momento un EGRESO de dinero de la cuenta bancaria.**

El pago posterior debe:

1. validar que la obligación pertenece al usuario autorizado;
2. validar que la cuenta bancaria utilizada pertenece al mismo perfil;
3. validar fondos suficientes cuando corresponda;
4. generar un `Movimiento EGRESO` sobre la cuenta bancaria;
5. reducir la obligación;
6. liberar el crédito utilizado en la tarjeta.

Conceptualmente:

`Pago de tarjeta`

→ `Movimiento EGRESO` bancario

→ reducción de `Obligacion`

→ liberación del crédito.

El pago parcial debe producir una liberación proporcional al importe efectivamente aplicado, según las reglas definitivas de financiación.

El pago no debe volver a registrar la compra ni generar una segunda obligación.

La lógica de pago debe priorizar las obligaciones pendientes mediante una regla explícita y testeable, evitando depender del orden accidental de una consulta.

## 15. Seguridad y aislamiento de datos

La tarjeta debe respetar el aislamiento de datos ya existente en SOFP.

Un usuario no debe poder:

- registrar una cuenta/tarjeta en otro perfil;
- consultar una cuenta/tarjeta de otro perfil;
- utilizar una tarjeta perteneciente a otro perfil para registrar una compra;
- pagar obligaciones asociadas a otro usuario;
- utilizar una cuenta bancaria de otro perfil para pagar una tarjeta propia.

Las validaciones de autorización deben permanecer en servicios/repositorios y no depender exclusivamente de Swing.

## 16. Integración con movimientos y obligaciones

La compra seguirá utilizando el modelo financiero existente:

`Cuenta(TARJETA_CREDITO)`

→ `Movimiento(EGRESO, FormaPago.TARJETA_CREDITO)`

→ consumo del límite

→ `Obligacion`

La compra no debe generar una salida inmediata de dinero de una cuenta bancaria.

El pago posterior debe ser un movimiento financiero independiente sobre la cuenta bancaria utilizada.

La implementación debe evitar que un mismo hecho se contabilice simultáneamente como consumo de tarjeta y disminución de efectivo.

## 17. Interfaz Swing

La incorporación de tarjetas deberá integrarse al shell existente siguiendo el patrón utilizado para Ingresos, Transferencias y Obligaciones.

La interfaz prevista deberá permitir progresivamente:

- listar tarjetas del perfil autorizado;
- registrar una tarjeta como cuenta de tipo `TARJETA_CREDITO`;
- editar sus datos permitidos;
- activar/desactivar la tarjeta;
- consultar límite y disponible;
- visualizar cierre y próximo vencimiento;
- registrar compras;
- consultar deuda y ciclo;
- registrar pagos.

La interfaz no debe contener reglas complejas de negocio. Los cálculos de ciclos, vencimientos, límites, deuda y pagos deben quedar en dominio/servicios.

## 18. Alertas

ControlFinanzas contaba con alertas específicas para vencimientos de tarjetas.

Se considera una funcionalidad válida para recuperar en SOFP, pero no forma parte del primer corte de implementación.

La prioridad inicial será construir correctamente el modelo y las reglas financieras. Las alertas podrán incorporarse posteriormente utilizando los servicios ya existentes.

## 19. Tests previstos

La implementación deberá cubrir como mínimo:

### Cuenta de tarjeta

- creación válida de una cuenta `TARJETA_CREDITO`;
- parámetros obligatorios nulos;
- límite inválido;
- día de cierre inválido;
- día de vencimiento inválido;
- cuenta/tarjeta perteneciente a otro perfil;
- tarjeta inactiva cuando corresponda;
- persistencia de los atributos específicos.

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
- generación correcta de la obligación;
- consumo correcto del límite;
- rechazo cuando no existe crédito suficiente;
- ausencia de disminución del saldo bancario por la compra;
- no duplicación del consumo al generar la obligación.

### Cuotas

Cuando se implemente financiación:

- una cuota;
- múltiples cuotas;
- consumo inicial del importe total financiado;
- interés;
- distribución por ciclos;
- última cuota con ajuste por redondeo;
- pagos parciales;
- liberación progresiva del límite según pagos.

### Límite

- cálculo del disponible;
- consumo de crédito al registrar una compra;
- no duplicación del consumo al generar la obligación/factura;
- liberación del límite mediante pagos;
- pago parcial;
- pago total;
- saldo a favor, si se adopta la regla;
- límites y valores extremos.

### Pagos

- pago parcial;
- pago total;
- pago superior al saldo;
- pago de obligación inexistente;
- pago de obligación de otro usuario;
- pago con cuenta inexistente;
- fondos insuficientes;
- pago posterior a una obligación ya cancelada;
- generación del `Movimiento EGRESO` bancario;
- reducción de la deuda;
- liberación del crédito consumido;
- persistencia del pago y de sus efectos.

### Seguridad

- acceso autorizado;
- acceso con usuario incorrecto;
- tarjeta de otro perfil;
- cuenta bancaria de otro perfil;
- obligación de otro usuario;
- aislamiento entre perfiles financieros.

## 20. Etapas de implementación

La implementación se realizará en etapas pequeñas y verificables.

### Etapa 1 — Modelo de cuenta especializada

- agregar `TARJETA_CREDITO` a `TipoCuenta`;
- agregar a `Cuenta` los atributos específicos de crédito acordados;
- validar límites y días;
- crear tests de dominio/persistencia;
- corregir la diferencia entre el cálculo de saldo de `MovimientoService` y `CuentaService`.

### Etapa 2 — Consumo de crédito

- identificar la cuenta como tarjeta mediante `TipoCuenta`;
- validar crédito disponible;
- registrar compras con `FormaPago.TARJETA_CREDITO`;
- asociar el consumo de crédito a la tarjeta;
- mantener la generación de `Obligacion`;
- verificar que la compra no reduzca el efectivo bancario.

### Etapa 3 — Ciclos y vencimientos

- definir semántica exacta del día de cierre;
- implementar cálculo de ciclo;
- implementar vencimiento;
- cubrir meses de distinta duración;
- agregar tests exhaustivos de fechas.

### Etapa 4 — Pagos

- definir asociación entre pago, obligación y tarjeta;
- generar EGRESO bancario;
- reducir obligación;
- liberar crédito;
- validar autorización y fondos;
- cubrir pagos parciales y totales.

### Etapa 5 — Financiación

- cerrar modelo de compra financiada;
- definir cuotas, intereses y ciclos;
- implementar la representación elegida;
- cubrir redondeos y pagos parciales.

### Etapa 6 — Swing

- incorporar panel de tarjetas;
- registro y edición;
- visualización de límite/disponible;
- compras;
- ciclos/deuda;
- pagos.

### Etapa 7 — Alertas

- evaluar alertas de vencimiento;
- integrar con el mecanismo de tareas/avisos disponible en SOFP.

## 21. Decisión arquitectónica consolidada — 08/09/2026

Después de revisar el código actual de `Cuenta`, `CuentaService`, `MovimientoService`, `GastoService` y `Obligacion`, se consolida la siguiente decisión:

**La tarjeta de crédito será una cuenta especializada (`TipoCuenta.TARJETA_CREDITO`), no una entidad paralela `TarjetaCredito`.**

La decisión se basa en que `Cuenta` ya posee:

- perfil financiero;
- institución financiera;
- moneda;
- identificador externo;
- estado activo/inactivo;
- integración natural con `Movimiento`;
- autorización y aislamiento por usuario/perfil;
- servicios y repositorios existentes.

La especialización agregará únicamente el comportamiento y los datos necesarios para crédito.

Esta decisión evita duplicar:

`PerfilFinanciero → Cuenta`

en otra estructura paralela como:

`PerfilFinanciero → TarjetaCredito`.

También mantiene separadas las responsabilidades:

- `Cuenta(TARJETA_CREDITO)` → instrumento y capacidad de crédito;
- `Movimiento` → hecho financiero de la compra o del pago;
- `Obligacion` → deuda pendiente;
- servicios → coordinación, autorización y reglas financieras;
- Swing → presentación e interacción.

## 22. Pendientes técnicos explícitos

Antes de considerar terminada la funcionalidad de tarjetas quedan pendientes, como mínimo:

1. decidir exactamente dónde almacenar los atributos específicos de tarjeta dentro de `Cuenta`;
2. definir la estrategia para calcular y persistir el crédito utilizado;
3. definir cómo asociar inequívocamente cada consumo a la cuenta tarjeta sin romper el modelo actual de `Movimiento`;
4. corregir la inconsistencia de saldo entre `CuentaService` y `MovimientoService`;
5. definir la semántica exacta del día de cierre;
6. definir la política de vencimiento en meses cortos;
7. definir cuotas e intereses;
8. definir cómo se relacionan pagos con una o varias obligaciones;
9. definir la liberación del límite ante pagos parciales, totales, anulaciones y ajustes;
10. definir si se adopta saldo a favor;
11. implementar y ejecutar los tests antes de incorporar Swing.

No se debe considerar implementada ninguna de estas reglas solamente porque esté documentada.

## 23. Regla de continuidad

Esta documentación acompaña al código, pero no reemplaza su estado real.

Ante cualquier nueva sesión de trabajo sobre tarjetas de crédito se deberá revisar primero:

`código actual → tests → commits → main → documentación`

Si el código contradice esta documentación, prevalecen el código y los tests hasta que se formalice una nueva decisión.
