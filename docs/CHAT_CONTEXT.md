# SOFP — Contexto para continuar con ChatGPT

## Estado actual — 14/09/2026

La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar y ante contradicción prevalecen código y tests.

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.

**Último bloque funcional:** reglas temporales de ciclos y pagos de tarjeta.
**Último commit funcional/test:** `3a001a57` — `test: adaptar persistencia de obligaciones a reglas temporales`.
**Último bloque documental:** actualización integral de continuidad posterior a ese cierre.

No se realizó merge a `main`.

## Validación más reciente

Suite general informada por el usuario: **704/704**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 13/09/2026 22:05:14 -03:00.

`ObligacionJpaTest`: **2/2**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 13/09/2026 21:12:49 -03:00.

Validaciones anteriores relevantes: integridad de `Cuenta` **66/66** específicos y **154/154** relacionados; `ObligacionServiceTest` **9/9**; suite de obligaciones/pagos/UI **69/69**; UI de pago **6/6**.

## Estado consolidado

La Fase Swing está integrada. Gastos con tarjeta generan movimiento + obligación + cuotas. El pago coordinado existe en `PagoTarjetaService` y está integrado en la UI.

`ObligacionService` exige `usuarioId` para registrar pagos y valida el perfil propietario.

`CuentaService` protege la integridad estructural: no permite cambiar tipo ni moneda cuando existen movimientos y la API genérica no permite transiciones hacia o desde `TARJETA_CREDITO`.

## Reglas temporales implementadas

- ciclo histórico de la obligación persistido al crearla;
- fechas de ciclo persistidas en cuotas;
- vencimiento de sábado/domingo desplazado al lunes;
- días de gracia configurables, por defecto 0;
- mora evaluada sobre vencimiento efectivo más gracia;
- pago anterior al consumo rechazado;
- pago futuro rechazado;
- pagos parciales aplicados en orden ascendente de cuotas;
- estabilidad histórica frente a cambios posteriores de configuración cuando existen datos persistidos;
- compatibilidad con obligaciones antiguas mediante campos nullable/fallback.

No están implementados calendario de feriados, fecha efectiva separada del movimiento, intereses, punitorios, CFT ni refinanciación.

## Integridad y persistencia

El movimiento origen de una obligación está protegido frente a cambios estructurales que romperían la correspondencia histórica. Los tests verifican que los valores originales persisten cuando una modificación prohibida es rechazada.

La adaptación de `ObligacionJpaTest` utiliza una cuenta de crédito real y refleja el vencimiento efectivo de fin de semana.

## Pendientes reales

P1:

1. Multidivisa de tarjetas.
2. Financiación avanzada.
3. Evaluar abstracción de reloj (`Clock`) para hacer determinista la validación de fechas futuras.

P2/P3:

4. UI específica de tarjetas.
5. Pasivos, patrimonio y análisis.
6. Gestión de entidades financieras.
7. Pulido de consola.

## Protocolo

Antes de cada bloque: reconstruir desde GitHub rama → últimos commits → comparación con `main` → documentación → implementación → clases relacionadas → tests → último resultado informado. Luego cambio mínimo → tests específicos → relacionados → suite → diff → diff-check → status → documentación.

La documentación de continuidad se actualiza sobre la rama activa. No modificar `main`, no asumir tests locales no informados y no considerar cerrada una funcionalidad solo porque compila.
