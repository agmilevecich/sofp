# Diagrama de dominio

El siguiente diagrama representa el modelo implementado actualmente. Las entidades futuras de movimientos específicos de activos no se muestran como parte del modelo actual.

```mermaid
classDiagram
    class EntidadAuditable {
        <<abstract>>
        fechaCreacion
        fechaActualizacion
        creadoPor
        actualizadoPor
    }

    class Usuario
    class PerfilFinanciero
    class Cuenta
    class Moneda
    class Categoria
    class Movimiento
    class OperacionFinanciera

    class Activo {
        <<abstract>>
    }
    class FondoComun
    class Bono
    class Accion
    class Criptomoneda
    class PlazoFijo
    class Divisa

    Usuario "1" --> "0..*" PerfilFinanciero : posee
    PerfilFinanciero "1" *-- "0..*" Cuenta : administra
    PerfilFinanciero "1" *-- "0..*" Categoria : define
    PerfilFinanciero "1" *-- "0..*" Activo : registra
    PerfilFinanciero "1" *-- "0..*" OperacionFinanciera : registra

    Cuenta "1" --> "0..*" Movimiento : registra
    Categoria "1" --> "0..*" Movimiento : clasifica
    OperacionFinanciera "1" --> "0..2" Movimiento : agrupa

    Activo <|-- FondoComun
    Activo <|-- Bono
    Activo <|-- Accion
    Activo <|-- Criptomoneda
    Activo <|-- PlazoFijo
    Activo <|-- Divisa

    Cuenta --> Moneda : opera en

    Movimiento --> OperacionFinanciera : puede pertenecer a

    EntidadAuditable <|-- Usuario
    EntidadAuditable <|-- PerfilFinanciero
    EntidadAuditable <|-- Cuenta
    EntidadAuditable <|-- Categoria
    EntidadAuditable <|-- Moneda
    EntidadAuditable <|-- Movimiento
    EntidadAuditable <|-- Activo
    EntidadAuditable <|-- OperacionFinanciera
```

## Evolución futura

Cuando se incorporen posiciones de activos, podrá definirse un modelo específico para los movimientos que afecten dichas posiciones. Esa evolución deberá documentarse y validarse antes de considerarla parte del dominio implementado.
