# Qué cambiar en el Astah para dejarlo en el Ciclo 4

Se parte del Astah del Ciclo 3 (ya corregido con `astah-cambios-ciclo3.md`). Abajo está solo lo que cambia.

---

## 1. Diagrama de clases

### Symbol
- Marcarla como **abstracta** (en Astah: propiedad *abstract*; el nombre sale en cursiva).
- **Eliminar** el atributo `shape : Rectangle` y su composición con `Rectangle`.
- **Añadir**
  - Constantes públicas estáticas: `NORMAL : String = "normal"`, `EPHEMERAL : String = "ephemeral"`, `SHY : String = "shy"`.
  - Constante protegida estática: `# SIZE : int = 30`.
  - `+ create(type : String, color : String, x : int, y : int) : Symbol` (estático, subrayado).
  - `+ isHidden() : boolean`, `+ setHidden(hidden : boolean) : void`, `+ spun() : void`, `+ selected() : void`.
  - Abstractos (en cursiva): `+ shiftHorizontal(distance : int) : void`, `# show() : void`, `# hide() : void`.
- El constructor pasa a `# Symbol(color : String)`.

### NormalSymbol, EphemeralSymbol, ShySymbol (nuevas)
| Clase | Atributos | Métodos |
|---|---|---|
| `NormalSymbol` | `- shape : Rectangle` | constructor `(color, x, y)`, `shiftHorizontal`, `show`, `hide` |
| `EphemeralSymbol` | `- shape : Circle`, `- diameter : int`, `- SHRINK_STEP : int = 4`, `- POINT_SIZE : int = 2` | constructor, `spun`, `getDiameter() : int`, `isPoint() : boolean`, `shiftHorizontal`, `show`, `hide` |
| `ShySymbol` | `- shape : Triangle`, `- hidden : boolean` | constructor, `selected`, `isHidden`, `setHidden`, `shiftHorizontal`, `show`, `hide` |

Cada una con **generalización** (flecha de herencia, triángulo vacío) hacia `Symbol` y **composición** `1 — 1` con su figura (`Rectangle`, `Circle`, `Triangle`).

### Wheel
- Marcarla como **abstracta**.
- **Eliminar** `swapContentWith(other : Wheel)`.
- **Añadir**
  - Constantes públicas estáticas: `NORMAL`, `LEFTY`, `REBEL`, `REVERSE`, `EMPTY_COLOR : String = "white"`.
  - Constantes privadas estáticas: `SYMBOL_Y : int = 100`, `MARKER_Y : int = 112`.
  - Atributo `- marker : Rectangle` (composición `1 — 1` con `Rectangle`).
  - `+ create(type : String, xPosition : int) : Wheel` (estático).
  - `# styleMarker(marker : Rectangle) : void` (abstracto).
  - `+ finishSpin(left : Wheel) : void`, `# afterSpin(left : Wheel) : void`, `+ select() : void`.
  - `+ getShownColor() : String`.
  - `+ isLockable() : boolean`, `+ isSwappable() : boolean`, `+ isRemovable() : boolean`.
  - `+ showActive() : void`, `- hideActive() : void`.
- **Cambiar** `addSymbol(pos, color)` por `addSymbol(type : String, pos : int, color : String) : boolean`.
- El constructor pasa a `# Wheel(xPosition : int)`.

### NormalWheel, LeftyWheel, RebelWheel, ReverseWheel (nuevas)
| Clase | Redefine |
|---|---|
| `NormalWheel` | `styleMarker` |
| `LeftyWheel` | `styleMarker`, `afterSpin(left : Wheel)` |
| `RebelWheel` | `styleMarker`, `isLockable`, `isSwappable`, `isRemovable` |
| `ReverseWheel` | `styleMarker`, `rotate(steps : int)` |

Las cuatro con generalización hacia `Wheel`.

### SlotMachine
- **Añadir**
  - Atributos `- symbolTypes : ArrayList<String>` y `- symbolColors : ArrayList<String>`.
  - `+ addWheel(type : String, pos : int) : void` y `+ addSymbol(type : String, pos : int, color : String) : void` (los que pide el diagrama del enunciado).
- La asociación `SlotMachine ◆— Wheel` queda igual: la máquina solo conoce la clase abstracta.
- **Añadir** dependencia `SlotMachine ⇢ Symbol` (usa `Symbol.create` y `Symbol.NORMAL` en `addSymbol`).

### SlotMachineContest
- Sin cambios.

### shapes
- Añadir `Circle` (`changeSize(newDiameter : int)`) y `Triangle` (`changeSize(newHeight : int, newWidth : int)`), si no están.

---

## 2. Diagramas de secuencia

### Nuevos
- **`addWheel(type, pos)`**: `SlotMachine` → `Wheel.create(type, 0)`. Si da `null` → `notifyError`. Si no, un ciclo (*loop*) sobre los símbolos registrados → `newWheel.addSymbol(type_i, i+1, color_i)` → `Symbol.create(...)`. Luego `wheels.add`, `recalculateAxes()`, `refreshVisibility()`.
- **`addSymbol(type, pos, color)`**: `Symbol.create(type, color, 0, 0)` para validar el tipo → *loop* para buscar un color repetido → insertar en `symbolTypes` y `symbolColors` → *loop* sobre las ruedas no bloqueadas → `wheel.addSymbol(type, pos, color)`.

### Actualizar
- **`spin(wheel, steps)`**: después del ciclo de `rotate(1)`, un fragmento *opt* `[steps > 0]` con `finishSpin(left)` → *loop* `spun()` sobre los símbolos → `afterSpin(left)`. En la lefty, `afterSpin` llama a `left.getVisibleSymbol()`, `setVisibleColor(color)` y `setHidden(...)`.
- **`spin()`**: *loop* sobre las ruedas, de izquierda a derecha; en cada una `spin()` y `finishSpin(left)`.
- **`swap(wheel1, wheel2)`**: las preguntas `isSwappable()` e `isLocked()` de las dos ruedas; luego `wheels.set(...)` dos veces y `updateAxisPosition(x)` en cada rueda. Ya no hay llamada a `swapContentWith`.
- **`lock(wheel)`**: agregar la pregunta `isLockable()` antes de `lock()`.
- **`delWheel(pos)`**: agregar la pregunta `isRemovable()` antes de `wheels.remove`.
- **`placeSymbol(wheel, symbol)`**: después de `setVisibleColor`, la llamada `select()` → `selected()`.
- **`configuration()`**: *loop* con `getShownColor()` en vez de `getVisibleSymbol().getColor()`.
- **`exit()`**: quitar la llamada a `System.exit(0)`.
