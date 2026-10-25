import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas de unidad del Ciclo 3.
 *
 * @author Juan David Guarguati Guarguati, Juan Pablo Suárez Rubiano
 * @version 5.0
 */
public class SlotMachineC3Test {

    @Test
    public void randomMachineShouldHaveNWheelsAndNSymbols() {
        // Act
        SlotMachine machine = new SlotMachine(5);

        // Assert
        assertTrue(machine.ok());
        assertEquals(5, machine.configuration().length);
        assertEquals(5, machine.symbols().length);
    }

    @Test
    public void randomMachineShouldAcceptTheLimits() {
        // Act
        SlotMachine small = new SlotMachine(3);
        SlotMachine big = new SlotMachine(50);

        // Assert
        assertTrue(small.ok());
        assertEquals(3, small.configuration().length);
        assertTrue(big.ok());
        assertEquals(50, big.symbols().length);
    }

    @Test
    public void randomMachineShouldRejectNOutOfRange() {
        // Act
        SlotMachine tooSmall = new SlotMachine(2);
        SlotMachine tooBig = new SlotMachine(51);

        // Assert
        assertFalse(tooSmall.ok());
        assertEquals(0, tooSmall.configuration().length);
        assertFalse(tooBig.ok());
        assertEquals(0, tooBig.configuration().length);
    }

    @Test
    public void randomMachineShouldHaveDifferentColors() {
        // Act
        SlotMachine machine = new SlotMachine(10);

        // Assert
        String[] colors = machine.symbols();
        for (int i = 0; i < colors.length; i++) {
            for (int j = i + 1; j < colors.length; j++) {
                assertNotEquals(colors[i], colors[j]);
            }
        }
    }

    @Test
    public void spinWithStepsShouldBeCyclic() {
        // Arrange
        SlotMachine machine = new SlotMachine(4);
        String before = machine.configuration()[0];

        // Act
        machine.spin(1, 4);

        // Assert
        assertEquals(before, machine.configuration()[0]);
        assertTrue(machine.ok());
    }

    @Test
    public void spinWithStepsShouldMoveToTheNextSymbol() {
        // Arrange
        SlotMachine machine = new SlotMachine();
        machine.addWheel(1);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");

        // Act
        machine.spin(1, 2);

        // Assert
        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void spinWithNegativeStepsShouldFail() {
        // Arrange
        SlotMachine machine = new SlotMachine(3);

        // Act
        machine.spin(1, -1);

        // Assert
        assertFalse(machine.ok());
    }

    @Test
    public void distinctSymbolsShouldCountVisibleColors() {
        // Arrange
        SlotMachine machine = new SlotMachine();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.spin(new String[]{"red", "blue", "red"});

        // Act
        int distinct = machine.distinctSymbols();

        // Assert
        assertEquals(2, distinct);
    }

    @Test
    public void distinctSymbolsShouldBeOneAtJackpot() {
        // Arrange
        SlotMachine machine = new SlotMachine();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.spin(new String[]{"blue", "blue"});

        // Act
        int distinct = machine.distinctSymbols();

        // Assert
        assertEquals(1, distinct);
        assertTrue(machine.isJackpot());
    }
}
