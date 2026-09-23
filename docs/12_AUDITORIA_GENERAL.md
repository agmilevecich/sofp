# SOFP — Auditoría general del proyecto

## Estado de referencia — 22/09/2026

Esta auditoría general consolida el estado funcional y técnico reconstruido desde GitHub sobre la rama `feature/swing-shell`.

La fuente de verdad es, en este orden:

1. código actual;
2. tests actuales;
3. commits y comparación con `main`;
4. documentación de continuidad.

La documentación histórica puede quedar desactualizada y no debe prevalecer sobre código o tests.

Esta auditoría es una fotografía de referencia para continuar el desarrollo. No modifica el historial de Git ni declara la rama lista para mergear a `main`.

## 1. Estado actual

- Rama de trabajo: `feature/swing-shell`.
- Rama estable: `main`.
- La rama de trabajo continúa separada de `main`; no se realizó merge.
- La evolución reciente incluye la auditoría técnica de tarjetas, financiación y refinanciación.
- Último resultado de tests informado por el usuario al cerrar esta auditoría: **860 tests, 0 failures, 0 errors, 0 skipped, BUILD SUCCESS**.
- Duración de esa ejecución: aproximadamente 37 minutos.
- Finalización informada: **22/09/2026 20:23:26 -03:00**.

El resultado de 860/860 es una validación local informada por el usuario. No debe confundirse con una ejecución de CI de GitHub Actions.

## 2. Evolución del sistema

SOFP evolucionó desde un núcleo inicial centrado en:

`Usuario → PerfilFinanciero → Cuenta → Movimiento`

hasta un sistema financiero mucho más amplio que actualmente incluye:

- operaciones financieras coordinadas;
- categorías;
- monedas;
- instituciones financieras;
- activos y posiciones;
- obligaciones y cuotas;
- tarjetas de crédito;
- ciclos y cierres;
- liquidaciones multidivisa;
- pagos y reversiones;
- financiación;
- tasas, intereses, punitorios y cargos;
- refinanciación;
- aplicación Swing;
- persistencia JPA/Hibernate/H2;
- suite amplia de pruebas.

La documentación anterior no siempre refleja esta evolución. En caso de contradicción, prevalecen código y tests.

## 3. Arquitectura

La separación general observada es:

`UI Swing → servicios → dominio/repositorios → JPA/Hibernate/H2`

La arquitectura es funcional y suficientemente separada para continuar el desarrollo.

Fortalezas:

- reglas propias de las entidades permanecen en el dominio;
- los servicios coordinan operaciones entre entidades;
- las operaciones financieras complejas tienen flujos transaccionales;
- existe autorización por usuario en operaciones sensibles;
- JPA está separado del núcleo de reglas;
- los tests utilizan una base H2 aislada.

Pendientes técnicos:

- algunos servicios instancian internamente otros servicios/repositorios, aumentando acoplamiento;
- existen constructores de compatibilidad y cierta duplicación en UI;
- las operaciones JPA se ejecutan sincrónicamente desde acciones Swing, con riesgo de congelar la interfaz ante operaciones más pesadas.

No se recomienda una refactorización arquitectónica amplia antes de cerrar los invariantes financieros pendientes.

## 4. Dominio financiero

El dominio actual contiene, entre otros:

- `Usuario`;
- `PerfilFinanciero`;
- `Cuenta`;
- `Moneda`;
- `InstitucionFinanciera`;
- `Categoria`;
- `Movimiento`;
- `OperacionFinanciera`;
- `Activo`, `Bono`, `MovimientoActivo`, `PosicionActivo`;
- `Obligacion`, `Cuota`;
- `TarjetaCredito` y componentes asociados;
- `PagoTarjeta`;
- `Financiacion`;
- `CargoFinanciero`;
- `TasaInteres`;
- `Refinanciacion`;
- `CuotaRefinanciacion`;
- `TipoCambio`.

El uso de `BigDecimal` en importes financieros es coherente con el dominio monetario.

La principal deuda actual no está en la existencia de las entidades, sino en completar y proteger invariantes entre entidades.

## 5. Cuentas y movimientos

El saldo de una cuenta se deriva de sus movimientos. Esto evita mantener dos fuentes de verdad independientes para el saldo.

Las reglas históricas básicas están protegidas, incluyendo restricciones sobre cuentas con historial y movimientos que originan obligaciones.

### Hallazgo de alta prioridad

La validación de disponibilidad para una operación histórica debe considerar el saldo existente **hasta la fecha de la operación**, no solamente el conjunto de movimientos actuales.

Ejemplo conceptual:

- 01/09: saldo $100.000;
- 10/09: gasto histórico $80.000;
- 20/09: ingreso $100.000.

Una validación del gasto del 10/09 no debería poder utilizar el ingreso del 20/09 para justificar fondos disponibles en el pasado.

Debe incorporarse una operación equivalente a `saldoHasta(cuenta, fechaHora)` para las validaciones temporales que lo requieran.

## 6. Transferencias

`OperacionFinancieraService` coordina las dos patas de una transferencia dentro de una operación transaccional.

Esto protege la atomicidad de la transferencia.

### Hallazgo de alta prioridad

`MovimientoService` todavía puede permitir la modificación o eliminación independiente de movimientos que pertenecen a una `OperacionFinanciera` coordinada, si no están protegidos por otra regla.

Eso puede dejar una transferencia con sus dos movimientos desbalanceados o con una sola pata.

Regla pendiente recomendada para el dominio:

- un movimiento perteneciente a una operación financiera coordinada no debe poder modificarse/eliminarse aisladamente;
- debe modificarse o revertirse la operación completa, o rechazarse la operación individual.

## 7. Inversiones

Está implementado un núcleo de:

- activos;
- bonos;
- compras y ventas;
- movimientos de activos;
- posiciones;
- costo/valor promedio;
- valorización;
- ganancias y pérdidas;
- reportes básicos.

### Hallazgos de alta prioridad

1. La venta de activos debe validar la posición disponible antes de persistir una venta. Actualmente existe riesgo de registrar una venta superior a la posición y descubrir el problema posteriormente al calcular la posición.

2. La compra debe validar explícitamente la compatibilidad entre moneda de la cuenta y moneda del activo/precio, o exigir una conversión explícita. No debe existir conversión implícita entre ARS y USD.

El subsistema de inversiones se considera funcional pero todavía parcial.

## 8. Tarjetas de crédito

El núcleo de tarjetas es uno de los módulos más desarrollados del proyecto.

Está cubierto:

- tarjeta y límite;
- crédito utilizado/disponible;
- consumos;
- obligaciones;
- ciclos;
- cuotas;
- cierre;
- vencimientos;
- gracia;
- mora;
- valorización histórica;
- liquidación;
- moneda original y moneda de liquidación;
- tipo de cambio histórico;
- pagos;
- pagos parciales;
- reversiones;
- financiación;
- TNA/intereses;
- punitorios;
- cargos;
- refinanciación;
- trazabilidad;
- recuperación de crédito;
- validaciones temporales.

La auditoría técnica específica de tarjetas quedó cerrada en el estado registrado en la documentación anterior.

### Estado funcional

El dominio de tarjetas es avanzado, pero la cobertura de UI es inferior a la del dominio.

La pantalla Swing permite consulta y pago, pero todavía no expone de forma completa todas las operaciones avanzadas, especialmente:

- alta/detalle de financiación;
- alta/detalle de refinanciación;
- desglose completo de TNA/intereses;
- cargos;
- cuotas refinanciadas;
- operaciones avanzadas de reversión.

Por lo tanto, tarjeta de crédito está técnicamente avanzada pero no debe considerarse funcionalmente 100% completa en la interfaz.

## 9. Financiación

La financiación permite manejar:

- capital;
- saldo;
- estado;
- fechas;
- moneda;
- cargos;
- TNA/intereses;
- punitorios;
- pagos;
- reversiones;
- obligaciones;
- efectos sobre crédito.

Se incorporó además protección contra sobre-financiación y comportamiento para financiación multidivisa en escenarios auditados.

Pendientes:

- política completa para financiación multidivisa seguida de liquidación en otra moneda;
- conversión y cotización explícitas en ese escenario;
- trazabilidad completa de la conversión;
- política de cancelación anticipada;
- integración final de cargos con todas las métricas financieras.

## 10. Refinanciación

La refinanciación dispone actualmente de:

- plan;
- cuotas;
- saldo;
- estados;
- tasa anual;
- sistema francés implementado;
- TNA interpretada como tasa nominal anual;
- periodicidad mensual;
- desglose entre interés y capital;
- redondeo monetario;
- ajuste de última cuota;
- pagos parciales;
- pagos sobre varias cuotas;
- reversiones;
- trazabilidad mediante `PagoTarjeta` y `Movimiento`;
- persistencia y validaciones específicas.

Ejemplo actualmente cubierto por tests:

- total plan: $127.000;
- TNA: 24%;
- 3 cuotas mensuales;
- cuotas aproximadas: $44.037,84, $44.037,84 y $44.037,85.

### Hallazgo CRÍTICO

La implementación actual utiliza `totalPlan` como base de `saldoPlan`, mientras que las cuotas del sistema francés contienen capital e intereses programados.

Debe definirse con precisión qué representa `saldoPlan`:

1. capital/equivalente económico pendiente;
2. suma de saldos de cuotas;
3. importe de cancelación anticipada;
4. otra definición financiera explícita.

No son conceptos necesariamente equivalentes cuando existe interés futuro.

Existe un riesgo concreto de que `registrarPago()` lleve `saldoPlan` a cero y marque el plan como `CANCELADA` mientras alguna cuota todavía conserve saldo pendiente.

Esto debe resolverse antes de considerar cerrada la refinanciación.

Tests que deben agregarse para cerrar el invariante:

- pago parcial;
- pago total;
- consistencia plan/cuotas después del pago total;
- reversión;
- pago y reversión;
- cancelación anticipada;
- diferencia entre deuda económica y cuotas con interés futuro.

No debe modificarse la regla financiera por inferencia: primero debe quedar documentada la semántica elegida.

## 11. Multidivisa

La arquitectura distingue moneda original y moneda de liquidación cuando corresponde.

Se utiliza `TipoCambio` para mantener cotizaciones históricas y evitar conversiones implícitas.

Esto está especialmente consolidado en tarjetas y liquidaciones.

Pendiente:

- definir explícitamente todos los escenarios de financiación multidivisa con posterior liquidación en otra moneda;
- conservar cotización, fecha, origen y resultado de cada conversión.

## 12. Persistencia

Producción utiliza JPA/Hibernate con H2.

La aplicación usa H2 TCP y:

`hibernate.hbm2ddl.auto=update`

Los tests utilizan una persistencia H2 en memoria independiente, evitando contaminar la base real de desarrollo.

Esto es apropiado para la etapa actual.

Pendiente de evolución:

- migraciones/versionado formal de esquema;
- estrategia de compatibilidad de datos para futuras versiones.

`hbm2ddl.auto=update` no debe considerarse una estrategia definitiva de migración para una aplicación distribuida.

## 13. UI Swing

La aplicación Swing ya dispone de shell y paneles para buena parte del flujo financiero.

Existe integración de:

- usuarios;
- perfiles;
- cuentas;
- categorías;
- ingresos;
- gastos;
- movimientos;
- transferencias;
- inversiones;
- obligaciones;
- tarjetas;
- reportes.

El principal problema no es la ausencia de una UI básica, sino la diferencia entre lo que el dominio ya puede hacer y lo que la UI permite operar.

Pendientes:

- financiación avanzada;
- refinanciación;
- detalle financiero de tarjetas;
- reportes financieros consolidados;
- operaciones avanzadas de inversión;
- mejoras de rendimiento mediante trabajo fuera del EDT.

## 14. Reportes y patrimonio

Este es uno de los sectores menos desarrollados.

Existe reporting básico, especialmente alrededor de inversiones, pero falta un módulo consolidado de situación financiera.

Debe incorporarse progresivamente:

`Patrimonio Neto = Activos - Pasivos`

y comprobar mediante tests que:

- una transferencia no modifica el patrimonio neto;
- un ingreso lo incrementa;
- un gasto lo reduce;
- una deuda modifica pasivos;
- una inversión cambia composición patrimonial;
- una venta refleja correctamente el resultado económico;
- tarjetas, financiación y refinanciación mantienen consistencia entre deuda y movimientos.

También faltan reportes consolidados de:

- flujo de caja;
- ingresos/gastos por categoría;
- evolución patrimonial;
- deuda total;
- deuda de tarjetas;
- inversiones;
- resultados por período;
- información multidivisa comparable mediante valorización explícita.

## 15. Seguridad

Se observó:

- hashing de contraseñas mediante PBKDF2WithHmacSHA256;
- salt aleatorio;
- número elevado de iteraciones;
- clave derivada de 256 bits;
- comparación resistente a diferencias simples de tiempo;
- autorización por usuario en operaciones sensibles.

El usuario demo se crea únicamente en modo de desarrollo.

No se identificó durante la auditoría un problema crítico de seguridad que bloquee el desarrollo funcional.

Mejoras futuras:

- limitación de intentos de autenticación;
- política de bloqueo;
- mayor separación entre APIs que reciben hashes y APIs que reciben contraseñas;
- revisión de secretos/configuración antes de una distribución real.

## 16. Build y calidad técnica

El proyecto utiliza:

- Java 23;
- Maven;
- JPA/Hibernate;
- H2;
- JUnit 5;
- Logback;
- Swing;
- LGoodDatePicker;
- GitHub Actions.

Java 23 es el estado actual del proyecto y debe mantenerse documentado para evitar volver a asumir Java 21 a partir de documentación histórica.

La suite actual es amplia y constituye una fortaleza importante.

El resultado más reciente informado:

**860/860 — 0 failures — 0 errors — 0 skipped — BUILD SUCCESS**

## 17. Tests

La cantidad de tests es alta para el estado actual del proyecto y cubre numerosas reglas.

Existe cobertura de:

- dominio;
- persistencia;
- JPA;
- tarjetas;
- ciclos;
- cuotas;
- cierre;
- multidivisa;
- pagos;
- reversiones;
- financiación;
- intereses;
- punitorios;
- refinanciación;
- Swing básico;
- validaciones.

La cantidad de tests verdes no significa que todos los invariantes posibles estén cubiertos.

### Gaps prioritarios

1. Invariante `saldoPlan` ↔ cuotas de refinanciación.
2. Pago total de refinanciación con TNA positiva.
3. Pago parcial y reversión de refinanciación.
4. Cancelación anticipada.
5. Venta de activos superior a posición.
6. Compra de activo con moneda incompatible.
7. Modificación/eliminación de una pata de transferencia.
8. Saldo histórico disponible considerando únicamente movimientos hasta una fecha.
9. Consistencia de saldo después de reversión/eliminación.
10. Escenarios multidivisa de financiación y liquidación posterior.

## 18. Contraste documentación → código → tests → comportamiento

La auditoría confirma una diferencia importante entre la documentación histórica y el estado real.

Ejemplos:

- documentos antiguos describían financiación/refinanciación como pendientes, mientras que el código actual ya contiene una implementación avanzada;
- algunos documentos declaraban cerrados bloques que posteriormente recibieron correcciones;
- el sistema de refinanciación avanzó hasta incorporar amortización francesa;
- la suite pasó de resultados históricos inferiores hasta los **860 tests verdes actuales**.

Por ello, los documentos de continuidad deben registrar cambios incrementales, pero nunca reemplazar la inspección del código y los tests.

## 19. Clasificación de pendientes

### CRÍTICO

- Definir y corregir la semántica de `saldoPlan` y su consistencia con las cuotas de refinanciación.

### ALTO

- Proteger las dos patas de una `OperacionFinanciera` contra mutación independiente.
- Validar posición antes de vender activos.
- Validar moneda de cuenta/activo en compras.
- Implementar saldo/disponibilidad histórica por fecha.

### MEDIO

- Completar UI avanzada de tarjetas, financiación y refinanciación.
- Implementar patrimonio neto y reportes consolidados.
- Definir calendario bancario de feriados.
- Introducir `Clock`.
- Formalizar migraciones de esquema.
- Evitar trabajo JPA pesado en el EDT.
- Resolver acoplamientos menores entre servicios.

### BAJO / EVOLUTIVO

- Pulido de constructores de compatibilidad.
- Limpieza de duplicaciones de UI.
- Mejoras de documentación no relacionadas con continuidad.

## 20. Estimación orientativa del avance

Estas cifras son estimaciones cualitativas de madurez funcional, no porcentaje de código ni cobertura de tests:

| Área | Estado aproximado |
|---|---:|
| Arquitectura | 75–85% |
| Dominio | 75–85% |
| Persistencia | 75–85% |
| Lógica financiera | 70–80% |
| Tarjetas | 80–90% |
| Financiación | 75–85% |
| Refinanciación | 65–75% |
| Inversiones | 50–65% |
| UI | 55–70% |
| Reportes/patrimonio | 30–45% |
| Tests | 80–90% |
| Documentación | 50–65% |

Estimación global orientativa: **65–75%**.

La cifra no debe utilizarse como métrica automática de progreso. Para continuidad es más útil seguir los invariantes y funcionalidades pendientes.

## 21. Orden de trabajo recomendado

El orden de desarrollo que surge de esta auditoría es:

1. Resolver el invariante crítico de refinanciación.
2. Agregar y ejecutar sus tests específicos y relacionados.
3. Proteger operaciones financieras coordinadas.
4. Corregir validaciones de inversiones.
5. Corregir disponibilidad histórica de cuentas.
6. Ejecutar suite completa.
7. Implementar patrimonio neto y reportes consolidados.
8. Completar UI avanzada.
9. Realizar endurecimiento técnico: `Clock`, calendario bancario, migraciones y rendimiento Swing.
10. Recién después evaluar una etapa de cierre y eventual merge a `main`.

No se debe modificar `main` automáticamente.

## 22. Criterio de continuidad

Cada nueva sesión de SOFP debe reconstruir:

`GitHub → rama → últimos commits → comparación con main → código → tests → documentación → último resultado informado → próximo cambio mínimo`

Si una documentación contradice el código actual, prevalecen código y tests.

Cada funcionalidad nueva debe mantener:

- regla de negocio explícita;
- validaciones;
- persistencia;
- relaciones;
- casos límite;
- tests específicos;
- tests relacionados;
- suite completa cuando corresponda;
- `git diff`;
- `git diff --check`;
- `git status`;
- actualización de documentación al cerrar una etapa importante.

## 23. Conclusión

SOFP ya no está en una etapa de prototipo simple. Tiene un núcleo financiero considerable, una arquitectura razonable, persistencia real, una aplicación Swing funcional y una suite de regresión de **860 tests verdes**.

El proyecto todavía no debe considerarse terminado porque existen invariantes financieros relevantes por cerrar, especialmente en refinanciación, operaciones coordinadas, inversiones y reconstrucción histórica de saldos. También existe una diferencia importante entre la potencia del dominio y lo que actualmente expone la UI.

La prioridad inmediata queda fijada en el **invariante de refinanciación entre saldo del plan y cuotas**, seguida por las protecciones de operaciones financieras, inversiones y temporalidad histórica.

Este documento queda como **base general de continuidad** para las próximas etapas del proyecto.
