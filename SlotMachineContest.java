import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Clase controladora para solucionar el problema de la maratón Slot Machine.
 * Implementa un algoritmo de búsqueda sistemática a ciegas (odómetro).
 * 
 * @version 3.0 (2026-09)
 */
public class SlotMachineContest {

    /**
     * Requisito 14: Solucionar el problema de la maratón.
     * La máquina permanece invisible durante el cálculo.
     * @param n Tamaño de la máquina (n ruedas y n símbolos).
     * @return Secuencia de acciones {i, j} ejecutadas para ganar (rueda,
     *         pasos), en el mismo orden en que se aplicaron. Arreglo
     *         vacío si n es inválido, si la máquina ya inicia en Jackpot,
     *         o si se alcanzó el tope de seguridad sin encontrar Jackpot
     *         (caso extremo, no debería ocurrir salvo n muy grande).
     */
    public int[][] solve(int n) {
        SlotMachine tool = new SlotMachine(n);
        if (n <= 0 || !tool.ok()) {
            return new int[0][];
        }

        ArrayList<int[]> actions = new ArrayList<>();
        int[] counters = new int[n];
        long maxIterations = maxIterations(n);
        long iterations = 0;

        while (tool.distinctSymbols() != 1) {
            if (iterations >= maxIterations) {
                System.out.println("solve(" + n + "): se alcanzó el tope de seguridad ("
                        + maxIterations + " incrementos) sin encontrar Jackpot.");
                return new int[0][];
            }
            advanceOdometer(tool, counters, actions);
            iterations++;
        }

        return actions.toArray(new int[0][]);
    }

    /**
     * Requisito 15: Simular visualmente la solución.
     * La máquina se hace visible antes de comenzar la búsqueda; cada
     * rotación se anima paso a paso (comportamiento ya incorporado en
     * spin(wheel, steps) cuando la máquina está visible).
     * @param n Tamaño de la máquina.
     */
    public void simulate(int n) {
        SlotMachine simulator = new SlotMachine(n);
        if (n <= 0 || !simulator.ok()) {
            return;
        }

        simulator.makeVisible();
        int[] counters = new int[n];
        long maxIterations = maxIterations(n);
        long iterations = 0;

        while (simulator.distinctSymbols() != 1) {
            if (iterations >= maxIterations) {
                JOptionPane.showMessageDialog(null,
                        "Se alcanzó el tope de seguridad (" + maxIterations
                                + " incrementos) sin llegar al Jackpot.\n"
                                + "Para     n = " + n + " el peor caso de la búsqueda por fuerza "
                                + "bruta es demasiado grande para animarlo.\nPrueba con un n "
                                + "más pequeño, o usa solve(n) (sin animación).",
                        "Slot Machine - Simulación detenida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            advanceOdometer(simulator, counters, null);
            iterations++;
        }
    }

    /**
     * Calcula el tope de seguridad de incrementos del odómetro: en el
     * peor caso hace falta recorrer casi todo el ciclo de n^n
     * configuraciones antes de garantizar encontrar una ganadora.
     * @param n Tamaño de la máquina.
     * @return El tope de iteraciones (n^n + 1).
     */
    private long maxIterations(int n) {
        return (long) Math.pow(n, n) + 1;
    }

    /**
     * Avanza el odómetro en una unidad: incrementa el contador de la
     * rueda 1; si esa rueda completa un ciclo (vuelve a 0), el acarreo
     * pasa a la siguiente rueda, y así sucesivamente. Cada incremento de
     * un contador se traduce en una llamada real a spin(wheel, 1).
     * @param machine  La máquina sobre la que se ejecutan las rotaciones.
     * @param counters Contador propio (nunca consultado a la máquina) del
     *                 desplazamiento acumulado de cada rueda.
     * @param actions  Lista donde se registran las acciones {i, j}
     *                 ejecutadas (para el resultado de solve()); puede
     *                 ser null si no se necesita conservar el historial
     *                 (como en simulate()).
     */
    private void advanceOdometer(SlotMachine machine, int[] counters, ArrayList<int[]> actions) {
        int n = counters.length;
        boolean carry = true;
        int i = 0;
        while (carry && i < n) {
            machine.spin(i + 1, 1);
            if (actions != null) {
                actions.add(new int[]{i + 1, 1});
            }
            counters[i] = (counters[i] + 1) % n;
            carry = (counters[i] == 0);
            i++;
        }
    }
}