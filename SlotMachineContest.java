import java.util.ArrayList;

/**
 * Clase controladora para solucionar el problema de la maratón Slot Machine.
 *
 * Respeta los requisitos de diseño del Ciclo 3: de SlotMachine solo usa,
 * como testing tool, SlotMachine(n), spin(wheel, steps) y
 * distinctSymbols(); y como simulador, makeVisible(). Nunca lee los
 * colores de la máquina: trabaja "a ciegas", como en la maratón.
 *
 * Estrategia determinística: la rueda 1 es la referencia (su color es el
 * color objetivo). Para cada una de las demás ruedas se calcula cuántos
 * pasos le faltan para mostrar ese mismo color, y luego se aplica
 * spin(wheel, steps) una sola vez por rueda.
 *
 * Cómo se calculan los pasos de una rueda: se prueba cada diferencia de
 * posición d (0..n-1) con la rueda 1. Con esa diferencia, ambas ruedas
 * giran juntas una vuelta completa (n pasos) y se suma distinctSymbols()
 * en cada paso. Con la d correcta las dos ruedas muestran siempre el mismo
 * color y la rueda nunca aporta un color extra; con una d incorrecta, en
 * algún paso aporta un color que ninguna otra rueda tiene (las otras n-2
 * ruedas no alcanzan a cubrir los n colores), así que la suma es mayor.
 * Por eso la suma mínima corresponde siempre, y solo, a la d correcta.
 * Como todo son vueltas completas, la máquina queda igual que al inicio.
 *
 * @version 6.0 (2026-10)
 */
public class SlotMachineContest {

    /** Tamaño mínimo de la maratón (3 <= n). */
    private static final int MIN_N = 3;

    /** Tamaño máximo de la maratón (n <= 50). */
    private static final int MAX_N = 50;

    /**
     * Requisito 14: Solucionar el problema de la maratón.
     * La máquina permanece invisible durante todo el proceso.
     * El tamaño se valida aquí, sin preguntarle ok() a la máquina.
     * @param n Tamaño de la máquina (n ruedas y n símbolos, 3 <= n <= 50).
     * @return Acciones {rueda, pasos} que llevan todas las ruedas al color
     *         de la rueda 1, en el orden en que se aplicaron. Arreglo
     *         vacío si n está fuera de 3..50.
     */
    public int[][] solve(int n) {
        if (n < MIN_N || n > MAX_N) {
            return new int[0][];
        }
        SlotMachine tool = new SlotMachine(n);

        // 1. Calcular los pasos de cada rueda 2..n respecto a la rueda 1.
        ArrayList<int[]> list = new ArrayList<>();
        for (int wheel = 2; wheel <= n; wheel++) {
            int bestSteps = 0;
            int bestSum = Integer.MAX_VALUE;
            for (int d = 0; d < n; d++) {
                int sum = 0;
                for (int step = 0; step < n; step++) {
                    sum += tool.distinctSymbols();
                    tool.spin(1, 1);
                    tool.spin(wheel, 1);
                }
                if (sum < bestSum) {
                    bestSum = sum;
                    bestSteps = d;
                }
                tool.spin(wheel, 1);
            }
            if (bestSteps > 0) {
                list.add(new int[]{wheel, bestSteps});
            }
        }
        int[][] actions = list.toArray(new int[0][]);

        // 2. Aplicar un solo spin(wheel, steps) por rueda.
        for (int[] action : actions) {
            tool.spin(action[0], action[1]);
        }
        return actions;
    }

    /**
     * Requisito 15: Simular visualmente la solución.
     * Primero se calculan los pasos con la máquina todavía invisible
     * (igual que en solve). Luego la máquina se hace visible y se ejecuta
     * cada spin(wheel, steps) a la vista. Al llegar al Jackpot, la
     * máquina lo anuncia con isJackpot().
     * @param n Tamaño de la máquina (3 <= n <= 50).
     */
    public void simulate(int n) {
        if (n < MIN_N || n > MAX_N) {
            return;
        }
        SlotMachine simulator = new SlotMachine(n);

        // 1. Calcular los pasos de cada rueda 2..n con la máquina invisible.
        ArrayList<int[]> actions = new ArrayList<>();
        for (int wheel = 2; wheel <= n; wheel++) {
            int bestSteps = 0;
            int bestSum = Integer.MAX_VALUE;
            for (int d = 0; d < n; d++) {
                int sum = 0;
                for (int step = 0; step < n; step++) {
                    sum += simulator.distinctSymbols();
                    simulator.spin(1, 1);
                    simulator.spin(wheel, 1);
                }
                if (sum < bestSum) {
                    bestSum = sum;
                    bestSteps = d;
                }
                simulator.spin(wheel, 1);
            }
            if (bestSteps > 0) {
                actions.add(new int[]{wheel, bestSteps});
            }
        }

        // 2. Mostrar la máquina y aplicar un spin(wheel, steps) por rueda.
        simulator.makeVisible();
        for (int[] action : actions) {
            simulator.spin(action[0], action[1]);
        }
    }
}
