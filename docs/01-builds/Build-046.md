# Build 046 — Implementación de OperacionFinancieraService

**Fecha:** 21/08/2026

## Objetivo

Implementar el servicio encargado de materializar una `OperacionFinanciera` como una transferencia entre dos cuentas, generando los movimientos correspondientes y coordinando la persistencia dentro de una única transacción.

## Cambios

Se incorporó `OperacionFinancieraService` en la rama `feature/operacion-financiera`.

El servicio:

- valida que las cuentas de origen y destino sean obligatorias;
- valida que las categorías de origen y destino sean obligatorias;
- valida fecha/hora obligatoria;
- permite descripción nula, de acuerdo con el contrato validado por los tests;
- rechaza cuentas desactivadas;
- valida que cada categoría pertenezca al mismo perfil financiero que su cuenta;
- valida que las cuentas de origen y destino utilicen la misma moneda;
- crea la `OperacionFinanciera`;
- crea un `EGRESO` en la cuenta origen;
- crea un `INGRESO` en la cuenta destino;
- persiste la operación y ambos movimientos dentro de una única transacción;
- realiza rollback ante una excepción durante la persistencia.

Commit inicial de código:

- `a995937` — `feat: implementar servicio de operacion financiera`

Corrección posterior:

- `2e4b94f` — `fix: permitir descripcion nula en transferencia`

La corrección elimina la validación que exigía una descripción no nula y deja que la transferencia pueda registrarse sin descripción.

## Validación

`OperacionFinancieraServiceTest` quedó en **20/20 tests en verde**.

La cobertura incluye:

- registro de una transferencia;
- creación del egreso en la cuenta origen;
- creación del ingreso en la cuenta destino;
- misma fecha/hora para ambos movimientos;
- parámetros nulos;
- importe nulo, cero y negativo;
- cuentas desactivadas;
- perfiles incompatibles;
- monedas diferentes;
- ausencia de persistencia ante operaciones rechazadas;
- descripción nula.

La suite general del proyecto fue ejecutada mediante Maven y quedó en:

- **Tests run: 300**
- **Failures: 0**
- **Errors: 0**
- **Skipped: 0**
- **BUILD SUCCESS**

La ejecución finalizó el **21/08/2026 a las 18:01:52 -03:00**, con una duración total de **09:44 min**.

## Estado

**Build 046 queda cerrado y validado.**

La implementación de `OperacionFinancieraService` y su corrección están publicadas en `feature/operacion-financiera`, tanto en GitHub como en Bitbucket.

La documentación se mantiene separada en `docs/continuidad-sofp` y el Build no se incorporó a `main`.

## Próximo paso

Continuar con el siguiente bloque funcional del proyecto, manteniendo el desarrollo en `feature/operacion-financiera` y registrando la continuidad en `docs/continuidad-sofp`.

No implementar todavía `OperacionFinancieraRepository` hasta confirmar si la persistencia de la operación requiere un repositorio independiente.
