# SOFP — Historial del proyecto

## Estado documental — 14/09/2026

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
33. **Implementación de reglas temporales de ciclos y pagos.**
34. **Validación de persistencia de obligaciones y suite completa tras adaptar tests a vencimientos de fin de semana.**

## Bloque temporal — cierre 14/09/2026

Se implementó y validó el bloque temporal identificado en la auditoría anterior.

### Reglas confirmadas

- cálculo del ciclo según fecha de consumo y cierre;
- persistencia histórica del ciclo en `Obligacion`;
- persistencia de inicio, cierre y vencimiento en `Cuota`;
- vencimiento ajustado cuando cae sábado o domingo;
- días de gracia configurables, con valor por defecto 0;
- evaluación de mora sobre el vencimiento efectivo más gracia;
- rechazo de pagos anteriores al movimiento de consumo;
- rechazo de pagos con fecha futura;
- pagos parciales y aplicación en orden ascendente de cuotas;
- estabilidad del ciclo histórico de obligaciones cuando la configuración de la tarjeta cambia posteriormente, cuando existen los datos históricos persistidos;
- compatibilidad con datos existentes mediante campos nullable y fallback para registros históricos sin esos datos;
- mantenimiento de las reglas financieras fuera de alcance: no se inventaron intereses, punitorios, CFT ni refinanciación.

## Adaptación de persistencia

`ObligacionJpaTest` tuvo que adaptarse a las reglas temporales: el fixture que crea una obligación mediante un movimiento con `FormaPago.TARJETA_CREDITO` utiliza una cuenta de crédito real y el vencimiento esperado refleja el desplazamiento del fin de semana.

La adaptación quedó en el commit `3a001a57c435237e62ab04f6c09a0657ff24fcb2`.

## Estado de validación conocido

`mvn -Dtest=ObligacionJpaTest test`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado 13/09/2026 21:12:49 -03:00.

`mvn test`: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado 13/09/2026 22:05:14 -03:00.

Validaciones previas relevantes: integridad de `Cuenta` **66/66** específicos y **154/154** relacionados; `ObligacionServiceTest` **9/9**; suite de obligaciones/pagos/UI **69/69**; UI de pago de tarjeta **6/6**.

## Pendientes actuales

1. Multidivisa de tarjetas.
2. Financiación avanzada: intereses, CFT, cuotas variables, adelantos, refinanciación, anulaciones/reversiones y ajustes.
3. Refinamiento mediante `Clock` para hacer determinista la validación de fechas futuras en tests futuros.
4. UI específica de tarjetas.
5. Pasivos, patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.
8. Calendario de feriados y fecha efectiva separada, solo si se definen como reglas de negocio.

## Continuidad Git

La rama de trabajo continúa separada de `main`. No hacer merge a `main` automáticamente. Antes de iniciar el próximo bloque se debe reconstruir el estado desde GitHub, revisar commits, comparar con `main`, revisar código y tests, y tomar la documentación solo como apoyo.
