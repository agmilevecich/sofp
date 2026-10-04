# SOFP — Pendientes y próximos pasos

## Estado al 04/10/2026

Rama: `feature/swing-shell`  
`main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`  
Comparación: **320 ahead / 0 behind**  
No se realizó merge a `main`.

Última suite completa conocida: **979/979**, 0 failures, 0 errors, 0 skipped, `BUILD SUCCESS`, finalizada 03/10/2026 22:54:12 -03:00.

## Bloques cerrados

- Shell Swing y navegación.
- Cuentas, categorías, movimientos e inversiones.
- Tarjetas de crédito.
- Obligaciones, cuotas, liquidaciones, financiación y refinanciación auditadas.
- Aislamiento multiusuario.
- Patrimonio financiero.
- Resultado financiero.
- Reportes que combinan patrimonio y resultado.
- Valorización histórica multidivisa.
- Corrección del cálculo de crédito utilizado para evitar duplicación de capital financiado en obligaciones con cuotas.

## Auditoría de pasivos — cerrada

Se validó el recorrido desde obligaciones hasta patrimonio:

- obligación sin refinanciación;
- obligación con cuotas;
- financiación completa;
- financiación parcial;
- cargos financieros;
- pago de cargos y capital;
- liquidación;
- liquidación parcialmente financiada;
- pago completo de liquidación;
- refinanciación activa;
- financiación multidivisa con cotización histórica;
- aislamiento del pasivo en el patrimonio.

La suite completa posterior quedó en **979/979**.

No queda un defecto conocido pendiente en este bloque.

## Pendientes funcionales

Estos puntos requieren una decisión de negocio o una evolución separada; no deben implementarse por inferencia:

1. Modalidades adicionales de financiación/refinanciación que no estén definidas explícitamente.
2. Reglas financieras de conversión multidivisa que todavía no estén modeladas.
3. Evolución de la UI avanzada de financiación/refinanciación.
4. Calendario bancario explícito para feriados.
5. Integración de una abstracción `Clock` para fechas dependientes del reloj.
6. Migraciones/versionado formal del esquema.
7. Estabilización del ciclo de vida de H2 al arrancar y cerrar la aplicación.

## Mejoras técnicas futuras

- Optimización de consultas de movimientos en repositorio cuando aporte valor.
- Revisar APIs de compatibilidad antiguas que todavía existan por evolución histórica.
- No mezclar estas mejoras con reglas contables.

## Próximo paso

Antes de comenzar otro bloque:

1. reconstruir estado desde GitHub;
2. comparar `feature/swing-shell` con `main`;
3. revisar código y tests actuales;
4. identificar una funcionalidad concreta pendiente;
5. definir la regla de negocio si es material;
6. hacer el cambio mínimo;
7. validar tests específicos, relacionados y suite completa cuando corresponda.

## Regla de cierre

Tests específicos → relacionados → suite completa → `git diff` → `git diff --check` → `git status` → documentación.
