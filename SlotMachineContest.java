import java.util.ArrayList;

/**
 * Clase controladora para solucionar el problema de la maratón Slot Machine.
 *
 * Respeta los requisitos de diseño del Ciclo 3: de SlotMachine solo usa,
 * como testing tool, SlotMachine(n), spin(wheel, steps) y
 * distinctSymbols(); y como simulador, makeVisible(). Nunca consulta los
 * colores de la máquina: trabaja "a ciegas", como en la maratón.
 *
 * Estrategia determinística: la rueda 1 es la referencia. Cada una de las
 * demás ruedas se sincroniza con ella buscando el desplazamiento relativo
 * que la deja mostrando el mismo color. Al terminar, todas las ruedas
 * muestran el color de la rueda 1.
 *
 * @version 4.0 (2026-10)
 */
public class SlotMachineContest {

    /**
     * Requisito 14: Solucionar el problema de la maratón.
     * La máquina permanece invisible durante el cálculo.
     * @param n Tamaño de la máquina (n ruedas y n símbolos).
     * @return Secuencia de acciones {i, j} ejecutadas para ganar (rueda,
     *         pasos), en el mismo orden en que se aplicaron. Arreglo
     *         vacío si n es inválido.
     */
    public int[][] solve(int n) {
        SlotMachine tool = new SlotMachine(n);
        if (n <= 0 || !tool.ok()) {
            return new int[0][];
        }
        ArrayList<int[]> actions = new ArrayList<>();
        alignWithReference(tool, n, actions);
        return actions.toArray(new int[0][]);
    }

    /**
     * Requisito 15: Simular visualmente la solución.
     * La máquina se hace visible antes de empezar; cada paso de
     * spin(wheel, steps) se anima. Cuando todas las ruedas muestran el
     * mismo color, la máquina pone el fondo verde de Jackpot.
     * @param n Tamaño de la máquina.
     */
    public void simulate(int n) {
        SlotMachine simulator = new SlotMachine(n);
        if (n <= 0 || !simulator.ok()) {
            return;
        }
        simulator.makeVisible();
        alignWithReference(simulator, n, null);
    }

    /**
     * Sincroniza las ruedas 2..n con la rueda 1 (la referencia). La rueda
     * 1 nunca queda movida: solo gira vueltas completas, así que conserva
     * su color original, que es el color final de todas las ruedas.
     * @param machine La máquina a resolver.
     * @param n       Cantidad de ruedas (y de símbolos).
     * @param actions Lista donde se registran las acciones {i, j}; puede
     *                ser null si no se necesita el historial.
     */
    private void alignWithReference(SlotMachine machine, int n, ArrayList<int[]> actions) {
        for (int wheel = 2; wheel <= n; wheel++) {
            int offset = findOffsetToReference(machine, n, wheel, actions);
            if (offset > 0) {
                spin(machine, wheel, offset, actions);
            }
        }
    }

    /**
     * Encuentra cuántos pasos debe girar la rueda `wheel` para mostrar el
     * mismo color que la rueda 1, usando solo distinctSymbols().
     *
     * Para cada desplazamiento relativo d (0..n-1) entre la rueda `wheel`
     * y la rueda 1, se giran ambas juntas una vuelta completa (n pasos)
     * sumando distinctSymbols() en cada paso. Si d es el correcto, las dos
     * ruedas muestran siempre el mismo color y nunca aportan un color
     * extra; si d es incorrecto, muestran colores distintos y en algún
     * paso la rueda `wheel` aporta un color que ninguna otra rueda tiene
     * (las otras n-2 ruedas no pueden cubrir los n colores). Por eso la
     * suma mínima corresponde siempre, y solo, al d correcto.
     *
     * Al terminar, la rueda 1 y la rueda `wheel` quedan en su posición
     * inicial (cada una dio vueltas completas).
     * @param machine La máquina.
     * @param n       Cantidad de ruedas (y de símbolos).
     * @param wheel   Rueda a sincronizar (2..n).
     * @param actions Historial de acciones; puede ser null.
     * @return Pasos (0..n-1) que debe girar `wheel` para igualar a la rueda 1.
     */
    private int findOffsetToReference(SlotMachine machine, int n, int wheel, ArrayList<int[]> actions) {
        int bestOffset = 0;
        int bestSum = Integer.MAX_VALUE;
        for (int d = 0; d < n; d++) {
            int sum = 0;
            for (int step = 0; step < n; step++) {
                sum += machine.distinctSymbols();
                spin(machine, 1, 1, actions);
                spin(machine, wheel, 1, actions);
            }
            if (sum < bestSum) {
                bestSum = sum;
                bestOffset = d;
            }
            spin(machine, wheel, 1, actions);
        }
        return bestOffset;
    }

    /**
     * Ejecuta spin(wheel, steps) sobre la máquina y, si se pidió, lo
     * registra como acción {wheel, steps}.
     */
    private void spin(SlotMachine machine, int wheel, int steps, ArrayList<int[]> actions) {
        machine.spin(wheel, steps);
        if (actions != null) {
            actions.add(new int[]{wheel, steps});
        }
    }
}
