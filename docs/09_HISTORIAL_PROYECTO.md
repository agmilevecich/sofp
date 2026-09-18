# SOFP — Historial del proyecto

## Estado documental — 17/09/2026

Los estados técnicos deben verificarse siempre contra código, tests y Git. Este documento registra hitos; no reemplaza la inspección del estado real.

## Hitos principales

1. Construcción progresiva del dominio financiero con JPA/Hibernate y H2.
2. Consolidación de `Movimiento` como núcleo financiero común.
3. Implementación de operaciones financieras e inversiones.
4. Auditoría transversal de seguridad y aislamiento por usuario/perfil.
5. Construcción del shell Swing.
6. Integración de cuentas, categorías, movimientos, inversiones y reportes.
7. Gastos e Ingresos como paneles especializados sobre `Movimiento`.
8. Integración de `FormaPago`.
9. Obligaciones y pagos para compras con tarjeta de crédito.
10. Autorización de pagos por usuario y UI de obligaciones.
11. Transferencias mediante `OperacionFinanciera`.
12. Moneda explícita en movimientos y obligaciones.
13. Crédito disponible y validación de límite de tarjeta.
14. Conservación de consumos de tarjeta sin obligación sin doble contabilización.
15. Aislamiento del contexto JPA/H2 en tests.
16. Ciclos de facturación y vencimientos básicos en el dominio.
17. Integración inicial de cuotas al registro de gastos con tarjeta.
18. Corrección de tests para respetar la generación automática de cuotas.
19. Configuración de H2 de la aplicación mediante servidor TCP compartido con H2 Console.
20. Selección explícita de tarjeta de crédito en `GastosPanel`.
21. Cobertura de cuotas al cruzar el fin de año.
22. Atomicidad de compra con tarjeta mediante coordinación transaccional.
23. Configuración de JAR ejecutable y dependencias runtime.
24. Auditoría funcional transversal del modelo de tarjeta, obligaciones, pagos, autorización y mutabilidad.
25. Protección de movimientos que originan obligaciones frente a cambios estructurales y eliminación.
26. Integración del pago coordinado de tarjeta en `ObligacionesPanel`.
27. Integración de `PagoTarjetaService` en `MainFrame` y `Main`.
28. Cobertura de pago real desde UI y saldo de la cuenta pagadora.
29. Cierre de la superficie pública de `ObligacionService`.
30. Auditoría transversal de integridad de `Cuenta`.
31. Implementación y validación de la integridad estructural de `Cuenta`.
32. Auditoría completa del ciclo de facturación aplicado al pago.
33. Implementación de reglas temporales de ciclos y pagos.
34. Validación de persistencia de obligaciones y suite tras adaptar tests a vencimientos de fin de semana.
35. Auditoría integral del estado técnico, multidivisa, cobertura de tests y coherencia documental.
36. Corrección de saldos y disponibilidad de fondos para trabajar por moneda.
37. Recuperación de cobertura de `CuentaService` sin modificar reglas de negocio.
38. Validación de `Moneda.cantidadDecimales` no negativa.
39. Modelo histórico de `TipoCambio` con validación y conversión según decimales de la moneda destino.
40. Separación de moneda original y moneda de liquidación en `Obligacion`.
41. Persistencia de ambas monedas con compatibilidad para datos existentes.
42. Asociación de `TipoCambio` histórico a la obligación.
43. Liquidación explícita de obligaciones multidivisa con trazabilidad y protección contra doble liquidación.
44. Saldo de liquidación persistente en `Obligacion`.
45. Integración del pago multidivisa en `PagoTarjetaService`.
46. Valorización histórica de cierre separada de la liquidación.
47. Persistencia y validación de la valorización de cierre.
48. Integración de la valorización de cierre en el cálculo de crédito disponible.
49. Validación de crédito de tarjeta con obligación multidivisa valorizada.
50. Corrección del crédito utilizado proporcionalmente después de pagos parciales en obligaciones multidivisa valorizadas.
51. Integración del cierre de ciclo desde `ObligacionesPanel`.
52. Corrección del crédito utilizado después de liquidar una obligación multidivisa.
53. Cobertura del flujo parcial → liquidación → pago total y liberación completa del crédito.
54. Suite completa de regresión en **756/756 tests verdes**.
55. Cobertura de integración del ciclo completo de tarjeta multidivisa y ajuste de comparaciones `BigDecimal` del test.
56. Suite completa de regresión en **761/761 tests verdes**.

## Validación actual

La suite general actual es **761/761**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 17:54:49 -03:00**.

Validaciones específicas del último bloque:

- `TarjetaCreditoMultidivisaIntegracionTest`: 1/1, `BUILD SUCCESS`, 17/09/2026 17:29:45 -03:00.
- `TarjetaCreditoPagoCreditoTest`: 5/5.
- `ObligacionServiceLiquidacionTest`: 4/4.
- Ejecución relacionada: 9/9, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, 17/09/2026 17:40:50 -03:00.

El usuario informó `git diff` limpio, `git diff --check` sin observaciones, `git status` limpio y rama local alineada con `bitbucket/feature/swing-shell` antes de la actualización documental.

## Pendientes actuales

1. Revisar `ObligacionService` y el flujo de cierre de resumen.
2. Definir, con referencia a normativa BCRA y documentación vigente de la entidad tomada como referencia, cómo se obtiene y aplica la cotización de cierre para consumos extranjeros.
3. Definir obligaciones multidivisa todavía no valorizadas al cierre.
4. Completar persistencia/UI del flujo integral de cierre, liquidación y pago multidivisa.
5. Revisar consumos extranjeros sobre crédito antes de disponer de valorización.
6. Política de eliminación de cuentas con historial.
7. Abstracción `Clock`.
8. Migraciones/versionado formal de esquema.
9. Financiación avanzada.
10. UI específica de tarjetas.
11. Pasivos, patrimonio y análisis.
12. Gestión de entidades financieras.
13. Pulido de consola.

## Estabilización futura antes de main

Como etapa separada, previa al fast-forward a `main`:

- arranque automático de H2 desde Java;
- cierre limpio de H2;
- ocultar salida técnica de consola;
- logging técnico a archivo;
- `JOptionPane` para fallos de conexión con base de datos y otros errores de arranque;
- no mostrar una ventana parcialmente inicializada si el arranque falla.

## Continuidad Git

La rama de trabajo continúa separada de `main`. No hacer merge a `main` automáticamente. Antes de iniciar el próximo bloque se debe reconstruir el estado desde GitHub, revisar commits, comparar con `main`, revisar código y tests, y tomar la documentación solo como apoyo.


## Actualización de continuidad — cierre 17/09/2026 22:51 -03:00

Esta sección supersede cualquier validación anterior de este documento cuando haya contradicción.

- Rama de trabajo: `feature/swing-shell`.
- HEAD actual: `b4a9bc6b4f64ce191f90eb9e4dd4291356e0574c` — `test: corregir expectativas de valorizacion multidivisa`.
- `main`: `a4be85913847200cb70976d5266d9cbba10b3100`.
- Comparación GitHub: `feature/swing-shell` está 847 commits por delante de `main` y 0 por detrás.
- No se realizó merge a `main`.
- Último bloque: corrección de expectativas de tests para reflejar que la valorización de cierre de obligaciones financiadas se almacena en la cuota; no se modificó producción en este último commit.
- Validación específica posterior: 8/8 tests verdes, 0 failures, 0 errors, `BUILD SUCCESS`, informada por el usuario.
- Validación final: `mvn test` con **769/769 tests**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **17/09/2026 22:51:16 -03:00**, informada por el usuario.
- Validación Git local final: `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio; rama local al día con `bitbucket/feature/swing-shell`, informado por el usuario.

### Punto exacto para retomar

El cálculo de crédito multidivisa, la valorización histórica de cierre, la liquidación explícita, los pagos antes/después de liquidar y el cierre iniciado desde `ObligacionesPanel` están cubiertos por tests. El siguiente bloque debe comenzar con una revisión de `ObligacionService` y de sus clases relacionadas para definir el flujo de cierre de resumen de tarjeta siguiendo reglas bancarias reales. Antes de modificar código se debe contrastar la cotización de cierre de consumos extranjeros con normativa BCRA y documentación vigente de la entidad financiera de referencia. No inventar una regla de negocio por inferencia.

### Regla de continuidad para la próxima sesión

Reconstruir desde GitHub antes de cualquier cambio: rama → últimos commits → comparación con `main` → código relacionado → tests → documentación → último resultado informado → próximo cambio mínimo. No asumir que la documentación histórica representa el estado actual si contradice código o tests.