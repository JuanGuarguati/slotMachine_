# Guía para probar el Ciclo 4 (y por qué está hecho así)

Esta guía tiene tres partes:
1. **Los fundamentos:** qué pide el enunciado y qué ideas de POO usamos.
2. **Prueba manual en BlueJ:** paso a paso, con lo que debe salir en cada paso y de qué parte del enunciado viene.
3. **Pruebas automáticas:** cómo correr las de unidad y las de aceptación, y qué prueba cada una.

Todos los resultados de la parte 2 los comprobé ejecutando el código; son los que debe ver.

---

## 1. Fundamentos

### Lo que dice el enunciado (Ciclo 4, "Refactoring y Extensión")
| Parte del enunciado | Texto | Qué hicimos |
|---|---|---|
| Requisito implícito | "Siempre hay un requisito implícito: el de **EXTENSIBILIDAD**." | Clases abstractas `Wheel` y `Symbol` + una subclase por tipo. Un tipo nuevo = una clase nueva, sin tocar `SlotMachine`. |
| Req. 16 | "Manejar diferentes tipos de ruedas y de símbolos." | `addWheel(type, pos)` y `addSymbol(type, pos, color)`. |
| Req. 17 | "normal (la que tenemos), **lefty** (si hay una rueda a su izquierda, al girar copia su estado), **rebel** (no se deja bloquear, ni intercambiar, ni eliminar)." | `NormalWheel`, `LeftyWheel`, `RebelWheel`. |
| Req. 18 | "normal (el que tenemos), **ephemeral** (en cada giro va decrementando su tamaño hasta quedar como un punto), **shy** (alterna su estado de visible a invisible, cada vez que es seleccionado en la rueda)." | `NormalSymbol`, `EphemeralSymbol`, `ShySymbol`. |
| Req. 19 | "Ofrecer un nuevo tipo de alguno de los elementos. (Propuesto por ustedes)." | `ReverseWheel`: gira hacia atrás. |
| Usabilidad 1 | "Los elementos de diferentes tipos deben poder distinguirse claramente visualmente." | Cada símbolo tiene su figura y cada rueda tiene su marca. |
| Entrega 3, 4, 5 | `SlotMachineC4Test`, mínimo dos pruebas en `SlotMachineCC4Test`, dos pruebas de aceptación. | 36 pruebas en C4, 15 activas en CC4 y 2 de aceptación. |
| Inicio del documento | "En este simulador los símbolos deben ser de colores diferentes." | `addSymbol` rechaza un color repetido. |

### Las ideas de POO que usamos
- **Herencia:** las cosas comunes van en la clase madre (`Wheel`, `Symbol`, `Shape`) y cada hija hereda eso sin repetirlo.
- **Clase abstracta:** `Wheel`, `Symbol` y `Shape` no se pueden crear con `new`, porque no existe "una rueda sin tipo". Solo se crean sus hijas.
- **Polimorfismo:** `SlotMachine` llama al mismo método (por ejemplo `w.finishSpin(left)` o `w.isLockable()`) sin saber qué tipo de rueda es, y cada hija responde a su manera.
- **Redefinir (`@Override`):** cada hija cambia solo lo que la hace distinta. Por ejemplo, la rebel solo redefine `isLockable`, `isSwappable` e `isRemovable`.
- **Fábrica (`create`):** `Wheel.create(type, x)` y `Symbol.create(type, color, x, y)` tienen un `switch` que crea la hija correcta, o devuelve `null` si el tipo no existe. Así `SlotMachine` nunca hace `new LeftyWheel(...)`.
- **Extensibilidad (la razón de todo lo anterior):** para la rueda reverse solo creamos una clase con un método y añadimos un `case` en la fábrica. `SlotMachine` no cambió.

### Decisiones de interpretación (lo que el enunciado no dice exacto)
| Duda | Qué decidimos | Por qué |
|---|---|---|
| ¿Qué es un "giro"? | `spin()`, `spin(wheel)` y `spin(wheel, steps)` con steps > 0. `placeSymbol` y `spin(String[])` **no** son giros: ponen un color exacto. | El ephemeral dice "en cada giro". Si `placeSymbol` lo encogiera, no tendría sentido. |
| ¿Qué es "seleccionado"? | Quedar como el símbolo visible: tras un giro o con `placeSymbol` / `spin(String[])`. | El shy dice "cada vez que es seleccionado en la rueda". |
| ¿Qué es el "estado" que copia la lefty? | El color visible y si el shy está escondido. | Es lo que se ve de la rueda de la izquierda. Si la lefty no tiene ese color, gira normal. |
| ¿Cómo se ve un shy escondido? | No se dibuja, `configuration()` da `""` y no cuenta en `distinctSymbols()` ni en `isJackpot()`. | "Invisible" = no se ve, así que no aporta color. |
| ¿Cuándo es un punto el ephemeral? | Empieza en 30 px y pierde 4 px por giro, hasta 2 px. A los 7 giros es un punto y nunca desaparece. | "Hasta quedar como un punto": no debe llegar a 0. |
| Rueda vacía en `configuration()` | Devuelve `"white"`. | Lo piden las pruebas compartidas. |

---

## 2. Prueba manual en BlueJ, paso a paso

**Antes de empezar:** compila (botón **Compilar**). Para crear la máquina: clic derecho en `SlotMachine` → `new SlotMachine()`. Aparece un objeto rojo abajo. Para llamar un método: clic derecho en ese objeto rojo → elige el método. Para ver si funcionó: llama `ok()` y `configuration()`.

Para pasar un arreglo de texto en BlueJ, escríbelo así: `{"green", "red", "blue", "red"}`.

### Parte A: tipos de ruedas
| # | Llamada | Qué debe pasar | Por qué (enunciado) |
|---|---|---|---|
| 1 | `makeVisible()` | Todavía no se ve nada: la máquina está vacía y los símbolos solo se dibujan dentro de una rueda. El lienzo aparece en el paso 5, con la primera rueda. | Se llama al inicio para que los errores de los pasos 3 y 4 salgan como mensaje. |
| 2 | `addSymbol(1, "red")`, `addSymbol(2, "blue")`, `addSymbol(3, "green")` | `symbols()` = `[red, blue, green]`. Se registran aunque no haya ruedas. | Req. 16. Los símbolos quedan guardados en la máquina. |
| 3 | `addSymbol(4, "RED")` | Mensaje de error, `ok()` = `false`. | "Los símbolos deben ser de colores diferentes" (sin importar mayúsculas). |
| 4 | `addSymbol("diamond", 4, "black")` | Error, `ok()` = `false`. | Req. 16: solo existen los tipos definidos; la fábrica devuelve `null`. |
| 5 | `addWheel("normal", 1)`, `addWheel("lefty", 2)`, `addWheel("rebel", 3)`, `addWheel("reverse", 4)` | 4 ruedas, cada una con su marca debajo. `configuration()` = `[red, red, red, red]`. Cada rueda nace con una copia de los 3 símbolos. | Req. 16 y 17, y usabilidad 1. |
| 6 | `addWheel("gigante", 5)` | Error, `ok()` = `false`. | Tipo que no existe. |
| 7 | `spin({"green", "red", "blue", "red"})` | `[green, red, blue, red]`. | Pone colores exactos para preparar la prueba. |
| 8 | `spin(2, 1)` | `[green, green, blue, red]`: la lefty **copió** el verde de su izquierda. | Req. 17 lefty: "al girar copia su estado". |
| 9 | `lock(3)` | Error y devuelve `-1`. | Req. 17 rebel: "no se deja bloquear". |
| 10 | `delWheel(3)` | Error; siguen las 4 ruedas. | Rebel: "ni eliminar". |
| 11 | `swap(1, 3)` | Error; nada cambia. | Rebel: "ni intercambiar". |
| 12 | `spin(4, 1)` | `[green, green, blue, green]`: la reverse pasó de red a **green** (hacia atrás). Una normal habría pasado a blue. | Req. 19, nuestro tipo propuesto. |
| 13 | `spin()` varias veces | La rueda 2 siempre queda igual que la rueda 1. | Las ruedas giran de izquierda a derecha, así la lefty copia a su vecina ya girada. |
| 14 | `swap(1, 2)` | `ok()` = `true`. Se mueve la marca azul de la lefty a la rueda 1: se cambian las **ruedas completas**, con su tipo. | Si solo se cambiaran colores, se perderían los tipos (por eso quitamos `swapContentWith`). |

### Parte B: tipos de símbolos
Crea **otra** máquina: `new SlotMachine()` y luego `makeVisible()`.

| # | Llamada | Qué debe pasar | Por qué (enunciado) |
|---|---|---|---|
| 1 | `addWheel(1)`, `addWheel(2)` | Se abre el lienzo con dos ruedas normales vacías (sin símbolos todavía). | El `addWheel(pos)` viejo crea ruedas normales. |
| 2 | `addSymbol("normal", 1, "red")`, `addSymbol("ephemeral", 2, "blue")`, `addSymbol("shy", 3, "green")` | Cuadrado rojo visible en las dos ruedas. `configuration()` = `[red, red]`. | Req. 18 y usabilidad 1: cuadrado, círculo y triángulo. |
| 3 | `placeSymbol(1, "blue")` | La rueda 1 muestra el **círculo** azul, con su tamaño completo. | `placeSymbol` no es un giro, así que no lo encoge. |
| 4 | `spin(1, 3)` (vuelta completa) 7 veces | El círculo se encoge en cada giro hasta quedar como **un punto**. `configuration()[0]` sigue siendo `blue`. | Req. 18 ephemeral: "en cada giro va decrementando su tamaño hasta quedar como un punto". |
| 5 | `placeSymbol(2, "green")` | El triángulo verde fue seleccionado y se **esconde**: el espacio queda vacío y `configuration()` = `[blue, ""]`. `distinctSymbols()` = 1. | Req. 18 shy: "alterna su estado de visible a invisible, cada vez que es seleccionado". |
| 6 | `placeSymbol(2, "green")` otra vez | El triángulo **reaparece**: `[blue, green]` y `distinctSymbols()` = 2. | Alterna: la siguiente selección lo vuelve visible. |
| 7 | `exit()` | Se cierra el lienzo, pero BlueJ sigue abierto. | Antes usaba `System.exit` y cerraba BlueJ y las pruebas. |

### Parte C: la herencia de `shapes`
Ábrela en el diagrama de BlueJ: `Rectangle`, `Circle` y `Triangle` tienen una flecha de herencia hacia `Shape`. Para probarlo: clic derecho en `Circle` → `new Circle()` → `makeVisible()` → `moveHorizontal(50)`. Esos dos métodos están escritos solo en `Shape`, y aun así funcionan en el círculo, porque los hereda.

---

## 3. Pruebas automáticas

### Cómo correrlas en BlueJ
- **Una clase de pruebas completa:** clic derecho en la clase verde (por ejemplo `SlotMachineC4Test`) → **Probar todo**. Sale una ventana con la lista: verde = pasa, rojo = falla.
- **Todas a la vez:** botón **Ejecutar pruebas** en la barra izquierda. Si no aparece, en Herramientas → Preferencias busca la casilla "Mostrar herramientas de prueba" y márcala.
- **Una sola:** clic derecho en la clase verde → elige el nombre de la prueba.

Resultado esperado: pasan **las 73** pruebas de unidad: C4 (36), CC4 (15), C2 (12), CC2 (2), ContestTest (6) y ContestCTest (2).

### `SlotMachineC4Test` (36): qué prueba cada grupo
Todas siguen la forma **Arrange / Act / Assert**: preparar, hacer una acción y comprobar el resultado.

| Grupo | Pruebas | Requisito |
|---|---|---|
| Creación de tipos | `addWheelShouldAcceptEveryWheelType`, `addWheelShouldIgnoreUpperCaseInType`, `addWheelShouldRejectUnknownType`, `addSymbolShouldAcceptEverySymbolType`, `addSymbolShouldRejectUnknownType`, `oldAddMethodsShouldCreateNormalElements`, `factoriesShouldBuildTheRightClasses`, `randomMachineShouldStillUseOnlyNormalElements` | 16 y extensibilidad |
| Lefty | `leftyWheelShouldCopyLeftWheelWhenSpinningSteps`, `...WhenSpinningRandomly`, `leftyWheelWithoutLeftWheelShouldSpinLikeNormal`, `leftyWheelShouldNotCopyWhenSymbolIsPlaced`, `leftyWheelShouldCopyHiddenStateOfLeftShy` | 17 |
| Rebel | `rebelWheelShouldNotBeLocked`, `...NotBeSwapped`, `...NotBeDeleted`, `normalWheelNextToRebelShouldStillBeDeleted` | 17 |
| Reverse | `reverseWheelShouldRotateBackwards`, `reverseWheelShouldCompleteATurnWithAllSteps` | 19 |
| Swap | `swapShouldMoveTheWheelType` | Corrección de swap |
| Ephemeral | `ephemeralSymbolShouldShrinkOnEachSpin`, `...ShouldEndAsAPoint`, `ephemeralSymbolsShouldShrinkWhenTheirWheelSpins`, `...ShouldNotShrinkWhenPlaced`, `...ShouldKeepItsColor` | 18 |
| Shy | `shySymbolShouldToggleEachTimeItIsSelected`, `...WhenPlacedAndWhenSpunIntoView`, `...ShouldNotToggleWhenAnotherSymbolIsSelected`, `hiddenShySymbolShouldNotCountForJackpot` | 18 |
| Símbolos registrados | `addSymbolShouldWorkWithoutWheels`, `newWheelShouldGetACopyOfTheRegisteredSymbols`, `addSymbolShouldRejectRepeatedColor`, `emptyWheelShouldShowWhite`, `delSymbolShouldRejectUnregisteredColor`, `delSymbolShouldRemoveFromWheelsAndRegistry` | 16 y colores diferentes |
| Salida | `exitShouldNotStopTheProgram` | Corrección de `exit` |

### `SlotMachineCC4Test` (compartidas)
- Las 15 activas son las nuestras y las de otros grupos que usan solo los métodos de `SlotMachine`.
- 4 están comentadas al final del archivo, cada una con el motivo escrito encima. En resumen: usan constructores y métodos de las clases internas de otro grupo (`new Wheel()`, `getSize()`, `onWheelSpin()`, etc.) que nuestro diseño no tiene. Además, dos se contradicen entre sí: una crea `EphemeralSymbol` con dos `String` y otra con uno solo, así que ningún diseño podría pasar las dos. La idea que prueban sí la cumplimos, con otras pruebas.

### Pruebas de aceptación (para la presentación)
Clic derecho en `SlotMachineAcceptanceTest` → elige una prueba. Se abre el simulador y salen mensajes que dicen qué mirar. Al final pregunta "¿es correcto?": responde **Sí** y la prueba pasa en verde.
1. `wheelTypesShouldBehaveAndLookDifferent`: las 4 ruedas con sus marcas. La lefty copia el verde, la rebel rechaza `lock` y `delWheel` (dos mensajes de error) y la reverse pasa de rojo a verde.
2. `symbolTypesShouldBehaveAndLookDifferent`: cuadrado, círculo y triángulo. El círculo da 8 vueltas y queda como un punto; el triángulo se esconde al seleccionarlo y reaparece en la siguiente selección.
