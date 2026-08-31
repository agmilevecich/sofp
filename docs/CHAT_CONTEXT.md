# SOFP — Contexto para continuar con ChatGPT

Este archivo es el punto de entrada para continuar SOFP en una nueva conversación. La fuente de verdad es el código, Git y los tests actuales; `docs/` es documentación auxiliar.

## Proyecto

SOFP — Sistema Operativo Financiero Personal.

Aplicación Java de finanzas personales con dominio, persistencia JPA, servicios y tests automatizados.

## Estado actual — 31/08/2026

La rama estable es `main`. La rama de trabajo actual es `feature/seguridad-aislamiento-datos`.

Estado verificado contra GitHub:

- rama de trabajo: `feature/seguridad-aislamiento-datos`;
- último commit de código/tests antes de la actualización documental: `b0e6377` — `test: cubrir aislamiento de datos por usuario`;
- commit funcional previo: `85a4e86` — `fix: autorizar operaciones financieras por usuario`;
- la rama está sincronizada con `github/feature/seguridad-aislamiento-datos` y `bitbucket/feature/seguridad-aislamiento-datos`;
- `main` no fue modificado durante esta feature.

## Validación global vigente

Última suite completa informada por Ariel el **31/08/2026**:

- **505/505 tests en verde**;
- Failures: **0**;
- Errors: **0**;
- Skipped: **0**;
- `BUILD SUCCESS`.

## Seguridad y aislamiento — estado de la feature

La auditoría exploratoria transversal fue finalizada y sus hallazgos están siendo corregidos en esta rama.

Correcciones implementadas hasta ahora:

- `CuentaService`: autorización por propietario para operaciones mutables relevantes;
- `CategoriaService`: autorización por propietario para operaciones mutables relevantes;
- `MovimientoService`: autorización por propietario para operaciones mutables relevantes;
- `PosicionActivoService`: aislamiento de posición por `PerfilFinanciero`;
- `MovimientoActivoRepository`: consultas filtradas por perfil para soportar el aislamiento de posiciones;
- `OperacionFinancieraService`: autorización explícita del usuario solicitante en las operaciones protegidas.

Tests adaptados/cubiertos recientemente:

- `OperacionFinancieraCompraServiceTest`;
- `OperacionFinancieraServiceTest`;
- `OperacionFinancieraVentaServiceTest`;
- `PosicionActivoServiceTest`.

El commit `b0e6377` registra esta cobertura de aislamiento por usuario.

## Hallazgos pendientes

La seguridad transversal todavía no está cerrada. Quedan por resolver/verificar:

1. aislamiento de lecturas por ID y listados cuando representen casos de uso accesibles al usuario;
2. caminos alternativos de creación de movimientos que puedan eludir las reglas del servicio;
3. tests específicos restantes de lectura de recursos ajenos y autorización;
4. casos límite de activos compartidos entre perfiles.

## Git y continuidad

Se mantienen dos remotos (`github` y `bitbucket`) como referencia y recuperación ante errores accidentales.

`main` es estable. Las nuevas funcionalidades/correcciones se desarrollan en ramas propias. No modificar `main` directamente durante una feature.

Antes de cualquier nuevo cambio:

1. reconstruir el estado desde GitHub;
2. revisar rama y últimos commits;
3. comparar con `main`;
4. revisar código, tests y reglas de negocio relacionadas;
5. seleccionar el cambio mínimo;
6. ejecutar tests específicos y suite general cuando corresponda;
7. revisar `git diff`, `git diff --check` y `git status`;
8. actualizar documentación al cerrar etapas importantes.

## Próximo paso

Continuar `feature/seguridad-aislamiento-datos` revisando las lecturas por ID y listados para garantizar el aislamiento entre perfiles. Luego revisar caminos alternativos de creación de movimientos y completar la cobertura pendiente.

No comenzar todavía la implementación Swing hasta cerrar la seguridad transversal y realizar la validación final contra `main`.

## Documentación de continuidad

- `docs/00_ESTADO_ACTUAL.md`;
- `docs/06_BUILDS.md`;
- `docs/07_TESTS.md`;
- `docs/08_PENDIENTES.md`;
- `docs/09_HISTORIAL_PROYECTO.md`;
- `docs/10_SEGURIDAD_PERFIL_FINANCIERO.md`;
- `docs/11_AUDITORIA_SEGURIDAD.md`;
- `docs/CHAT_CONTEXT.md`.

No registrar resultados de tests o builds que no hayan sido realmente ejecutados.
