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


## ACTUALIZACIÓN DE CONTINUIDAD — CIERRE 23/09/2026

Esta sección supersede los resultados anteriores cuando exista contradicción.

### Estado actual
- Rama de trabajo: `feature/swing-shell`.
- HEAD: `0550d6248b12ecf2a8019d1328a8def430062c2b` — `test: corregir credito esperado de pago de refinanciacion`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Feature: 23 commits por delante de `main`, 0 por detrás.
- No se realizó merge a `main`.

### Cierre de la incidencia de refinanciación
El último fallo de la suite estaba en expectativas antiguas de tests de crédito asociado a refinanciación. El comportamiento actual de producción considera en `saldoPlan` el interés programado al generar las cuotas, por lo que se actualizaron las expectativas de los tests sin modificar producción.

Resultados finales informados por el usuario:
- `ObligacionRepositoryFinanciacionCreditoTest`: 1/1 verde.
- Bloque relacionado: 24/24 verde.
- `PagoTarjetaServiceTest`: 23/23 verde.
- `mvn test`: **861/861**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalización: **23/09/2026 20:29:58 -03:00**.

La validación local posterior de Git mostró la rama sincronizada con los remotos y sin diferencias en `git diff` ni observaciones de `git diff --check`. El único archivo que había quedado sin seguimiento, `salida-refinanciacion.txt`, fue eliminado posteriormente por haber cumplido su función de diagnóstico.

### Depuración descartada
La rama `debug/refinanciacion-errores` y `salida-refinanciacion.txt` fueron artefactos temporales utilizados para localizar la causa de los fallos. Ambos fueron eliminados y no constituyen estado pendiente.

### Próximo punto de trabajo
La refinanciación ya tiene cobertura funcional relevante y la suite está verde, pero permanece como siguiente bloque la definición y protección del invariante entre `saldoPlan`, cuotas y estado de la refinanciación en pagos parciales, pago total, reversión y cancelación anticipada. El próximo cambio debe partir de una nueva inspección del código y tests actuales, sin asumir que la documentación histórica refleja el HEAD.

### Regla de continuidad
Para la próxima sesión: reconstruir GitHub antes de cualquier cambio y registrar rama, HEAD, comparación con `main`, código relacionado, tests, último resultado y próximo cambio mínimo. No modificar `main` ni crear cambios de producción sin confirmar primero la regla de negocio.


## ACTUALIZACIÓN DE CONTINUIDAD — 24/09/2026 12:46 -03:00

Esta sección supersede los estados anteriores cuando exista contradicción. La fuente de verdad continúa siendo el código, los tests y GitHub.

### Estado real actual

- Rama de trabajo: `feature/swing-shell`.
- HEAD actual: `dcb85822199639819a02d81a5d55753f292e4617` — `fix: bloquear toda mutacion independiente de operaciones financieras`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Comparación GitHub: **35 commits por delante de `main`, 0 por detrás**.
- No se realizó merge a `main`.

### Estado de tests conocido

La última suite completa informada anteriormente fue `mvn test`: **861/861**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada el 23/09/2026 a las 20:29:58 -03:00.

Después de esa suite se cerró el bloque de operaciones financieras coordinadas. Las validaciones locales más recientes son:

- `MovimientoServiceTest`: **55/55**, 0 failures, 0 errors, 0 skipped.
- `OperacionFinancieraTest` + `OperacionFinancieraServiceTest` + `MovimientoServiceTest`: **101/101**, 0 failures, 0 errors, 0 skipped.
- Última ejecución: **24/09/2026 12:46:37 -03:00**.

No debe interpretarse el resultado 101/101 como una nueva suite completa: corresponde únicamente al bloque conjunto indicado.

### Operaciones financieras coordinadas — CERRADO

El hallazgo anterior que figuraba como pendiente —mutación o eliminación independiente de una pata de `OperacionFinanciera`— queda **resuelto y validado**.

La protección cubre:

- modificaciones estructurales de `Movimiento` asociado;
- modificaciones estructurales de `MovimientoActivo` asociado;
- modificación o eliminación de movimientos asociados desde `MovimientoService`.

La regla de continuidad es que una operación financiera coordinada no debe poder quedar parcialmente alterada mediante APIs individuales.

### Refinanciación — estado actual

El núcleo de refinanciación ya contiene generación de cuotas, sistema francés con TNA mensual, desglose de interés/capital, pagos, reversiones, persistencia y validaciones estructurales. Los tests relacionados y la suite completa conocida quedaron verdes en el estado informado del 23/09/2026.

La documentación histórica contiene formulaciones anteriores sobre `saldoPlan`. Para nuevas decisiones debe prevalecer siempre el código y los tests actuales; no se debe modificar la semántica financiera por inferencia documental.

### Próximo hallazgo prioritario — inversiones

El siguiente punto de auditoría es **validar que una venta de activos no pueda superar la posición disponible**.

Antes de modificar código se debe revisar desde GitHub:

- `OperacionFinancieraService`;
- `MovimientoActivo`;
- `PosicionActivo`;
- repositorios de posiciones/movimientos de activos;
- `OperacionFinancieraServiceTest` y tests de posiciones.

La implementación debe reutilizar la fórmula de posición ya existente en SOFP y agregar únicamente la validación mínima necesaria.

### Pendientes posteriores

1. Venta superior a posición disponible.
2. Compatibilidad de moneda cuenta/activo/precio en compras.
3. Disponibilidad histórica de fondos por fecha.
4. Nueva suite completa después de cerrar los invariantes financieros prioritarios.
5. Patrimonio neto y reportes consolidados.
6. UI avanzada de tarjetas, financiación, refinanciación e inversiones.
7. Endurecimiento técnico: `Clock`, calendario bancario, migraciones y rendimiento Swing.

### Criterio de continuidad

En cada nueva sesión reconstruir: GitHub → rama → últimos commits → comparación con `main` → código → tests → documentación → último resultado informado → próximo cambio mínimo. Si documentación y código contradicen, prevalecen código y tests.


## ACTUALIZACIÓN CANÓNICA — CIERRE DE CONTINUIDAD — 24/09/2026 21:11 -03:00

Esta sección supersede cualquier estado anterior de este documento cuando exista contradicción. La fuente de verdad es el código y los tests actuales de `feature/swing-shell`; la documentación histórica queda como registro.

### Estado Git reconstruido desde GitHub

- Rama de trabajo: `feature/swing-shell`.
- HEAD verificado: `b57cb2709262352f492ad81c75cde7f6a879df27` — `test: cubrir saldo historico por la ruta publica`.
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`.
- Comparación GitHub: `feature/swing-shell` está **61 commits por delante de `main` y 0 por detrás**.
- No se realizó merge a `main`.

### Estado funcional actualizado

Los siguientes bloques que figuraban como pendientes en versiones anteriores ya fueron implementados y validados:

- protección de movimientos asociados a `OperacionFinanciera` contra modificación/eliminación independiente;
- validación de posición disponible antes de vender activos;
- validación de moneda entre cuenta y activo en operaciones de inversión, sin conversión implícita;
- cálculo de disponibilidad histórica de una cuenta limitado a los movimientos existentes hasta la fecha/hora de la operación;
- regresión específica que impide que un ingreso futuro financie un egreso histórico;
- cobertura ampliada de refinanciación, financiación y operaciones financieras relacionadas.

### Validación más reciente

La suite completa ejecutada localmente e informada por el usuario queda registrada como:

- **883 tests**;
- **0 failures**;
- **0 errors**;
- **0 skipped**;
- **BUILD SUCCESS**;
- duración: **31:37 min**;
- finalización: **24/09/2026 21:11:38 -03:00**.

Este resultado sustituye como referencia de continuidad a los resultados históricos de 860/860, 861/861, 841/841, 848/848 y anteriores. Es una ejecución local informada por el usuario y no debe presentarse como resultado de GitHub Actions.

### Validaciones específicas inmediatamente anteriores

- refinanciación: **34/34** verdes;
- `MovimientoRepositoryTest` + `MovimientoServiceTest`: **62/62** verdes;
- operaciones financieras relacionadas: **78/78** verdes.

### Pendientes vigentes

La prioridad ya no está en los cuatro invariantes técnicos corregidos durante esta etapa. Las próximas líneas de evolución son:

1. patrimonio neto y reportes financieros consolidados;
2. ampliar la UI Swing para cubrir operaciones avanzadas ya disponibles en dominio/servicios;
3. revisar si las posiciones de activos necesitan consultas históricas por fecha y, en ese caso, definir la regla antes de implementarla;
4. completar reglas explícitas para escenarios multidivisa que todavía no estén definidos;
5. mejoras técnicas de `Clock`, calendario bancario de feriados, migraciones/versionado de esquema y rendimiento Swing/JPA.

No se debe convertir un pendiente de definición de negocio en una implementación por inferencia.

### Criterio de continuidad vigente

Cada nueva sesión debe reconstruir desde GitHub:

`rama → últimos commits → comparación con main → código relacionado → tests → documentación → último resultado informado → cambio mínimo`

Después de cada cambio importante corresponde validar tests específicos, tests relacionados y suite completa cuando corresponda, además de `git diff`, `git diff --check`, `git status` y documentación.

La rama activa continúa siendo `feature/swing-shell`. No se debe modificar ni mergear `main` automáticamente.
