# Ciclo 4: cómo se implementó

Se partió del código del Ciclo 3 ya corregido (`SlotMachineContest` sin `ok()`, n validado con `MIN_N`/`MAX_N`, toda la lógica dentro de `solve` y `simulate`).

## La idea en una línea
`Wheel` y `Symbol` pasan a ser **clases abstractas**. Cada tipo es una subclase que redefine solo lo que cambia. `SlotMachine` crea los objetos con una fábrica (`Wheel.create`, `Symbol.create`) y no conoce las clases concretas. Así, agregar un tipo nuevo es crear una clase y añadir una línea en la fábrica (requisito implícito de **extensibilidad**).

```
Wheel (abstracta)            Symbol (abstracta)
├── NormalWheel              ├── NormalSymbol    (cuadrado)
├── LeftyWheel               ├── EphemeralSymbol (círculo que se encoge)
├── RebelWheel               └── ShySymbol       (triángulo que se esconde)
└── ReverseWheel (propuesta)
```

## Requisitos y dónde se cumplen
| Requisito | Cómo se cumple |
|---|---|
| 16. Diferentes tipos | `addWheel(type, pos)` y `addSymbol(type, pos, color)`. Los métodos viejos `addWheel(pos)` y `addSymbol(pos, color)` crean elementos `"normal"`. Un tipo que no existe da `ok() = false` y no agrega nada. Los tipos no distinguen mayúsculas. |
| 17. Rueda `normal` | `NormalWheel`: el comportamiento de siempre. |
| 17. Rueda `lefty` | `LeftyWheel.afterSpin(left)`: al terminar un giro copia el **estado** de la rueda de la izquierda: su color visible y, si es shy, si está escondido o no. Si es la primera rueda, si la vecina está vacía o si ella no tiene ese color, gira normal. |
| 17. Rueda `rebel` | `RebelWheel` responde `false` a `isLockable()`, `isSwappable()` e `isRemovable()`. `lock` (devuelve `-1`), `swap` y `delWheel` la rechazan con `ok() = false` y la máquina queda igual. Sí gira. |
| 18. Símbolo `normal` | `NormalSymbol`: el cuadrado de siempre. |
| 18. Símbolo `ephemeral` | `EphemeralSymbol.spun()`: en cada giro de su rueda el círculo pierde 4 px de diámetro (30, 26, … 2). A los 7 giros queda como un punto de 2 px y ya no baja más. Su color sigue contando. |
| 18. Símbolo `shy` | `ShySymbol.selected()`: cada vez que queda seleccionado alterna visible ↔ escondido. Empieza visible. Escondido no muestra color: `configuration()` da `""` y no cuenta en `distinctSymbols()` ni en `isJackpot()`. |
| 19. Tipo propuesto | Rueda `reverse` (`ReverseWheel`): gira hacia atrás. `rotate(steps)` llama a `super.rotate(-steps)`. |
| Usabilidad 1 | Cada tipo se distingue a la vista (ver abajo). |
| Entrega 3 | `SlotMachineC4Test`: 36 pruebas. |
| Entrega 4 | `SlotMachineCC4Test`: 15 pruebas activas (4 nuestras y 11 de otros grupos) y 4 comentadas con el motivo. |
| Entrega 5 | `SlotMachineAcceptanceTest`: 2 pruebas de aceptación. |
| Entrega 6 | `retrospectiva.md` (ciclos 1 a 4). |

## Decisiones de diseño (acordadas)
1. **Qué es un giro:** `spin()`, `spin(wheel)` y `spin(wheel, steps)` con `steps > 0`. Al terminar, `SlotMachine` llama a `wheel.finishSpin(ruedaIzquierda)`:
   1. `spun()` a todos los símbolos de la rueda (los ephemeral se encogen).
   2. `afterSpin(left)`: por defecto selecciona el símbolo visible (el shy alterna); la lefty en cambio copia a su vecina.
2. **No son giros:** `placeSymbol` y `spin(String[])`, porque ponen un color exacto. La lefty no copia y el ephemeral no se encoge. Sí cuentan como **selección**, así que el shy alterna, aunque ya estuviera visible.
3. En `spin()` las ruedas giran de izquierda a derecha, para que la lefty copie a su vecina ya girada.
4. **La máquina guarda sus símbolos registrados** (tipo y color, en orden). `addSymbol` funciona aunque no haya ruedas, y toda rueda nueva nace con una copia de esos símbolos (objetos nuevos, del mismo tipo). No se permiten dos símbolos del mismo color. `symbols()` devuelve la lista registrada.
5. Una rueda sin símbolos muestra `"white"` en `configuration()`.
6. **`swap` tiene toda su lógica dentro**: valida, cambia las dos ruedas de lugar en la lista y mueve cada una al eje de la otra. Se eliminó `Wheel.swapContentWith`, que volvía a crear los símbolos como cuadrados normales y perdía los tipos.
7. `SlotMachine(n)` sigue creando solo elementos normales, así que `solve` y `simulate` funcionan igual.
8. `exit()` oculta el simulador pero ya **no llama a `System.exit(0)`**: eso cerraba BlueJ y cortaba las pruebas compartidas que llaman `exit()` al terminar.

## Cómo se distinguen a la vista
- **Símbolos**, por figura: cuadrado = normal, círculo = ephemeral, triángulo = shy.
- **Ruedas**, por una marca debajo del marco: línea gris = normal, cuadrado azul a la izquierda = lefty, barra roja gruesa = rebel, cuadrado verde a la derecha = reverse.

Ver [tipos.png](tipos.png): ruedas normal, lefty, rebel, reverse, normal y normal. La rueda 4 tiene el shy escondido, la 5 lo tiene visible y la 6 tiene un ephemeral que ya giró 3 veces.

---

## Cambios por archivo

### Symbol.java (ahora abstracta)
**Eliminado**
- El atributo `shape` (`Rectangle`): pasa a cada subclase, porque cada tipo usa una figura distinta.

**Nuevo**
- Constantes `NORMAL`, `EPHEMERAL`, `SHY` y `SIZE = 30`.
- `create(type, color, x, y)`: fábrica estática; devuelve `null` si el tipo no existe.
- `spun()` y `selected()`: no hacen nada; las subclases los redefinen.
- `isHidden()` (devuelve `false`) y `setHidden(hidden)` (no hace nada); el shy los redefine.
- Abstractos: `shiftHorizontal(distance)`, `show()`, `hide()`.

**Cambiado**
- El constructor es `protected Symbol(color)`.
- `makeVisible()` y `makeInvisible()` cambian `isVisible` y llaman a `show()` / `hide()`.

### NormalSymbol.java, EphemeralSymbol.java, ShySymbol.java (nuevas)
- `NormalSymbol`: un `Rectangle` de 30x30.
- `EphemeralSymbol`: un `Circle`. Atributo `diameter`, constantes `SHRINK_STEP = 4` y `POINT_SIZE = 2`, y métodos `spun()`, `getDiameter()` e `isPoint()`.
- `ShySymbol`: un `Triangle` de 30x30. Atributo `hidden` y métodos `selected()`, `isHidden()` y `setHidden(hidden)`. `show()` no dibuja si está escondido.

### Wheel.java (ahora abstracta)
**Eliminado**
- `swapContentWith(other)`.

**Nuevo**
- Constantes `NORMAL`, `LEFTY`, `REBEL`, `REVERSE`, `EMPTY_COLOR = "white"`, `SYMBOL_Y = 100` y `MARKER_Y = 112`.
- Atributo `marker` (`Rectangle`): la marca del tipo.
- `create(type, x)`: fábrica estática; devuelve `null` si el tipo no existe.
- `styleMarker(marker)`: abstracto; cada tipo le da tamaño, color y posición.
- `finishSpin(left)`, `afterSpin(left)` (por defecto llama a `select()`) y `select()`.
- `getShownColor()`: el color que se ve (`""` si está escondido, `"white"` si está vacía).
- `isLockable()`, `isSwappable()`, `isRemovable()`: devuelven `true` por defecto.
- `showActive()`: dibuja solo el símbolo activo.

**Cambiado**
- El constructor es `protected Wheel(x)`. Crea la marca y llama a `styleMarker`.
- `addSymbol(type, pos, color)`: recibe el tipo y devuelve `boolean`.
- `delSymbol`: ahora borra del lienzo los símbolos eliminados.
- `spin()` y `rotate()`: solo mueven la rueda. Los efectos del giro van en `finishSpin`. `spin()` ya no revisa el bloqueo (lo revisa `SlotMachine`).
- `updateAxisPosition`: también mueve la marca.
- `makeVisible()` / `makeInvisible()`: muestran u ocultan también la marca.

### NormalWheel.java, LeftyWheel.java, RebelWheel.java, ReverseWheel.java (nuevas)
- Las cuatro redefinen `styleMarker`.
- `LeftyWheel` redefine `afterSpin(left)`.
- `RebelWheel` redefine `isLockable`, `isSwappable` e `isRemovable`.
- `ReverseWheel` redefine `rotate(steps)`.

### SlotMachine.java
**Nuevo**
- Atributos `symbolTypes` y `symbolColors` (`ArrayList<String>`): los símbolos registrados.
- `addWheel(String type, int pos)` y `addSymbol(String type, int pos, String color)`.

**Cambiado**
- `addWheel(pos)` → `addWheel("normal", pos)`; `addSymbol(pos, color)` → `addSymbol("normal", pos, color)`.
- `addSymbol`: registra el símbolo; funciona sin ruedas; rechaza tipo inexistente y color repetido.
- `delSymbol`: lo quita del registro; rechaza un color no registrado.
- `delWheel`, `lock`: rechazan la rueda rebel.
- `swap`: toda la lógica adentro; rechaza la rebel; intercambia las ruedas completas.
- `spin()`, `spin(wheel)`, `spin(wheel, steps)`: llaman a `finishSpin(left)` después de mover.
- `placeSymbol`, `spin(String[])`: llaman a `select()`.
- `configuration()`: usa `getShownColor()`.
- `symbols()`: devuelve los símbolos registrados (ya no da error sin ruedas).
- `distinctSymbols()`, `allSameColor()`: ignoran el shy escondido.
- `exit()`: ya no llama a `System.exit(0)`.
- Javadoc: `3 <= n <= 50` pasa a `entre 3 y 50`, porque el `<` rompe la generación de la documentación.

### SlotMachineContest.java
- Solo el javadoc (`entre 3 y 50`). La lógica es la misma del Ciclo 3.

### Pruebas
- **Nuevas:** `SlotMachineC4Test` (36), `SlotMachineCC4Test` (15 activas) y `SlotMachineAcceptanceTest` (2).
- **Sin cambios:** `SlotMachineC2Test`, `SlotMachineCC2Test`, `SlotMachineContestTest`, `SlotMachineContestCTest`.

## Verificación
- Pasan las 73 pruebas de unidad: C4 (36), CC4 (15), C2 (12), CC2 (2), ContestTest (6) y ContestCTest (2). Se corrieron con pantalla virtual.
- Las dos pruebas de aceptación se ejecutaron completas: salen todos los mensajes y los dos errores de la rebel.
- La documentación (javadoc) se genera sin errores.

---

## Pruebas de aceptación (para la presentación)
Se ejecutan con la pantalla visible. Cada paso muestra un mensaje que explica lo que va a pasar. Al final se pregunta "¿es correcto?", y la prueba pasa si se responde **Sí**.
1. `wheelTypesShouldBehaveAndLookDifferent`: crea las 4 ruedas. La lefty copia el verde de la rueda 1. La rebel rechaza `lock` y `delWheel`, con dos mensajes de error. La reverse pasa de rojo a verde, hacia atrás.
2. `symbolTypesShouldBehaveAndLookDifferent`: crea un cuadrado, un círculo y un triángulo. El círculo da 8 giros y queda como un punto. El triángulo se esconde al seleccionarlo y reaparece al seleccionarlo otra vez.
