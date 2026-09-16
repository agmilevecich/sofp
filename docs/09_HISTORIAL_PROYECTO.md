# SOFP — Historial del proyecto

## Estado documental — 16/09/2026

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
52. Suite completa de regresión en **744/744 tests verdes**.

## Validación actual

La suite general actual es **744/744**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada **16/09/2026 18:47:51 -03:00**, con duración de 10:32 min.

La validación específica de `ObligacionesPanelTest` fue **6/6**, `BUILD SUCCESS`, finalizada **18:22:54 -03:00**.

La copia local fue verificada con `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio.

## Pendientes actuales

1. Definir el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir el comportamiento de una obligación multidivisa todavía no valorizada al cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Revisar consumos extranjeros sobre crédito antes de disponer de valorización.
5. Política de eliminación de cuentas con historial.
6. Abstracción `Clock`.
7. Migraciones/versionado formal de esquema.
8. Financiación avanzada.
9. UI específica de tarjetas.
10. Pasivos, patrimonio y análisis.
11. Gestión de entidades financieras.
12. Pulido de consola.

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
