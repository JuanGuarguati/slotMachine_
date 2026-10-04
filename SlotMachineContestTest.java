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
        int[][] actions = solver.solve(2);

        // Assert
        assertNotNull(actions);
        for (int[] action : actions) {
            assertEquals("Cada acción debe tener el formato {rueda, pasos}", 2, action.length);
            assertTrue(action[0] >= 1 && action[0] <= 2);
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
    public void solveShouldUseADeterministicNumberOfActions() {
        // Arrange: por cada rueda 2..n se prueban n desplazamientos, cada uno
        // con 2n giros más 1 giro para pasar al siguiente desplazamiento, y
        // al final como máximo 1 giro para dejarla alineada con la rueda 1.
        int n = 4;
        int scanActions = (n - 1) * n * (2 * n + 1);

        // Act
        int[][] actions = solver.solve(n);

        // Assert: no depende de una búsqueda al azar.
        assertTrue(actions.length >= scanActions);
        assertTrue(actions.length <= scanActions + (n - 1));
    }
}
