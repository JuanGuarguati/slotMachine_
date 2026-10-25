# Slot Machine (DOPO 2026-2)

Simulador de la máquina tragamonedas del proyecto inicial. Autores: Juan David Guarguati Guarguati y Juan Pablo Suárez Rubiano.

## Abrir en VS Code
1. Instalar un JDK 17 o superior y la extensión **Extension Pack for Java**.
2. *File > Open Folder* y elegir esta carpeta. VS Code reconoce el `pom.xml` solo.

## Ejecutar
| Qué | Cómo |
|---|---|
| Pruebas de unidad | Panel **Testing** (matraz) o terminal: `mvn test` |
| Prueba de aceptación | Panel **Testing** > `SlotMachineAcceptanceTest` > Run. Mirar la pantalla y responder **Sí** o **No**. |
| Informe JaCoCo | `mvn clean test` → `target/site/jacoco/index.html` |
| Informe PMD | `mvn pmd:pmd` → `target/reports/pmd.html` |

`SlotMachineContestCTest` llama a `simulate(3)`: se abre el lienzo y sale el aviso de JACKPOT, que hay que cerrar con **Aceptar**.

## Carpetas
- `src/main/java`: el simulador (y el paquete `shapes`).
- `src/test/java`: pruebas `SlotMachineC1Test`, `C2Test`, `C3Test`, `C4Test`, `CC2Test`, `CC4Test`, `ContestTest`, `ContestCTest` y `AcceptanceTest`.
- `informes/`: informes de análisis dinámico (JaCoCo) y estático (PMD), con los resultados inicial y final.
- `retrospectiva.md`: retrospectiva de todos los ciclos.
- `slotMachine_new.asta`: diseño en Astah.
