# Principios de Arquitectura - SOFP

## Objetivo

Este documento define las reglas arquitectónicas del proyecto SOFP. Toda nueva funcionalidad deberá respetar estos principios.

---

## 1. Dominio primero

Las entidades representan el dominio del negocio y no únicamente tablas de base de datos.

El paquete principal será:

domain

No se utilizará un paquete `entity`.

---

## 2. Entidades ricas

Las entidades contienen comportamiento.

Se evita el uso de setters públicos.

Los cambios de estado deben realizarse mediante métodos del dominio.

Ejemplos:

activar()

desactivar()

renombrar()

cambiarDescripcion()

---

## 3. Persistencia

Todas las entidades deberán tener:

- constructor protegido para JPA
- constructor de dominio

---

## 4. Validaciones

Las validaciones pertenecen al dominio.

Se utilizará:

Objects.requireNonNull()

hasta definir un mecanismo común.

---

## 5. Dinero

Nunca se utilizarán:

double

float

Todos los importes monetarios serán representados mediante:

BigDecimal

---

## 6. Saldos

La entidad Cuenta no almacenará:

- saldo
- saldoInicial

El saldo será calculado exclusivamente a partir de los movimientos registrados.

Cuando una cuenta se incorpore con un saldo previo, el sistema generará un Movimiento de tipo SALDO_INICIAL.

---

## 7. Movimientos

Toda modificación patrimonial deberá quedar registrada mediante un Movimiento.

No existirán excepciones.

---

## 8. Fechas

Se utilizará exclusivamente la API java.time.

No se utilizarán Date ni Calendar.

---

## 9. Pruebas

Toda entidad del dominio deberá contar con:

- pruebas unitarias
- pruebas de persistencia cuando corresponda

---

## 10. Git

Todo Build deberá finalizar con:

- código compilando
- tests en verde
- commit realizado
- documentación actualizada