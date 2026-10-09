# Retrospectiva del Proyecto Inicial: Slot Machine (Ciclos 1 a 4)

Autores: Juan David Guarguati Guarguati y Juan Pablo Suárez Rubiano

> Los campos marcados con **[COMPLETAR]** solo los pueden llenar ustedes (horas reales). Revisen también que los mini-ciclos de los ciclos 1 a 3 coincidan con los que entregaron en esos ciclos.

---

## 1. ¿Cuáles fueron los mini-ciclos definidos? Justifíquenlos.
Los mini-ciclos se definieron por **requisito funcional**. Cada uno termina con código que compila y con pruebas que pasan, así que siempre hubo una versión funcionando para mostrar.

### Ciclo 1: simulador básico
| Mini-ciclo | Contenido | Justificación |
|---|---|---|
| 1.1 | Crear la máquina, `Symbol` y `Wheel` (Req. 1) | Es la base de todo lo demás. |
| 1.2 | Adicionar y eliminar ruedas y símbolos (Req. 2 y 3) | Sin ruedas y símbolos no hay nada que girar. |
| 1.3 | Girar y consultar (Req. 4 y 5) | Es el comportamiento principal del simulador. |
| 1.4 | Jackpot (Req. 6) | Depende de poder consultar los símbolos. |
| 1.5 | Visibilidad, terminar y mensajes de error (Req. 7 y 8, usabilidad) | Se puede trabajar invisible y luego añadir el dibujo. |

### Ciclo 2: nuevas operaciones
| Mini-ciclo | Contenido | Justificación |
|---|---|---|
| 2.1 | `swap` (Req. 9) | Es independiente de los demás. |
| 2.2 | `lock` / `unlock` (Req. 10) | Los giros siguientes deben respetar el bloqueo. |
| 2.3 | `spin(wheel)` y `spin(wheel, steps)` (Req. 11) | Usan el bloqueo del mini-ciclo anterior. |
| 2.4 | `spin(String[])`, `placeSymbol`, `symbols`, `distinctSymbols` (Req. 12) | Consultas y colocación directa. |
| 2.5 | Pruebas `SlotMachineC2Test` y `SlotMachineCC2Test` | Para cerrar el ciclo con pruebas. |

### Ciclo 3: resolver la maratón
| Mini-ciclo | Contenido | Justificación |
|---|---|---|
| 3.1 | `SlotMachine(n)` aleatoria, con n entre 3 y 50 (Req. 13) | Es el problema a resolver. |
| 3.2 | `solve(n)` usando solo `SlotMachine(n)`, `spin(wheel, steps)` y `distinctSymbols()` (Req. 14) | Es la restricción de diseño del ciclo. |
| 3.3 | `simulate(n)` con `makeVisible()` (Req. 15) | Hace lo mismo que `solve`, pero a la vista. |
| 3.4 | Paleta de 50 colores, marco de jackpot, pruebas | Para que n = 50 se vea y se pruebe. |
| 3.5 | Corrección: el Contest ya no usa `ok()`; valida n con `MIN_N`/`MAX_N`; toda la lógica queda dentro de `solve` y `simulate` | `ok()` no estaba entre los métodos permitidos, y así cada método se lee de principio a fin. |

### Ciclo 4: refactorización y extensión
| Mini-ciclo | Contenido | Justificación |
|---|---|---|
| 4.1 | **Refactorizar:** `Wheel` y `Symbol` abstractas, `NormalWheel` y `NormalSymbol`, fábricas `create` | Sin cambiar el comportamiento, comprobado con las pruebas de los ciclos 2 y 3 antes de extender. |
| 4.2 | `addWheel(type, pos)`, `addSymbol(type, pos, color)` y símbolos registrados en la máquina (Req. 16) | Todo lo demás se crea con estos métodos; y las pruebas compartidas agregan símbolos antes que ruedas. |
| 4.3 | Ruedas `lefty` y `rebel` (Req. 17); `swap` con toda su lógica adentro | Cada tipo es una subclase que redefine un solo punto. `swap` perdía los tipos. |
| 4.4 | Símbolos `ephemeral` y `shy` (Req. 18) | Igual, con los ganchos `spun()` y `selected()`. |
| 4.5 | Rueda propuesta `reverse` (Req. 19) | Muestra que extender ya no cuesta: una clase y una línea en la fábrica. |
| 4.6 | Figuras y marcas por tipo (Usabilidad 1) | Cada tipo se ve distinto. |
| 4.7 | Pruebas C4, CC4 y de aceptación, más la documentación y el Astah | Para cerrar el ciclo con pruebas y diseño. |

## 2. ¿Cuál es el estado actual del proyecto en términos de mini-ciclos? ¿Por qué?
| Ciclo | Estado | Detalle |
|---|---|---|
| 1 | Terminado (1.1 a 1.5) | Todos los requisitos funcionan, en modo visible e invisible. |
| 2 | Terminado (2.1 a 2.5) | Las 14 pruebas de C2 y CC2 siguen pasando en el Ciclo 4 sin cambios. |
| 3 | Terminado (3.1 a 3.5) | `solve` es determinístico, usa a lo sumo un spin por rueda y funciona hasta n = 50. |
| 4 | Código terminado (4.1 a 4.6); 4.7 [COMPLETAR: Astah actualizado / pendiente] | Pasan las 73 pruebas de unidad del proyecto, y las 2 de aceptación se ejecutaron completas. 4 pruebas compartidas de otros grupos quedaron comentadas porque usan clases internas incompatibles con nuestro diseño (el motivo está en `SlotMachineCC4Test`). |

## 3. ¿Cuál fue el tiempo total invertido por cada uno de ustedes? (Horas/Hombre)
| Ciclo | Juan David Guarguati | Juan Pablo Suárez |
|---|---|---|
| 1 | [COMPLETAR] | [COMPLETAR] |
| 2 | [COMPLETAR] | [COMPLETAR] |
| 3 | [COMPLETAR] | [COMPLETAR] |
| 4 | [COMPLETAR] | [COMPLETAR] |
| **Total** | [COMPLETAR] | [COMPLETAR] |

## 4. ¿Cuál consideran fue el mayor logro? ¿Por qué?
- **En el proyecto:** resolver el problema de la maratón "a ciegas" (Ciclo 3). Se usa solo `distinctSymbols()` como medida, y el resultado es determinístico, con un solo spin por rueda.
- **En el Ciclo 4:** reorganizar el proyecto con herencia sin dañar lo que ya funcionaba. `SlotMachine` ya no conoce las clases concretas, así que un tipo nuevo (por ejemplo la rueda `reverse`) se agrega con una clase y una línea en la fábrica, sin tocar la máquina. Esto cumple el requisito de extensibilidad.

## 5. ¿Cuál consideran que fue el mayor problema técnico? ¿Qué hicieron para resolverlo?
- **Ciclo 3:** resolver sin leer los colores. Se resolvió comparando la suma de `distinctSymbols()` en vueltas completas: la diferencia correcta da la suma mínima.
- **Ciclo 4, qué es un "giro":** el enunciado no lo dice, y de eso dependen la lefty, el ephemeral y el shy. Se definió que giran `spin()`, `spin(wheel)` y `spin(wheel, steps)`, mientras que `placeSymbol` y `spin(String[])` solo seleccionan. Los efectos del giro quedaron en un solo método, `finishSpin`.
- **Ciclo 4, pasar las pruebas compartidas:** otros grupos agregan símbolos antes que ruedas, esperan que un shy escondido no cuente para el jackpot y que la lefty copie todo el estado de su vecina. Se resolvió guardando los símbolos registrados en la máquina, copiándolos a cada rueda nueva y haciendo que la lefty copie también si el shy está escondido.
- **Ciclo 4, `swap`:** la versión anterior copiaba los colores de una rueda a otra y creaba símbolos nuevos, así que se perdían los tipos. Ahora `swap` intercambia las ruedas completas, con toda la lógica dentro del método.

## 6. ¿Qué hicieron bien como equipo? ¿Qué se comprometen a hacer para mejorar los resultados?
Mantuvimos una buena comunicación y nos repartimos las tareas de forma ordenada; luego las integramos al proyecto entre los dos.

Nos comprometemos a actualizar el diagrama de Astah al terminar cada mini-ciclo y no al final, para evitar complicaciones de última hora.

## 7. Considerando las prácticas XP incluidas en los laboratorios, ¿cuál fue la más útil? ¿Por qué?
La **refactorización apoyada en pruebas**. Antes de agregar los tipos nuevos cambiamos la estructura de `Wheel` y `Symbol`, y las pruebas de los ciclos anteriores nos confirmaron que todo seguía funcionando igual. Así pudimos extender el proyecto con confianza. También ayudó el **diseño simple**: cada tipo redefine solo lo que cambia.

## 8. ¿Qué referencias usaron? ¿Cuál fue la más útil?
- Barnes, D. J., & Kölling, M. (2016). *Objects First with Java: A Practical Introduction Using BlueJ* (6th ed.). Pearson. (Proyecto `shapes`.)
- Beck, K. (2000). *Extreme Programming Explained: Embrace Change*. Addison-Wesley.
- Fowler, M. (2018). *Refactoring: Improving the Design of Existing Code* (2nd ed.). Addison-Wesley.
- Oracle. (s.f.). *Abstract Methods and Classes (The Java Tutorials)*. https://docs.oracle.com/javase/tutorial/java/IandI/abstract.html
- Oracle. (s.f.). *Inheritance (The Java Tutorials)*. https://docs.oracle.com/javase/tutorial/java/IandI/subclasses.html
- Oracle. (s.f.). *How to Write Doc Comments for the Javadoc Tool*. https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
- ICPC. (2025). *Problem I: Slot Machine*. ICPC World Finals.
- Anthropic. (2026). *Claude*. Usado para revisar el diseño, implementar y verificar las pruebas. https://claude.ai

La más útil fue la guía de Oracle sobre clases abstractas, porque es la base de la refactorización de este ciclo.
