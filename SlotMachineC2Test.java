import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Casos de prueba de unidad para las nuevas funcionalidades
 * del Ciclo 2: swap, lock/unlock, spin sobrecargado,
 * placeSymbol, symbols y distinctSymbols.
 */
public class SlotMachineC2Test {

    private SlotMachine machine;

    @Before
    public void setUp() {
        // Arrange: máquina base con 3 ruedas y 2 colores en cada una
        machine = new SlotMachine();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
    }

    @Test
    public void swapShouldExchangeWheelPositions() {
        // Arrange
        machine.spin(new String[]{"red", "blue", "red"});

        // Act
        machine.swap(1, 2);

        // Assert
        String[] config = machine.configuration();
        assertEquals("blue", config[0]);
        assertEquals("red", config[1]);
    }

    @Test
    public void swapShouldFailWithLessThanTwoWheels() {
        // Arrange
        SlotMachine small = new SlotMachine();
        small.addWheel(1);

        // Act
        small.swap(1, 1);

        // Assert
        assertFalse(small.ok());
    }

    @Test
    public void lockShouldPreventWheelFromChangingOnSpin() {
        // Arrange
        machine.placeSymbol(1, "red");
        machine.lock(1);

        // Act
        machine.spin();

        // Assert
        assertEquals("red", machine.configuration()[0]);
    }

    @Test
    public void unlockShouldAllowWheelToSpinAgain() {
        // Arrange
        machine.lock(1);
        machine.unlock(1);

        // Act
        machine.spin(1);

        // Assert
        assertTrue(machine.ok());
    }

    @Test
    public void spinSingleWheelShouldNotAffectOtherWheels() {
        // Arrange
        machine.placeSymbol(2, "blue");

        // Act
        machine.spin(1);

        // Assert
        assertEquals("blue", machine.configuration()[1]);
    }

    @Test
    public void spinWithNegativeStepsShouldFail() {
        // Act
        machine.spin(1, -3);

        // Assert
        assertFalse(machine.ok());
    }

    @Test
    public void spinWithGivenConfigurationShouldMatchExactly() {
        // Arrange
        String[] target = {"red", "blue", "red"};

        // Act
        machine.spin(target);

        // Assert
        assertArrayEquals(target, machine.configuration());
    }

    @Test
    public void spinWithWrongSizeConfigurationShouldFail() {
        // Arrange
        String[] wrongSize = {"red", "blue"};

        // Act
        machine.spin(wrongSize);

        // Assert
        assertFalse(machine.ok());
    }

    @Test
    public void placeSymbolShouldFailWithNonExistentColor() {
        // Act
        machine.placeSymbol(1, "green");

        // Assert
        assertFalse(machine.ok());
    }

    @Test
    public void symbolsShouldReturnAllRegisteredColors() {
        // Act
        String[] result = machine.symbols();

        // Assert
        assertEquals(2, result.length);
    }

    @Test
    public void distinctSymbolsShouldCountUniqueColorsOnly() {
        // Act
        int result = machine.distinctSymbols();

        // Assert
        assertEquals(2, result);
    }

    @Test
    public void isJackpotShouldBeTrueWhenAllWheelsMatch() {
        // Arrange
        machine.spin(new String[]{"red", "red", "red"});

        // Act
        boolean result = machine.isJackpot();

        // Assert
        assertTrue(result);
    }
}