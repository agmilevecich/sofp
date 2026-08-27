# Build 009 - Modelo de Movimientos

## Objetivo

Definir el modelo conceptual del motor de movimientos del SOFP.

Un movimiento representa un hecho económico que modifica el patrimonio o la composición del patrimonio de un perfil financiero.

El objetivo de este documento es establecer las reglas de diseño antes de comenzar la implementación de las entidades del módulo de movimientos.

---

# Principios de diseño

## 1. Todo cambio patrimonial se registra mediante un Movimiento

No existen modificaciones directas del saldo de una cuenta.

Toda alteración del patrimonio debe quedar registrada mediante un movimiento.

Esto garantiza:

- trazabilidad;
- auditoría;
- reconstrucción histórica;
- consistencia de los saldos.

---

## 2. Las cuentas no almacenan el saldo actual

La entidad Cuenta no posee un atributo saldo.

El saldo se obtiene a partir de los movimientos registrados.

De esta manera se evita mantener un saldo independiente del historial.

---

## 3. Movimiento representa el hecho económico

Movimiento no describe una inversión.

Movimiento no describe un bono.

Movimiento no describe un fondo común.

Movimiento representa únicamente el hecho económico.

Los detalles específicos serán responsabilidad de otras entidades especializadas.

---

# Modelo conceptual

Perfil Financiero

↓

Cuenta

↓

Movimiento

↓

Categoría

En versiones futuras el movimiento podrá relacionarse además con:

- inversiones;
- transferencias;
- impuestos;
- tarjetas;
- préstamos;
- presupuestos.

---

# Datos principales del Movimiento

Inicialmente un movimiento estará compuesto por:

- fecha y hora;
- descripción;
- importe;
- tipo de movimiento;
- categoría;
- cuenta;
- observaciones;
- estado.

La implementación definitiva podrá ampliarse conforme evolucione el sistema.

---

# Tipos de Movimiento

El diseño conceptual original contemplaba tres tipos principales:

- INGRESO
- EGRESO
- TRANSFERENCIA

En el modelo implementado actualmente, una transferencia no es un tercer `TipoMovimiento`: se representa mediante una `OperacionFinanciera` que agrupa un `EGRESO` en la cuenta origen y un `INGRESO` en la cuenta destino.

Las categorías permiten representar situaciones específicas como:

- sueldo;
- supermercado;
- alquiler;
- combustible;
- dividendos;
- compra de bonos;
- rescate de fondos;
- intereses;
- comisiones.

---

# Relaciones

Cada Movimiento pertenece a:

- una Cuenta;
- una Categoría;
- un Perfil Financiero (de forma indirecta mediante la Cuenta).

Además, un Movimiento puede pertenecer a una `OperacionFinanciera`. En el estado actual, una operación puede agrupar como máximo dos movimientos.

---

# Principios de implementación

Las entidades del dominio deberán cumplir las siguientes reglas:

- sin setters públicos;
- comportamiento orientado al dominio;
- constructores de dominio;
- constructor protegido para JPA;
- validaciones centralizadas;
- pruebas unitarias obligatorias;
- pruebas de persistencia obligatorias.

---

# Evolución prevista

El motor de movimientos será la base para futuras funcionalidades del SOFP:

- inversiones;
- fondos comunes de inversión;
- bonos;
- acciones;
- CEDEARs;
- obligaciones negociables;
- criptomonedas;
- presupuestos;
- reportes;
- patrimonio;
- flujo de caja;
- balances;
- indicadores financieros.

Por este motivo el diseño prioriza simplicidad, trazabilidad y extensibilidad.

---

## Estado histórico

Este documento corresponde al diseño conceptual inicial del Build 009. La implementación posterior evolucionó el modelo: actualmente `Movimiento` está implementado como entidad persistente, utiliza `INGRESO` y `EGRESO`, y puede asociarse a `OperacionFinanciera`.

Para el estado técnico actual deben tomarse como referencia `docs/00-arquitectura/dominio.md`, `docs/00-arquitectura/modelo-jpa.md`, `docs/06_BUILDS.md` y `docs/07_TESTS.md`.
