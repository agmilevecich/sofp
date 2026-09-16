# SOFP — Continuidad 2026-09-16

## Estado auditado

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell`.
**Último commit de código antes de la actualización documental:** `48cf588` — `test: cubrir cierre de ciclo desde obligaciones`.

La rama continúa separada de `main`; no se realizó merge.

## Bloque cerrado

Se integró el cierre de ciclo desde `ObligacionesPanel` mediante el botón `Cerrar ciclo`. El panel delega la regla en `ObligacionService`, usa el ciclo persistido de la obligación y refresca la UI.

Se agregaron pruebas para cierre multidivisa con valorización histórica y para ausencia de cotización histórica con fallo y rollback.

Commits:

- `a5e6a86` — `feat: permitir cerrar ciclo desde obligaciones`.
- `48cf588` — `test: cubrir cierre de ciclo desde obligaciones`.

## Validación

Suite general ejecutada por el usuario:

- `mvn test`: **744/744**.
- Failures: 0.
- Errors: 0.
- Skipped: 0.
- `BUILD SUCCESS`.
- Finalizada: **16/09/2026 18:47:51 -03:00**.
- Tiempo total: **10:32 min**.

Prueba específica inmediatamente anterior:

- `ObligacionesPanelTest`: **6/6**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`.
- Finalizada: **16/09/2026 18:22:54 -03:00**.

El usuario informó `git diff` vacío, `git diff --check` sin observaciones y `git status` limpio, con la copia local alineada con GitHub y Bitbucket.

## Decisiones vigentes

1. La deuda original conserva su moneda original.
2. La valorización de cierre usa una cotización histórica explícita.
3. La valorización no reemplaza la liquidación.
4. No se realizan conversiones implícitas.
5. El pago posterior es independiente de la valorización de cierre.
6. Los pagos parciales reducen proporcionalmente el crédito valorizado mientras disminuye el `saldoPendiente` original.
7. El cierre iniciado desde UI delega las reglas de negocio en el servicio y no recalcula fechas ni cotizaciones.
8. Una obligación multidivisa sin cotización histórica necesaria para el cierre provoca error y rollback.

## Pendientes reales

1. Definir completamente el flujo de obtención/registro de la valorización de cierre dentro de la aplicación.
2. Definir qué ocurre con una obligación multidivisa todavía no valorizada al cierre.
3. Completar persistencia/UI del flujo integral de cierre y pago multidivisa.
4. Revisar consumos extranjeros sobre crédito antes de disponer de valorización de cierre.
5. P2: eliminación de cuentas con historial, `Clock` y migraciones/versionado formal.
6. P3: financiación avanzada, UI específica de tarjetas, pasivos/patrimonio/análisis, entidades financieras y pulido de consola.

## Estabilización futura — previa al fast-forward a main

No implementar todavía. Antes de considerar `main` como versión estable se deberá diseñar e implementar:

- arranque automático de H2 desde Java;
- cierre limpio de H2;
- ocultar la salida técnica de consola;
- logging técnico a archivo;
- `JOptionPane` para fallos de conexión con la base y otros errores de arranque;
- no mostrar una ventana parcialmente inicializada si el arranque falla.

## Próximo paso

Reconstruir el estado desde GitHub antes de modificar código. Revisar servicios, repositorios y UI relacionados con cierre multidivisa y diseñar tests de la siguiente regla de negocio antes de implementarla.

## Regla de continuidad

Código actual → tests → commits → comparación con `main` → documentación → próximo paso. No modificar `main` automáticamente y no asumir resultados locales no informados.
