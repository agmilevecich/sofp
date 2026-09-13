# SOFP — Historial del proyecto

## Estado documental — 13/09/2026

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
29. Cierre de la superficie pública de `ObligacionService`: se eliminó el registro de pagos sin `usuarioId`.
30. Auditoría transversal de integridad de `Cuenta`.
31. Implementación y validación de la integridad estructural de `Cuenta`.
32. **Auditoría completa del ciclo de facturación aplicado al pago.**

## Auditoría temporal — cierre documental 13/09/2026

Se revisaron las clases de dominio y servicio responsables del ciclo y pago: `Cuenta`, `CicloFacturacion`, `Cuota`, `Obligacion`, `PagoTarjetaService` y `CuentaService`, junto con los tests relacionados.

### Confirmado como implementado

- cálculo del ciclo según fecha de consumo y cierre;
- inicio posterior al cierre anterior;
- cierre en mes actual o siguiente según consumo;
- ajuste a último día real del mes;
- vencimiento posterior al cierre;
- cruce de año;
- persistencia de fechas de las cuotas;
- pagos parciales y aplicación en orden ascendente;
- movimiento real de salida para el pago;
- coincidencia de moneda entre obligación y cuenta pagadora.

### Confirmado como no implementado

- validación temporal del pago respecto del consumo;
- clasificación en término/mora;
- período de gracia;
- ajuste por fines de semana y feriados;
- fecha efectiva independiente;
- rechazo de fechas futuras;
- tratamiento temporal de pagos parciales sobre cuotas vencidas;
- protección histórica de `diaCierre`/`diaVencimiento` después de existir consumos;
- congelamiento del ciclo de una obligación sin cuotas frente a cambios posteriores de configuración.

### Decisión de alcance

No se implementó ninguna de estas reglas por inferencia. La auditoría queda cerrada identificando exactamente qué existe y qué falta. Intereses, punitorios, CFT, refinanciación y demás financiación avanzada permanecen fuera de este bloque.

## Estado de validación

Tests específicos `CuentaServiceIntegridadTest,CuentaServiceTest`: **66/66**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite relacionada `CuentaServiceTest,CuentaTest,MovimientoServiceTest,OperacionFinancieraServiceTest,CuentaServiceIntegridadTest`: **154/154**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

Suite completa `mvn test`: **700/700**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Pendientes

1. Implementar las reglas temporales de pago una vez fijadas explícitamente.
2. Definir multidivisa de tarjetas.
3. Financiamiento avanzado.
4. UI específica de tarjetas.
5. Pasivos, patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.

No hacer merge a `main` automáticamente.
