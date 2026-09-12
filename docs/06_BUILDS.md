# SOFP — Historial de Builds

## Build actual — cierre de etapa y auditoría

**Estado: VALIDADO.**

Último commit de código: `6c1b896` — `build: configurar jar ejecutable y dependencias`.

El build actual configura un JAR ejecutable con `Main-Class` y copia de dependencias runtime a `target/lib`.

El usuario verificó el arranque con:

`java -Dsofp.dev=true -jar target/SOFP-1.0-SNAPSHOT.jar`

## Validación más reciente

Suite general más reciente informada por el usuario:

- `mvn test`;
- **690/690**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

El conteo 690 reemplaza al histórico 689 documentado el 11/09/2026.

## Bloques funcionales consolidados

Fondos insuficientes, categorías con movimientos, Gastos, Ingresos, FormaPago, Obligaciones/pagos, autorización por usuario, Transferencias, moneda explícita, crédito/límite de tarjeta, ciclos básicos de facturación, cuotas, atomicidad de compra con tarjeta, selección explícita de tarjeta, H2 TCP y JAR ejecutable.

## Auditoría posterior

La auditoría del estado actual detectó como próximos bloques principales:

1. proteger movimientos que originan obligaciones;
2. cerrar superficies públicas de `ObligacionService` que puedan bypassar autorización;
3. integrar `PagoTarjetaService` en `ObligacionesPanel` y completar el flujo real de pago;
4. proteger cambios estructurales de tipo/moneda de cuentas con historial;
5. definir reglas de ciclo aplicadas al pago;
6. definir tratamiento multidivisa de tarjetas;
7. financiación avanzada y UI específica.

La auditoría también confirmó que no deben seguir figurando como pendientes independientes la generación básica de cuotas, la atomicidad básica de compra, la configuración del JAR y la base de ciclos/cuotas.

No se modificó código funcional como consecuencia de esta auditoría.
