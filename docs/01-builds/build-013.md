# Build 013 — Repository de Cuenta

## Objetivo

Incorporar la capa de persistencia para la entidad `Cuenta`.

## Implementación

Se creó:

- `CuentaRepository`
- `CuentaRepositoryTest`

El repository utiliza `EntityManager` y proporciona las siguientes operaciones:

- Guardar una cuenta nueva.
- Actualizar una cuenta existente.
- Buscar una cuenta por ID.
- Listar todas las cuentas.
- Listar cuentas pertenecientes a un `PerfilFinanciero`.

## Relaciones utilizadas

El test de persistencia verifica la integración de `Cuenta` con:

- `Usuario`
- `PerfilFinanciero`
- `InstitucionFinanciera`
- `Moneda`

La relación utilizada es:

```text
Usuario
   └── PerfilFinanciero
          └── Cuenta
                ├── InstitucionFinanciera
                └── Moneda