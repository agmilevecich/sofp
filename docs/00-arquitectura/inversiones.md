# Modelo de inversiones

## Objetivo

Definir la base conceptual para registrar operaciones de inversión sin incorporar todavía posiciones calculadas ni atributos financieros específicos de cada tipo de activo.

## Conceptos

### Activo

Representa el instrumento financiero sobre el que existe una tenencia. Actualmente `Activo` contiene solamente identidad (`nombre`) y `Moneda`. Las especializaciones concretas se incorporan cuando el dominio las necesita. `Bono` es la primera especialización implementada.

### Operación financiera

Representa el hecho de negocio que coordina los efectos monetarios de una operación. Actualmente `OperacionFinanciera` trabaja con movimientos asociados a cuentas y puede agrupar hasta dos movimientos.

### Movimiento

Representa un efecto monetario sobre una `Cuenta`. No representa una posición de un activo.

### Operación de inversión

Una operación de inversión vincula un `Activo` con un efecto monetario. Como concepto de negocio, una compra aumenta la tenencia del activo y genera un efecto monetario de salida; una venta disminuye la tenencia y genera un efecto monetario de entrada.

## Regla arquitectónica

La cantidad o tenencia de un activo no pertenece a `Activo`, porque el mismo instrumento puede estar mantenido en distintas cantidades por distintos contextos financieros. Tampoco pertenece a `OperacionFinanciera`, porque una operación representa un hecho puntual y una posición representa un estado acumulado.

Por lo tanto, la posición de un activo será una consecuencia de las operaciones de inversión y no un atributo de `Activo`.

## Evolución prevista

La arquitectura podrá evolucionar hacia movimientos específicos de activos, por ejemplo `MovimientoActivo`, asociados a operaciones de inversión. Estos movimientos podrán registrar la variación de la tenencia producida por cada operación.

En esta etapa no se implementa todavía `MovimientoActivo` ni una entidad `Posicion`. Primero se establece el modelo conceptual para evitar fijar prematuramente una estructura de persistencia que después deba modificarse.

## Flujo conceptual

```text
OperacionFinanciera
        |
        +---- Movimiento ----------------> Cuenta
        |
        +---- efecto sobre Activo
                    |
                    v
             futura tenencia
```

## Compra

Una compra representa una incorporación de cantidad de un `Activo` y un efecto monetario de salida desde una cuenta.

```text
Compra
  Activo: + cantidad
  Cuenta: - importe
```

## Venta

Una venta representa una disminución de cantidad de un `Activo` y un efecto monetario de entrada hacia una cuenta.

```text
Venta
  Activo: - cantidad
  Cuenta: + importe
```

## Decisiones pendientes

Todavía deben definirse explícitamente:

- la representación exacta de la cantidad de activo;
- la precisión y escala de dicha cantidad;
- si el precio unitario y el importe total forman parte del movimiento de activo;
- cómo se representa la compra y la venta en el modelo de dominio;
- cómo se calcula la posición acumulada;
- tratamiento de comisiones, impuestos y otros costos;
- operaciones que no sean compra o venta.

Estas decisiones deben resolverse antes de implementar una entidad de posición o movimientos específicos de activos.
