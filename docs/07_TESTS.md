# SOFP — Tests

## Última validación conocida

La última suite general informada antes de la tanda actual fue **505/505 en verde**, `BUILD SUCCESS`.

Ese resultado no se extiende automáticamente a los cambios posteriores.

## Cobertura de seguridad actual

La feature `feature/seguridad-aislamiento-datos` cubre autorización por propietario en cuentas, categorías, movimientos, operaciones financieras y posiciones.

La tanda actual agregó cobertura para:

- lectura propia por ID;
- rechazo de lectura de recursos ajenos;
- listado por perfil con verificación del propietario;
- cálculo de saldo y evolución contextualizados por usuario;
- altas de cuentas, categorías y movimientos con verificación de propietario;
- lectura de perfil propio frente a perfil ajeno;
- aislamiento de posición y cartera por usuario.

Test agregado: `AislamientoDatosServiceTest`.

## Estado de validación

Los cambios actuales todavía deben ejecutarse localmente. No se registra como pasada ninguna suite posterior a `a28f9027` hasta recibir un resultado real de ejecución.

## Regla de cierre

No registrar resultados no ejecutados. La etapa de seguridad se considerará validada cuando los tests específicos y la suite general posterior a los cambios estén en verde.
