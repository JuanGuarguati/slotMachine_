import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Pruebas de unidad para la clase SlotMachineContest (Solver de la maratón).
 * 
 * @version 5.0 (2026-09) - solve() ahora retorna int[][] en vez de int.
 */
public class SlotMachineContestTest {

    private SlotMachineContest solver;

    @Before
    public void setUp() {
        solver = new SlotMachineContest();
    }

    @Test
    public void solveShouldReturnValidActionsForSmallMachine() {
        // Act
        int[][] actions = solver.solve(3);

        // Assert
        assertNotNull(actions);
        for (int[] action : actions) {
            assertEquals("Cada acción debe tener el formato {rueda, pasos}", 2, action.length);
            assertTrue(action[0] >= 1 && action[0] <= 3);
            assertTrue(action[1] >= 0);
        }
    }

    @Test
    public void solveShouldHandleLargerMachinesWithoutFailing() {
        // Act
        int[][] actions = solver.solve(4);

        // Assert
        assertNotNull(actions);
        for (int[] action : actions) {
            assertEquals(2, action.length);
            assertTrue(action[0] >= 1 && action[0] <= 4);
        }
    }

    @Test
    public void solveShouldReturnEmptyActionsForInvalidSize() {
        // Act
        int[][] actions = solver.solve(0);

        // Assert
        assertNotNull(actions);
        assertEquals(0, actions.length);
    }

    @Test
    public void solveShouldUseAtMostOneSpinPerWheel() {
        // Act
        int n = 5;
        int[][] actions = solver.solve(n);

        // Assert: cada rueda 2..n recibe como máximo un spin(wheel, steps)
        // con 1 <= steps < n; la rueda 1 (referencia) nunca se mueve.
        assertTrue(actions.length <= n - 1);
        for (int[] action : actions) {
            assertTrue(action[0] >= 2 && action[0] <= n);
            assertTrue(action[1] >= 1 && action[1] < n);
        }
    }

    @Test
    public void solveShouldAcceptTheMarathonLimits() {
        // Act
        int[][] smallest = solver.solve(3);
        int[][] largest = solver.solve(50);

        // Assert
        assertTrue(smallest.length <= 2);
        assertTrue(largest.length <= 49);
    }

    @Test
    public void solveShouldRejectSizesOutsideTheMarathonLimits() {
        // Act & Assert
        assertEquals(0, solver.solve(2).length);
        assertEquals(0, solver.solve(51).length);
    }
}
