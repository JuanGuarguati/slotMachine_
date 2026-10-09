# Sustentación Ciclo 4: SlotMachine

## Qué pide el ciclo y dónde está
| Requisito | Dónde está |
|---|---|
| 16. Diferentes tipos de ruedas y símbolos | `addWheel(type, pos)` y `addSymbol(type, pos, color)` en `SlotMachine` |
| 17. Ruedas normal, lefty, rebel | `NormalWheel`, `LeftyWheel`, `RebelWheel` |
| 18. Símbolos normal, ephemeral, shy | `NormalSymbol`, `EphemeralSymbol`, `ShySymbol` |
| 19. Tipo propuesto | `ReverseWheel` (gira hacia atrás) |
| Usabilidad 1 | Figura por tipo de símbolo y marca por tipo de rueda |
| Extensibilidad (implícito) | Clases abstractas `Wheel` y `Symbol` con fábrica `create` |

## Correcciones respecto a lo que teníamos
| Qué | Antes | Ahora | Por qué |
|---|---|---|---|
| `SlotMachineContest` (Ciclo 3) | Preguntaba `ok()` | Valida n con `MIN_N`/`MAX_N`; toda la lógica dentro de `solve` y `simulate` | `ok()` no estaba entre los métodos permitidos. |
| `swap` | `Wheel.swapContentWith` copiaba los colores y creaba cuadrados nuevos | Toda la lógica en `swap`: intercambia las ruedas completas | Se perdían los tipos de rueda y de símbolo. |
| `addSymbol` | Daba error si no había ruedas; permitía colores repetidos | Registra el símbolo aunque no haya ruedas; rechaza colores repetidos | Las pruebas compartidas agregan símbolos antes que ruedas. |
| `addWheel` | La rueda nueva nacía vacía | Nace con una copia de los símbolos registrados | Si no, una rueda agregada después no tiene símbolos. |
| `symbols()` | Leía la rueda 1 | Devuelve los símbolos registrados | Funciona sin ruedas. |
| `configuration()` | `"empty"` en una rueda vacía | `"white"` en una rueda vacía, `""` si el shy está escondido | Lo piden las pruebas compartidas. |
| `exit()` | `System.exit(0)` | Solo oculta el simulador | `System.exit` cerraba BlueJ y cortaba las pruebas. |
| `shapes` | `Rectangle`, `Circle` y `Triangle` repetían los mismos atributos y métodos | Clase abstracta `Shape` con lo común; las tres la heredan | Sugerencia del profesor: quita código repetido. |
| Javadoc | `3 <= n <= 50` | `entre 3 y 50` | El `<` rompía la generación de la documentación. |

---

## La idea del diseño (lo primero que hay que explicar)
- `Wheel` y `Symbol` son **abstractas**. Guardan lo común; cada tipo es una **subclase** que redefine solo lo que cambia (polimorfismo).
- `SlotMachine` solo conoce `Wheel` y `Symbol`. Crea los objetos con las fábricas `Wheel.create(type, x)` y `Symbol.create(type, color, x, y)`, que tienen un `switch` y devuelven `null` si el tipo no existe.
- **Extensibilidad:** para un tipo nuevo se crea una clase y se añade un `case` en la fábrica. `SlotMachine` no cambia. Así se agregó la rueda reverse.

- **También en `shapes`:** `Shape` es abstracta y tiene lo común de las figuras (posición, color, visibilidad, moverse, cambiar color, borrar). `Rectangle`, `Circle` y `Triangle` la heredan y solo redefinen `draw()` (cómo se dibuja cada una).

Ver [diagrama-clases-ciclo4.png](diagramas/diagrama-clases-ciclo4.png). En azul está lo nuevo o cambiado.

### Herencia en `shapes` (`Shape`)
- Antes las tres figuras tenían copiados `xPosition`, `yPosition`, `color`, `isVisible`, `makeVisible`, `moveHorizontal`, `changeColor`, etc. Ahora están una sola vez en `Shape`.
- `draw()` es **abstracto**: cada figura sabe dibujarse (rectángulo, círculo con `PI`, triángulo con 3 vértices). `Shape` llama a `draw()` desde `makeVisible`, `moveHorizontal`, `changeColor`... sin saber qué figura es (polimorfismo).
- **Ganancia en los símbolos:** `Symbol` guarda `protected Shape shape`. Así `shiftHorizontal`, `show` y `hide` se escriben una sola vez en `Symbol` (`shape.moveHorizontal(...)`, `shape.makeVisible()`, `shape.makeInvisible()`), y cada subclase solo crea su figura en el constructor.
- `EphemeralSymbol` guarda además `circle` (el mismo objeto) porque `changeSize(diameter)` solo existe en `Circle`.
- `ShySymbol` redefine `show()`: solo llama a `super.show()` si no está escondido.
- En `Canvas` hubo que escribir `java.awt.Shape`, porque ahora existe `shapes.Shape` con el mismo nombre.

---

## Funciones nuevas: cómo se implementan y su lógica

### 1. `addWheel(String type, int pos)`
Ver [secuencia-addWheel.png](diagramas/secuencia-addWheel.png).
1. `Wheel.create(type, 0)` crea la rueda del tipo pedido. Si da `null`, el tipo no existe: `notifyError` y `ok() = false`.
2. Recorre los símbolos registrados (`symbolTypes` y `symbolColors`) y le agrega a la rueda nueva **un símbolo nuevo de cada uno**, del mismo tipo y en el mismo orden. Son objetos nuevos, porque cada rueda dibuja sus propias figuras.
3. Inserta la rueda en `wheels` en la posición `pos` (ajustada con `adjustPos`).
4. Si la máquina está visible, la dibuja; luego `recalculateAxes()` acomoda los ejes.

`addWheel(int pos)` ahora solo llama a `addWheel("normal", pos)`.

### 2. `addSymbol(String type, int pos, String color)`
Ver [secuencia-addSymbol.png](diagramas/secuencia-addSymbol.png).
1. Valida el tipo con `Symbol.create(type, color, 0, 0)`: si da `null`, error.
2. Recorre `symbolColors`: si el color ya existe (sin importar mayúsculas), error. **No puede haber dos símbolos del mismo color.**
3. Registra el símbolo en `symbolTypes` y `symbolColors` en la posición `pos`.
4. Lo agrega a cada rueda **no bloqueada** con `wheel.addSymbol(type, pos, color)`.

Funciona aunque no haya ruedas: el símbolo queda registrado y las ruedas que se agreguen después lo reciben. `addSymbol(int pos, String color)` ahora llama a `addSymbol("normal", pos, color)`.

### 3. ¿Qué es un "giro"? (`finishSpin`, `afterSpin`, `select`)
Ver [secuencia-spin-steps.png](diagramas/secuencia-spin-steps.png) y [secuencia-spin.png](diagramas/secuencia-spin.png).
- **Son giros:** `spin()`, `spin(wheel)` y `spin(wheel, steps)` con `steps > 0`. Primero la rueda se mueve (`spin()` aleatorio o `rotate(steps)`), y al final `SlotMachine` llama a `w.finishSpin(left)`, donde `left` es la rueda de la izquierda (o `null` si es la primera).
- `finishSpin(left)` hace dos cosas:
  1. `spun()` a **todos** los símbolos de la rueda: el ephemeral se encoge.
  2. `afterSpin(left)`: en la rueda normal llama a `select()`, que le avisa `selected()` al símbolo visible (el shy alterna). La lefty lo redefine para copiar.
- **No son giros:** `placeSymbol` y `spin(String[])`, porque ponen un color exacto. Solo llaman a `select()`: el shy alterna, pero la lefty no copia y el ephemeral no se encoge.
- En `spin()` las ruedas giran **de izquierda a derecha**, para que la lefty copie a su vecina ya girada.

### 4. Rueda `lefty` (`LeftyWheel.afterSpin(left)`)
1. Toma el símbolo visible de la rueda de la izquierda.
2. Si existe y la lefty tiene ese color, se pone en ese color (`setVisibleColor`) y copia si está escondido (`setHidden(leftSymbol.isHidden())`). Así copia su **estado** completo, como dice el enunciado.
3. Si es la primera rueda, si la vecina está vacía o si no tiene ese color, se comporta normal (`super.afterSpin(left)`).

### 5. Rueda `rebel` (`RebelWheel`)
- Redefine `isLockable()`, `isSwappable()` e `isRemovable()` para que devuelvan `false`. En `Wheel` los tres devuelven `true`.
- `SlotMachine` pregunta antes de actuar:
  - `lock`: si `!isLockable()`, error y devuelve `-1`. Ver [secuencia-lock.png](diagramas/secuencia-lock.png).
  - `delWheel`: si `!isRemovable()`, error y no la quita. Ver [secuencia-delWheel.png](diagramas/secuencia-delWheel.png).
  - `swap`: si alguna de las dos `!isSwappable()`, error.
- En todos los casos `ok() = false` y la máquina queda igual. Sí gira normalmente.

### 6. `swap(int wheel1, int wheel2)` (toda la lógica adentro)
Ver [secuencia-swap.png](diagramas/secuencia-swap.png).
1. Valida que haya al menos 2 ruedas y que las posiciones existan.
2. Toma `w1` y `w2`. Si alguna es rebel (`!isSwappable()`) o está bloqueada, error.
3. Si `wheel1 == wheel2`, no hay nada que hacer (`ok = true`).
4. Guarda los ejes `x1` y `x2`, cambia las ruedas de lugar en la lista (`wheels.set` dos veces) y mueve cada una al eje de la otra con `updateAxisPosition`.

Se intercambian las **ruedas completas** (tipo, símbolos y estado), no solo los colores.

### 7. Símbolo `ephemeral` (`EphemeralSymbol.spun()`)
- Es un `Circle` de 30 px de diámetro.
- En cada giro de su rueda pierde 4 px (`SHRINK_STEP`), sin bajar de 2 px (`POINT_SIZE`): 30, 26, 22, 18, 14, 10, 6, 2. A los 7 giros queda como un punto.
- Se mueve la mitad de lo que se encoge, para quedar centrado.
- Aunque sea un punto, su color sigue contando.

### 8. Símbolo `shy` (`ShySymbol.selected()`)
- Es un `Triangle`. Tiene el atributo `hidden`, que empieza en `false`.
- Cada vez que es seleccionado, `hidden = !hidden`: la primera vez se esconde, la siguiente reaparece.
- Escondido **no muestra color**: `configuration()` da `""` en su rueda, y no cuenta en `distinctSymbols()` ni en `isJackpot()`.
- `Symbol` tiene `isHidden()` (devuelve `false`) y `setHidden()` (no hace nada); el shy los redefine. Así `SlotMachine` pregunta sin saber qué tipo es.

### 9. Rueda `reverse` (propuesta, requisito 19)
- `ReverseWheel.rotate(steps)` llama a `super.rotate(-steps)`: cada paso va al símbolo **anterior**.
- Ejemplo con `[red, blue, green]` mostrando red: una normal con `spin(w, 1)` pasa a blue; la reverse pasa a green.
- Es el ejemplo de extensibilidad: una clase, un método redefinido y una línea en la fábrica.

### 10. `configuration()` y `getShownColor()`
Ver [secuencia-configuration.png](diagramas/secuencia-configuration.png).
`Wheel.getShownColor()` devuelve `"white"` si la rueda está vacía, `""` si el símbolo visible está escondido, y si no, su color.

### 11. Cómo se distinguen a la vista
- **Símbolos:** cuadrado = normal, círculo = ephemeral, triángulo = shy.
- **Ruedas:** cada una tiene una marca (`marker`, un `Rectangle`) debajo del marco. Cada subclase le da forma en `styleMarker`: línea gris = normal, cuadrado azul a la izquierda = lefty, barra roja = rebel, cuadrado verde a la derecha = reverse.

Ver [tipos.png](tipos.png).

---

## Pruebas
| Clase | Pruebas | Qué cubren |
|---|---|---|
| `SlotMachineC4Test` | 36 | Cada tipo, la fábrica, la lefty (también con shy escondido), la rebel, la reverse, el ephemeral, el shy, los símbolos registrados, los colores repetidos, la rueda vacía y `exit()`. |
| `SlotMachineCC4Test` | 15 activas, 4 comentadas | Las nuestras y las de otros grupos que usan `SlotMachine`. Las comentadas usan clases internas de otros grupos (constructores y métodos que no tenemos); el motivo está escrito encima de cada una. |
| `SlotMachineAcceptanceTest` | 2 | Para la presentación: se ejecutan visibles y preguntan "¿es correcto?". |
| Ciclos 2 y 3 | 22 | Siguen pasando sin cambios. |

En total pasan las 73 pruebas de unidad.

## Preguntas probables y respuesta corta
- **¿Por qué clases abstractas y no un atributo `type` con `if`?** Con `if` habría que tocar `SlotMachine` y `Wheel` en cada tipo nuevo. Con herencia, cada tipo redefine su comportamiento y la máquina no cambia.
- **¿Por qué la máquina guarda los símbolos?** Para que una rueda agregada después nazca con los mismos símbolos, y para poder agregar símbolos antes que ruedas.
- **¿Por qué placeSymbol no encoge el ephemeral?** Porque no es un giro: pone un color exacto. Sí es una selección, por eso el shy sí alterna.
- **¿La rebel se puede desbloquear?** Nunca se bloquea, así que `unlock` no le cambia nada.
- **¿Por qué `Shape` es abstracta?** Porque no existe una "figura" sin forma: no se sabe dibujar. Solo se crean `Rectangle`, `Circle` o `Triangle`.
- **¿Por qué el ephemeral guarda `circle` si ya tiene `shape`?** Porque `shape` es de tipo `Shape`, y `Shape` no tiene `changeSize`. Con `circle` se evita hacer un cast.
- **¿`solve` y `simulate` cambian?** No. `SlotMachine(n)` crea solo elementos normales.
