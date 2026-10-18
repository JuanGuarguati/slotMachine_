import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Casos de prueba de unidad del Ciclo 4: tipos de ruedas (normal, lefty,
 * rebel, reverse) y tipos de símbolos (normal, ephemeral, shy).
 *
 * @version 2.0 (Ciclo 4)
 */
public class SlotMachineC4Test {

    private SlotMachine machine;

    @Before
    public void setUp() {
        // Arrange: máquina vacía; cada prueba agrega sus ruedas
        machine = new SlotMachine();
    }

    /**
     * Agrega tres colores normales en el orden [red, blue, green].
     */
    private void addThreeColors() {
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
    }

    // ---------------------------------------------------------------
    // Creación de tipos (Requisito 16)
    // ---------------------------------------------------------------

    @Test
    public void addWheelShouldAcceptEveryWheelType() {
        // Act & Assert
        machine.addWheel("normal", 1);
        assertTrue(machine.ok());
        machine.addWheel("lefty", 2);
        assertTrue(machine.ok());
        machine.addWheel("rebel", 3);
        assertTrue(machine.ok());
        machine.addWheel("reverse", 4);
        assertTrue(machine.ok());
        assertEquals(4, machine.configuration().length);
    }

    @Test
    public void addWheelShouldIgnoreUpperCaseInType() {
        // Act
        machine.addWheel("LEFTY", 1);

        // Assert
        assertTrue(machine.ok());
        assertEquals(1, machine.configuration().length);
    }

    @Test
    public void addWheelShouldRejectUnknownType() {
        // Act
        machine.addWheel("magic", 1);

        // Assert
        assertFalse(machine.ok());
        assertEquals(0, machine.configuration().length);
    }

    @Test
    public void addSymbolShouldAcceptEverySymbolType() {
        // Arrange
        machine.addWheel(1);

        // Act & Assert
        machine.addSymbol("normal", 1, "red");
        assertTrue(machine.ok());
        machine.addSymbol("ephemeral", 2, "blue");
        assertTrue(machine.ok());
        machine.addSymbol("shy", 3, "green");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.symbols());
    }

    @Test
    public void addSymbolShouldRejectUnknownType() {
        // Arrange
        machine.addWheel(1);

        // Act
        machine.addSymbol("ghost", 1, "red");

        // Assert
        assertFalse(machine.ok());
        assertEquals(0, machine.symbols().length);
    }

    @Test
    public void oldAddMethodsShouldCreateNormalElements() {
        // Act
        machine.addWheel(1);
        machine.addSymbol(1, "red");

        // Assert
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.configuration());
    }

    // ---------------------------------------------------------------
    // Rueda lefty
    // ---------------------------------------------------------------

    @Test
    public void leftyWheelShouldCopyLeftWheelWhenSpinningSteps() {
        // Arrange
        machine.addWheel("normal", 1);
        machine.addWheel("lefty", 2);
        addThreeColors();
        machine.spin(new String[]{"green", "red"});

        // Act
        machine.spin(2, 1);

        // Assert
        assertEquals("green", machine.configuration()[1]);
    }

    @Test
    public void leftyWheelShouldCopyLeftWheelWhenSpinningRandomly() {
        // Arrange
        machine.addWheel("normal", 1);
        machine.addWheel("lefty", 2);
        addThreeColors();

        // Act: con spin() la rueda 1 gira primero y la lefty la copia
        for (int i = 0; i < 20; i++) {
            machine.spin();

            // Assert
            String[] config = machine.configuration();
            assertEquals(config[0], config[1]);
        }
    }

    @Test
    public void leftyWheelWithoutLeftWheelShouldSpinLikeNormal() {
        // Arrange
        machine.addWheel("lefty", 1);
        machine.addWheel("normal", 2);
        addThreeColors();
        machine.spin(new String[]{"red", "green"});

        // Act
        machine.spin(1, 1);

        // Assert
        assertEquals("blue", machine.configuration()[0]);
    }

    @Test
    public void leftyWheelShouldNotCopyWhenSymbolIsPlaced() {
        // Arrange
        machine.addWheel("normal", 1);
        machine.addWheel("lefty", 2);
        addThreeColors();
        machine.placeSymbol(1, "green");

        // Act: placeSymbol no es un giro
        machine.placeSymbol(2, "blue");

        // Assert
        assertEquals("blue", machine.configuration()[1]);
    }

    // ---------------------------------------------------------------
    // Rueda rebel
    // ---------------------------------------------------------------

    @Test
    public void rebelWheelShouldNotBeLocked() {
        // Arrange
        machine.addWheel("rebel", 1);
        addThreeColors();

        // Act
        int result = machine.lock(1);

        // Assert
        assertEquals(-1, result);
        assertFalse(machine.ok());
        machine.spin(1, 1);
        assertTrue("La rueda rebelde sigue girando", machine.ok());
    }

    @Test
    public void rebelWheelShouldNotBeSwapped() {
        // Arrange
        machine.addWheel("rebel", 1);
        machine.addWheel("normal", 2);
        addThreeColors();
        machine.spin(new String[]{"red", "blue"});

        // Act
        machine.swap(1, 2);

        // Assert
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "blue"}, machine.configuration());
    }

    @Test
    public void rebelWheelShouldNotBeDeleted() {
        // Arrange
        machine.addWheel("normal", 1);
        machine.addWheel("rebel", 2);

        // Act
        machine.delWheel(2);

        // Assert
        assertFalse(machine.ok());
        assertEquals(2, machine.configuration().length);
    }

    @Test
    public void normalWheelNextToRebelShouldStillBeDeleted() {
        // Arrange
        machine.addWheel("normal", 1);
        machine.addWheel("rebel", 2);

        // Act
        machine.delWheel(1);

        // Assert
        assertTrue(machine.ok());
        assertEquals(1, machine.configuration().length);
    }

    // ---------------------------------------------------------------
    // Rueda reverse (propuesta)
    // ---------------------------------------------------------------

    @Test
    public void reverseWheelShouldRotateBackwards() {
        // Arrange: símbolos [red, blue, green]
        machine.addWheel("reverse", 1);
        addThreeColors();
        machine.placeSymbol(1, "red");

        // Act
        machine.spin(1, 1);

        // Assert: una rueda normal mostraría blue
        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void reverseWheelShouldCompleteATurnWithAllSteps() {
        // Arrange
        machine.addWheel("reverse", 1);
        addThreeColors();
        machine.placeSymbol(1, "blue");

        // Act
        machine.spin(1, 3);

        // Assert
        assertEquals("blue", machine.configuration()[0]);
    }

    // ---------------------------------------------------------------
    // Swap mueve las ruedas completas, con su tipo
    // ---------------------------------------------------------------

    @Test
    public void swapShouldMoveTheWheelType() {
        // Arrange: [lefty, normal]
        machine.addWheel("lefty", 1);
        machine.addWheel("normal", 2);
        addThreeColors();

        // Act: queda [normal, lefty]
        machine.swap(1, 2);
        machine.placeSymbol(1, "green");
        machine.spin(2, 1);

        // Assert: la lefty ahora está a la derecha y copia a la rueda 1
        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[1]);
    }

    // ---------------------------------------------------------------
    // Símbolo ephemeral
    // ---------------------------------------------------------------

    @Test
    public void ephemeralSymbolShouldShrinkOnEachSpin() {
        // Arrange
        EphemeralSymbol symbol = new EphemeralSymbol("red", 50, 100);
        int initial = symbol.getDiameter();

        // Act
        symbol.spun();

        // Assert
        assertTrue(symbol.getDiameter() < initial);
    }

    @Test
    public void ephemeralSymbolShouldEndAsAPoint() {
        // Arrange
        EphemeralSymbol symbol = new EphemeralSymbol("red", 50, 100);

        // Act
        for (int i = 0; i < 50; i++) {
            symbol.spun();
        }

        // Assert
        assertTrue(symbol.isPoint());
        assertTrue(symbol.getDiameter() > 0);
    }

    @Test
    public void ephemeralSymbolsShouldShrinkWhenTheirWheelSpins() {
        // Arrange
        Wheel wheel = Wheel.create("normal", 50);
        wheel.addSymbol("ephemeral", 1, "red");
        wheel.addSymbol("ephemeral", 2, "blue");
        EphemeralSymbol red = (EphemeralSymbol) wheel.getSymbols().get(0);
        EphemeralSymbol blue = (EphemeralSymbol) wheel.getSymbols().get(1);
        int initial = red.getDiameter();

        // Act
        wheel.rotate(1);
        wheel.finishSpin(null);

        // Assert
        assertTrue(red.getDiameter() < initial);
        assertTrue(blue.getDiameter() < initial);
    }

    @Test
    public void ephemeralSymbolShouldNotShrinkWhenPlaced() {
        // Arrange
        Wheel wheel = Wheel.create("normal", 50);
        wheel.addSymbol("ephemeral", 1, "red");
        wheel.addSymbol("normal", 2, "blue");
        EphemeralSymbol red = (EphemeralSymbol) wheel.getSymbols().get(0);
        int initial = red.getDiameter();

        // Act: colocar no es girar
        wheel.setVisibleColor("red");
        wheel.select();

        // Assert
        assertEquals(initial, red.getDiameter());
    }

    @Test
    public void ephemeralSymbolShouldKeepItsColor() {
        // Arrange
        machine.addWheel(1);
        machine.addSymbol("ephemeral", 1, "red");

        // Act
        for (int i = 0; i < 10; i++) {
            machine.spin(1, 1);
        }

        // Assert
        assertEquals("red", machine.configuration()[0]);
    }

    // ---------------------------------------------------------------
    // Símbolo shy
    // ---------------------------------------------------------------

    @Test
    public void shySymbolShouldToggleEachTimeItIsSelected() {
        // Arrange
        ShySymbol symbol = new ShySymbol("green", 50, 100);
        assertFalse(symbol.isHidden());

        // Act & Assert
        symbol.selected();
        assertTrue(symbol.isHidden());
        symbol.selected();
        assertFalse(symbol.isHidden());
    }

    @Test
    public void shySymbolShouldToggleWhenPlacedAndWhenSpunIntoView() {
        // Arrange: rueda con [red, shy green]
        Wheel wheel = Wheel.create("normal", 50);
        wheel.addSymbol("normal", 1, "red");
        wheel.addSymbol("shy", 2, "green");
        ShySymbol shy = (ShySymbol) wheel.getSymbols().get(1);

        // Act & Assert: al colocarlo se selecciona y se esconde
        wheel.setVisibleColor("green");
        wheel.select();
        assertTrue(shy.isHidden());

        // Act & Assert: tras una vuelta completa vuelve a quedar seleccionado
        wheel.rotate(2);
        wheel.finishSpin(null);
        assertFalse(shy.isHidden());
    }

    @Test
    public void shySymbolShouldNotToggleWhenAnotherSymbolIsSelected() {
        // Arrange
        Wheel wheel = Wheel.create("normal", 50);
        wheel.addSymbol("normal", 1, "red");
        wheel.addSymbol("shy", 2, "green");
        ShySymbol shy = (ShySymbol) wheel.getSymbols().get(1);

        // Act: queda seleccionado red
        wheel.setVisibleColor("red");
        wheel.select();

        // Assert
        assertFalse(shy.isHidden());
    }

    @Test
    public void hiddenShySymbolShouldNotCountForJackpot() {
        // Arrange
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addSymbol("shy", 1, "green");
        assertTrue(machine.isJackpot());

        // Act: la rueda 2 selecciona su shy y lo esconde
        machine.placeSymbol(2, "green");

        // Assert
        assertEquals("", machine.configuration()[1]);
        assertFalse(machine.isJackpot());
        assertTrue(machine.ok());
        assertEquals(1, machine.distinctSymbols());
    }

    @Test
    public void leftyWheelShouldCopyHiddenStateOfLeftShy() {
        // Arrange: normal + lefty, un solo símbolo shy
        machine.addWheel("normal", 1);
        machine.addWheel("lefty", 2);
        machine.addSymbol("shy", 1, "green");
        machine.placeSymbol(1, "green");
        assertEquals("", machine.configuration()[0]);

        // Act
        machine.spin(2);

        // Assert: la lefty también queda escondida
        assertEquals("", machine.configuration()[1]);
    }

    // ---------------------------------------------------------------
    // Símbolos registrados en la máquina
    // ---------------------------------------------------------------

    @Test
    public void addSymbolShouldWorkWithoutWheels() {
        // Act
        machine.addSymbol("shy", 1, "red");

        // Assert
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red"}, machine.symbols());
    }

    @Test
    public void newWheelShouldGetACopyOfTheRegisteredSymbols() {
        // Arrange
        machine.addSymbol("normal", 1, "red");
        machine.addSymbol("ephemeral", 2, "blue");

        // Act
        machine.addWheel(1);
        machine.addWheel(2);

        // Assert: las dos ruedas tienen sus propios objetos, del mismo tipo
        machine.spin(new String[]{"blue", "blue"});
        assertTrue(machine.isJackpot());
        machine.spin(2, 1);
        assertEquals("red", machine.configuration()[1]);
        assertEquals("blue", machine.configuration()[0]);
    }

    @Test
    public void addSymbolShouldRejectRepeatedColor() {
        // Arrange
        machine.addWheel(1);
        machine.addSymbol(1, "red");

        // Act
        machine.addSymbol("shy", 2, "RED");

        // Assert
        assertFalse(machine.ok());
        assertEquals(1, machine.symbols().length);
    }

    @Test
    public void emptyWheelShouldShowWhite() {
        // Act
        machine.addWheel(1);

        // Assert
        assertArrayEquals(new String[]{"white"}, machine.configuration());
    }

    @Test
    public void delSymbolShouldRejectUnregisteredColor() {
        // Arrange
        machine.addWheel(1);
        machine.addSymbol(1, "red");

        // Act
        machine.delSymbol("blue");

        // Assert
        assertFalse(machine.ok());
        assertEquals(1, machine.symbols().length);
    }

    @Test
    public void delSymbolShouldRemoveFromWheelsAndRegistry() {
        // Arrange
        addThreeColors();
        machine.addWheel(1);

        // Act
        machine.delSymbol("blue");

        // Assert
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "green"}, machine.symbols());
        machine.placeSymbol(1, "blue");
        assertFalse(machine.ok());
    }

    @Test
    public void exitShouldNotStopTheProgram() {
        // Arrange
        machine.addWheel(1);

        // Act
        machine.exit();

        // Assert: si exit() cerrara el programa, esta línea no se ejecutaría
        assertTrue(machine.ok());
    }

    // ---------------------------------------------------------------
    // Fábricas
    // ---------------------------------------------------------------

    @Test
    public void factoriesShouldBuildTheRightClasses() {
        // Act & Assert
        assertTrue(Wheel.create("normal", 0) instanceof NormalWheel);
        assertTrue(Wheel.create("lefty", 0) instanceof LeftyWheel);
        assertTrue(Wheel.create("rebel", 0) instanceof RebelWheel);
        assertTrue(Wheel.create("reverse", 0) instanceof ReverseWheel);
        assertNull(Wheel.create("magic", 0));
        assertTrue(Symbol.create("normal", "red", 0, 0) instanceof NormalSymbol);
        assertTrue(Symbol.create("ephemeral", "red", 0, 0) instanceof EphemeralSymbol);
        assertTrue(Symbol.create("shy", "red", 0, 0) instanceof ShySymbol);
        assertNull(Symbol.create("ghost", "red", 0, 0));
    }

    // ---------------------------------------------------------------
    // Lo anterior sigue funcionando
    // ---------------------------------------------------------------

    @Test
    public void randomMachineShouldStillUseOnlyNormalElements() {
        // Act
        SlotMachine random = new SlotMachine(5);

        // Assert
        assertTrue(random.ok());
        assertEquals(5, random.configuration().length);
        assertEquals(5, random.symbols().length);
    }
}
