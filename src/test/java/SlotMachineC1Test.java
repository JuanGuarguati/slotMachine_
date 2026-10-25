import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Pruebas de unidad del Ciclo 1.
 *
 * @author Juan David Guarguati Guarguati, Juan Pablo Suárez Rubiano
 * @version 5.0
 */
public class SlotMachineC1Test {

    private SlotMachine machine;

    @Before
    public void setUp() {
        // Arrange: máquina base con 2 ruedas y 2 colores en cada una
        machine = new SlotMachine();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
    }

    @Test
    public void newMachineShouldBeEmpty() {
        // Act
        SlotMachine empty = new SlotMachine();

        // Assert
        assertTrue(empty.ok());
        assertEquals(0, empty.configuration().length);
        assertEquals(0, empty.symbols().length);
    }

    @Test
    public void addWheelShouldIncreaseTheNumberOfWheels() {
        // Act
        machine.addWheel(3);

        // Assert
        assertTrue(machine.ok());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void addWheelShouldAdjustAnOutOfRangePosition() {
        // Act
        machine.addWheel(99);

        // Assert
        assertTrue(machine.ok());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void delWheelShouldDecreaseTheNumberOfWheels() {
        // Act
        machine.delWheel(1);

        // Assert
        assertTrue(machine.ok());
        assertEquals(1, machine.configuration().length);
    }

    @Test
    public void delWheelShouldFailWithoutWheels() {
        // Arrange
        SlotMachine empty = new SlotMachine();

        // Act
        empty.delWheel(1);

        // Assert
        assertFalse(empty.ok());
    }

    @Test
    public void addSymbolShouldRegisterTheColor() {
        // Act
        machine.addSymbol(3, "green");

        // Assert
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.symbols());
    }

    @Test
    public void addSymbolShouldRejectARepeatedColor() {
        // Act
        machine.addSymbol(1, "RED");

        // Assert
        assertFalse(machine.ok());
        assertEquals(2, machine.symbols().length);
    }

    @Test
    public void delSymbolShouldRemoveTheColor() {
        // Act
        machine.delSymbol("red");

        // Assert
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue"}, machine.symbols());
        assertArrayEquals(new String[]{"blue", "blue"}, machine.configuration());
    }

    @Test
    public void delSymbolShouldFailWithAnUnknownColor() {
        // Act
        machine.delSymbol("purple");

        // Assert
        assertFalse(machine.ok());
        assertEquals(2, machine.symbols().length);
    }

    @Test
    public void spinShouldLeaveARegisteredColorInEveryWheel() {
        // Act
        machine.spin();

        // Assert
        assertTrue(machine.ok());
        for (String color : machine.configuration()) {
            assertTrue("red".equals(color) || "blue".equals(color));
        }
    }

    @Test
    public void spinShouldFailWithoutWheels() {
        // Arrange
        SlotMachine empty = new SlotMachine();

        // Act
        empty.spin();

        // Assert
        assertFalse(empty.ok());
    }

    @Test
    public void configurationShouldShowWhiteInAWheelWithoutSymbols() {
        // Arrange
        SlotMachine noSymbols = new SlotMachine();
        noSymbols.addWheel(1);

        // Act
        String[] config = noSymbols.configuration();

        // Assert
        assertArrayEquals(new String[]{"white"}, config);
    }

    @Test
    public void isJackpotShouldBeTrueWhenAllColorsMatch() {
        // Arrange
        machine.spin(new String[]{"blue", "blue"});

        // Act
        boolean jackpot = machine.isJackpot();

        // Assert
        assertTrue(jackpot);
        assertTrue(machine.ok());
    }

    @Test
    public void isJackpotShouldBeFalseWhenColorsDiffer() {
        // Arrange
        machine.spin(new String[]{"red", "blue"});

        // Act
        boolean jackpot = machine.isJackpot();

        // Assert
        assertFalse(jackpot);
        assertTrue(machine.ok());
    }

    @Test
    public void isJackpotShouldFailWithLessThanTwoWheels() {
        // Arrange
        machine.delWheel(1);

        // Act
        boolean jackpot = machine.isJackpot();

        // Assert
        assertFalse(jackpot);
        assertFalse(machine.ok());
    }

    @Test
    public void isJackpotShouldFailWhenAWheelHasNoSymbols() {
        // Arrange
        SlotMachine noSymbols = new SlotMachine();
        noSymbols.addWheel(1);
        noSymbols.addWheel(2);

        // Act
        boolean jackpot = noSymbols.isJackpot();

        // Assert
        assertFalse(jackpot);
        assertFalse(noSymbols.ok());
    }

    @Test
    public void makeInvisibleAndExitShouldKeepTheMachineWorking() {
        // Act
        machine.makeInvisible();
        machine.exit();

        // Assert
        assertTrue(machine.ok());
        assertEquals(2, machine.configuration().length);
    }
}
