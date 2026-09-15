# SOFP — Historial del proyecto

## Estado documental — 15/09/2026

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
32. Auditoría completa del ciclo de facturación aplicado al pago.
33. Implementación de reglas temporales de ciclos y pagos.
34. Validación de persistencia de obligaciones y suite completa tras adaptar tests a vencimientos de fin de semana.
35. Auditoría integral del estado técnico, multidivisa, cobertura de tests y coherencia documental.
36. Corrección de saldos y disponibilidad de fondos para trabajar por moneda.
37. Recuperación de cobertura de `CuentaService` sin modificar las reglas de negocio para hacer pasar tests.
38. Validación de `Moneda.cantidadDecimales` no negativa.

## Bloque de robustez — 15/09/2026

Se cerró la validación de `Moneda.cantidadDecimales`:

- `null` continúa siendo rechazado;
- valores negativos son rechazados;
- la regla se aplica al crear y modificar `Moneda`;
- no se agregó un límite superior arbitrario.

Commits del bloque:

- `d4fdcd9` — `fix: validar decimales no negativos en Moneda`.
- `5a6de42` — `test: validar decimales no negativos en Moneda`.

## Estado de multidivisa

El movimiento conserva su moneda económica explícita. La cuenta calcula saldo por su moneda y `MovimientoService` valida fondos con la moneda del movimiento, evitando mezclar ARS y USD.

La multidivisa de tarjetas continúa abierta: falta definir el impacto de consumos en moneda distinta sobre el límite, la moneda de liquidación y el mecanismo de conversión/liquidación trazable.

No se deben introducir conversiones implícitas.

## Estado de validación actual

- `MonedaTest`: **7/7**.
- `MonedaTest,CuentaTest,CuentaJpaTest,MovimientoTest`: **53/53**.
- `mvn test`: **693/693**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizado 15/09/2026 12:03:19 -03:00.

No existe objetivo de recuperar artificialmente el conteo histórico de 704 tests.

## Pendientes actuales

1. Multidivisa de tarjetas: límite, liquidación y pagos entre monedas.
2. Tests específicos y relacionados de multidivisa.
3. Política de eliminación de cuentas con historial.
4. Abstracción `Clock`.
5. Migraciones/versionado formal de esquema.
6. Financiación avanzada.
7. UI específica de tarjetas.
8. Pasivos, patrimonio y análisis.
9. Gestión de entidades financieras.
10. Pulido de consola.

## Continuidad Git

La rama de trabajo continúa separada de `main`. No hacer merge a `main` automáticamente. Antes de iniciar el próximo bloque se debe reconstruir el estado desde GitHub, revisar commits, comparar con `main`, revisar código y tests, y tomar la documentación solo como apoyo.
