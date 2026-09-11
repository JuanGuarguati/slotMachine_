import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Clase de pruebas de unidad compartida entre los equipos del curso.
 * Cada autor aporta un mínimo de dos casos de prueba, identificados
 * Autores Juan Pablo Suárez Rubiano, Juan David Guarguati Guarguati (GgSr)
 */
public class SlotMachineCC2Test {

    /**
     * Qué debería hacer: al intercambiar dos ruedas con swap(),
     * sus configuraciones visibles deben quedar cruzadas.
     */
    @Test
    public void accordingGgSrShouldSwapWheelsCorrectly() {
        // Arrange
        SlotMachine machine = new SlotMachine();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "green");
        machine.spin(new String[]{"red", "green"});

        // Act
        machine.swap(1, 2);

        // Assert
        assertEquals("green", machine.configuration()[0]);
        assertEquals("red", machine.configuration()[1]);
    }

    /**
     * Qué no debería hacer: una rueda fijada con lock() no debe
     * cambiar de color al ejecutar spin().
     */
    @Test
    public void accordingGgSrShouldNotAllowLockedWheelToChangeColor() {
        // Arrange
        SlotMachine machine = new SlotMachine();
        machine.addWheel(1);
        machine.addSymbol(1, "red");
        machine.placeSymbol(1, "red");
        machine.lock(1);

        // Act
        machine.spin();

        // Assert
        assertEquals("red", machine.configuration()[0]);
    }
}