# SOFP — Contexto final de continuidad

## Estado auditado — 14/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar. Antes de proponer cambios, reconstruir siempre el estado desde GitHub.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** reglas temporales de ciclos y pagos de tarjeta.
**Último commit funcional/test:** `3a001a57` — `test: adaptar persistencia de obligaciones a reglas temporales`.
**Último bloque documental:** auditoría integral del estado técnico, multidivisa, cobertura y roadmap.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 13/09/2026 22:05:14 -03:00.

`ObligacionJpaTest`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.

## Estado consolidado

La integridad de `Cuenta` está implementada: una cuenta con movimientos no puede cambiar de tipo ni de moneda, y no se permiten transiciones genéricas hacia o desde `TARJETA_CREDITO`.

El movimiento origen de una obligación está protegido frente a modificaciones estructurales incompatibles con la obligación y las pruebas cubren la conservación de los valores originales cuando la modificación es rechazada.

El pago de tarjeta está coordinado por `PagoTarjetaService`, exige autorización por usuario y está integrado en la UI.

La auditoría integral confirmó que la arquitectura general no necesita rehacerse.

## Reglas temporales cerradas

- ciclo histórico de la obligación persistido al crearla;
- fechas históricas persistidas en `Cuota`;
- vencimiento de fin de semana desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada sobre vencimiento efectivo más gracia;
- pago anterior al consumo rechazado;
- fecha futura rechazada;
- pagos parciales en orden ascendente de cuotas;
- estabilidad histórica frente a cambios posteriores de configuración cuando existen datos persistidos;
- compatibilidad con datos existentes mediante campos nullable/fallback.

No se implementaron feriados, fecha efectiva separada, intereses, punitorios, CFT ni refinanciación.

## Hallazgo principal de auditoría: multidivisa

El modelo conserva moneda explícita en los movimientos y obligaciones y evita conversiones implícitas, pero la multidivisa todavía no está cerrada.

Problemas confirmados:

1. Los cálculos de saldo de cuenta mezclan importes de monedas diferentes.
2. La validación de fondos utiliza ese saldo mezclado.
3. Un consumo de tarjeta en moneda distinta de la tarjeta no tiene una regla completa de impacto sobre el límite.
4. El pago coordinado exige misma moneda y no existe todavía liquidación/conversión trazable.

Antes de implementar conversiones deben definirse moneda de liquidación, tasa, fecha/fuente de cotización y representación de saldos por moneda.

## Hallazgos secundarios

- `Moneda.cantidadDecimales` necesita una regla de rango explícita.
- La eliminación de cuentas con historial financiero necesita una política de dominio explícita.
- `PagoTarjetaService` puede beneficiarse de `Clock` para tests deterministas.
- `hibernate.hbm2ddl.auto=update` debe reemplazarse por migraciones/versionado si el proyecto pasa a una etapa de distribución/producción.

## Pendientes

1. Multidivisa de tarjetas: saldos, fondos, crédito y liquidación por moneda.
2. Tests específicos de multidivisa.
3. Robustez de `Moneda`, eliminación histórica y `Clock`.
4. Financiación avanzada.
5. UI específica de tarjetas.
6. Pasivos, patrimonio y análisis.
7. Gestión de entidades financieras.
8. Pulido de consola.

## Documentación

La auditoría también detectó y corrigió el roadmap, que todavía decía que Swing no había comenzado y conservaba 512/512 como validación global vigente. El estado actual documentado ya refleja la implementación real de Swing y la validación conocida 704/704.

## Orden de continuidad

Antes de cada nuevo bloque: revisar rama y commits actuales, comparar con `main`, revisar documentación como apoyo, inspeccionar implementación y clases relacionadas, revisar tests y tomar como válido únicamente el último resultado informado por el usuario. Después: cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

No modificar `main` automáticamente y no asumir resultados locales no informados.
