# SOFP — Contexto de continuidad

## Snapshot operativo — 04/10/2026

SOFP es una aplicación Java Swing de finanzas personales con JPA/Hibernate, H2, Maven y JUnit 5.

### Git

- Trabajo: `feature/swing-shell`
- Estable: `main`
- main: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`
- feature: **320 commits ahead / 0 behind**
- No merge a main.

### Última validación

`mvn test`:

**979/979 — 0 failures — 0 errors — 0 skipped — BUILD SUCCESS**

03/10/2026 22:54:12 -03:00, 24:47 min.

### Último bloque trabajado

Auditoría de pasivos y patrimonio neto.

Se detectó una duplicación en `ObligacionRepository.sumarCreditoUtilizadoPorCuenta()`: cuando una obligación tenía cuotas y una financiación normal, el capital financiado podía quedar contado tanto dentro de las cuotas como dentro de las financiaciones.

Se agregó la cobertura correspondiente y se corrigió la consulta para descontar de `cuotas` el capital pendiente de financiaciones normales sobre obligaciones con cuotas.

### Validación

- Repositorio: 17/17.
- Patrimonio: 13/13.
- Obligaciones + refinanciación: 36/36.
- Caso específico de liquidación financiada: 1/1.
- Suite completa: 979/979.

La financiación sobre liquidación fue validada además en un escenario multidivisa. El diagnóstico inicial del test reveló que el fixture financiaba 60,00 y no 60.000, por lo que se corrigieron las expectativas del test; no era un defecto de JPA ni del repositorio.

### Próximo punto

La auditoría de pasivos está cerrada. Antes de iniciar una nueva funcionalidad hay que reconstruir el estado actual desde GitHub y elegir el siguiente bloque a partir del código y tests reales.

No asumir ningún resultado histórico.
