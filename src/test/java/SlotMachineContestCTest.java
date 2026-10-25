import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas de unidad compartidas de SlotMachineContest.
 *
 * @author Juan David Guarguati Guarguati, Juan Pablo Suárez Rubiano
 * @version 5.0
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
            contest.simulate(3);
            assertTrue(true); 
        } catch (Exception e) {
            fail("El método simulate arrojó una excepción inesperada: " + e.getMessage());
        }
    }
}