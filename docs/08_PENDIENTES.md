# SOFP — Pendientes

## Estado — 10/09/2026

**Rama estable:** `main` → `a4be85913847200cb70976d5266d9cbba10b3100`.
**Rama de trabajo:** `feature/swing-shell` → `46290786ba68bc5c7c1affadfa733a0b821f0291`.

La comparación verificada en GitHub indica **442 commits adelante y 0 atrás** respecto de `main`. No se realizó merge a `main`.

## Último bloque cerrado

### Crédito disponible y aislamiento de tests de tarjetas

**Completado y validado.**

Se implementó el criterio inicial de crédito disponible para tarjetas:

`crédito disponible = límite de crédito − consumos de tarjeta pendientes en la moneda de la tarjeta`

No se realizan conversiones implícitas entre monedas.

Se incorporó:

- cálculo de crédito disponible en `Cuenta`;
- exposición del crédito disponible mediante `CuentaService` con autorización y filtrado por moneda;
- validación de límite al registrar consumos con `FormaPago.TARJETA_CREDITO`;
- validación al modificar importe o tipo de movimiento;
- exclusión de consumos con tarjeta de crédito del saldo monetario de la cuenta;
- conservación de consumos de tarjeta sin obligación al calcular el crédito, evitando doble contabilización;
- pruebas para consumo parcial, límite exacto, exceso de límite, moneda diferente y liberación de crédito mediante pagos.

Además se corrigió el aislamiento del contexto JPA de los tests: `PosicionActivoServiceTest` ahora cierra su `EntityManagerFactory` mediante `@AfterEach`, evitando que otro test reutilice accidentalmente su contexto H2.

Commits principales del bloque:

- `923e6bf` — `feat: calcular credito disponible de tarjeta`
- `6c4049d` — `feat: exponer credito disponible de tarjeta`
- `9f9925c` — `feat: validar limite de tarjeta en consumos`
- `4a57efe` — `test: cubrir credito disponible de tarjeta`
- `fab7fd1` — `test: cubrir limite y credito disponible de tarjeta`
- `63dde05` — `fix: restaurar rollback original en CuentaService`
- `f53de09` — `fix: conservar consumos sin obligacion al calcular credito`
- `e8f6fdb` — `fix: aislar contexto JPA entre hilos de test`
- `46290786` — `fix: cerrar contexto JPA de PosicionActivoServiceTest`

## Validación actual

### Suite general

Ejecutada e informada por el usuario el **09/09/2026 21:58:35 -03:00**:

- `mvn test`;
- **664/664**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`;
- duración **10:04 min**.

La suite completa valida el estado actual después de la corrección del aislamiento JPA/H2.

### Estado Git local informado por el usuario

Después de `git syncsofp`:

- rama `feature/swing-shell` sincronizada con `github/feature/swing-shell`;
- `git diff` sin cambios versionados;
- `git diff --check` sin salida;
- quedó únicamente `surefire-debug.txt` como archivo no rastreado, generado durante el diagnóstico del fallo anterior.

` surefire-debug.txt` es un artefacto de diagnóstico local y **no forma parte del proyecto ni debe agregarse al repositorio**.

## Pendiente inmediato

### 1. Ciclos de facturación y vencimientos de tarjetas

**Siguiente bloque funcional.**

Antes de construir la pantalla Swing específica de tarjetas, continuar con las reglas financieras del dominio. Revisar primero `Cuenta`, `Movimiento`, `Obligacion`, `CicloFacturacion` y servicios/tests relacionados.

El objetivo será definir correctamente el período de facturación, fecha de cierre y vencimiento, incluyendo los casos límite de meses con distinta cantidad de días.

### 2. Pagos y liberación de crédito

**Parcialmente cubierto; pendiente profundizar el modelo.**

Ya existe cobertura del efecto de pagos sobre obligaciones y crédito disponible. Falta verificar si el modelo requiere reglas adicionales para el ciclo de tarjeta y su relación con el crédito disponible.

### 3. Cuotas y financiación

**Pendiente.**

Deberá abordarse después de consolidar ciclos, vencimientos y pagos.

### 4. UI específica de tarjetas

**Pendiente.**

No avanzar todavía hasta cerrar las reglas financieras del dominio.

### 5. Ampliar pasivos y patrimonio neto

**Pendiente.**

Las obligaciones son una primera representación de pasivos. Falta evolucionar el modelo para obtener una visión más completa de pasivos y patrimonio neto.

### 6. Análisis y dashboard

**Pendiente.**

Evolucionar progresivamente resúmenes mensuales/históricos, distribución por categoría/tipo, evolución patrimonial, vencimientos y dashboard.

### 7. Pulido de consola

**Pendiente de baja prioridad.**

Limpiar la salida de consola de la aplicación sin eliminar la posibilidad de diagnóstico ni alterar innecesariamente la configuración de logging.

## Bloques ya cerrados

- Seguridad y aislamiento de datos.
- Categorías con movimientos.
- Fondos insuficientes y reglas de saldo.
- Primer corte funcional de Gastos.
- FormaPago.
- Modelo de obligaciones y pagos en dominio/persistencia/servicio.
- Autorización de pagos de obligaciones por usuario.
- UI Swing de obligaciones y pagos.
- Navegación de obligaciones desde el shell.
- Corrección de conservación de selección al refrescar obligaciones.
- Pulido visual inicial de Cuentas, Movimientos y Categorías.
- Compatibilidad de constructores de `MainFrame` sin `ObligacionService`.
- Formulario e integración de Ingresos.
- Navegación hacia Ingresos.
- Formulario e integración de Transferencias.
- Integración de Transferencias en `Main` y `MainFrame`.
- Navegación hacia Transferencias.
- Moneda explícita en movimientos.
- Conservación de moneda en obligaciones.
- Visualización de moneda en obligaciones.
- Crédito disponible de tarjetas.
- Validación de límite de tarjeta.
- Liberación de crédito mediante pagos.
- Aislamiento del contexto JPA/H2 en la suite de tests.
- Suite general actual: **664/664**.

## Integración

No hacer merge a `main` automáticamente.

No crear ramas nuevas salvo indicación explícita.

Antes de una eventual integración revisar commits, comparación con `main`, tests, `git diff`, `git diff --check`, `git status` y documentación.

## Continuidad

Ante una nueva sesión reconstruir el estado desde GitHub priorizando código → tests → commits → `main` → documentación.

La documentación debe reflejar el código actual y nunca declarar implementada una funcionalidad únicamente porque esté escrita aquí.
