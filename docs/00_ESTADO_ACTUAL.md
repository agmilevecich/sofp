# SOFP — Estado actual

## 04/10/2026

### Git

- Rama de trabajo: `feature/swing-shell`
- `main`: `a23d3a5c0658ffbca93391c34f79ad8bc37fdc10`
- Feature vs main: **320 ahead / 0 behind**
- Sin merge a `main`
- Último commit validado de la etapa: `5a8cb25be5f15c776007b136f243e5e01f2f6bb1`

### Suite

Última ejecución informada por el usuario:

- `mvn test`
- **979/979**
- Failures 0
- Errors 0
- Skipped 0
- `BUILD SUCCESS`
- 03/10/2026 22:54:12 -03:00
- 24:47 min

### Bloques funcionales auditados recientemente

- Tarjetas de crédito.
- Multiusuario y aislamiento de recursos.
- Resultado financiero.
- Patrimonio financiero y valorización.
- Pasivos: obligaciones, cuotas, financiación, cargos, liquidaciones y refinanciaciones.

### Cierre de pasivos

El cálculo de crédito utilizado fue auditado porque una obligación con cuotas y financiación podía contar dos veces el capital financiado. Se corrigió únicamente la consulta de `ObligacionRepository`.

Validación posterior:

- `ObligacionRepositoryTest`: 17/17.
- `PatrimonioFinancieroServiceTest`: 13/13.
- `ObligacionServiceTest + RefinanciacionTest`: 36/36.
- Suite completa: 979/979.

No se realizó ningún cambio en `main`.

### Regla de fuente de verdad

Código actual → tests → commits → comparación con `main` → documentación → conversaciones históricas.

Si documentación y código contradicen, prevalecen código y tests.
