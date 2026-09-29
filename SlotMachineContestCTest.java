import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Clase de pruebas de unidad compartida entre los equipos del curso.
 * Autores: Juan Pablo Suárez Rubiano, Juan David Guarguati Guarguati (GgSr)
 * 
 * @version 5.0 (2026-09) - actualizado porque solve() ahora retorna int[][].
 */
public class SlotMachineContestCTest {

    @Test
    public void accordingGgSrSolveShouldReturnNonNullActions() {
        // Arrange
        SlotMachineContest contest = new SlotMachineContest();

        // Act
        int[][] actions = contest.solve(3);

        // Assert
        assertNotNull(actions);
    }


    @Test   
    public void accordingGgSrSimulateShouldExecuteWithoutExceptions() {
        // Arrange
        SlotMachineContest contest = new SlotMachineContest();

        // Act & Assert
        try {
            contest.simulate(2);
            assertTrue(true); 
        } catch (Exception e) {
            fail("El método simulate arrojó una excepción inesperada: " + e.getMessage());
        }
    }
}