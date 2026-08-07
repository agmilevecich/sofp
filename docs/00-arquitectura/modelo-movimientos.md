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

El saldo siempre se obtiene a partir de:

Saldo inicial (si existiera)

+

Movimientos registrados

=

Saldo actual

De esta manera nunca existen inconsistencias entre el historial y el saldo mostrado al usuario.

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

El sistema utilizará inicialmente tres tipos principales:

- INGRESO
- EGRESO
- TRANSFERENCIA

Las categorías permitirán representar situaciones específicas como:

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

## Estado

Documento aprobado como base arquitectónica del módulo de movimientos.

La implementación comenzará en el Build 009.