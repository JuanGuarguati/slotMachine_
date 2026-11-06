import java.util.Arrays;
import java.util.Scanner;

/**
 * Demostración del SlotMachine del ciclo 4.
 * Muestra, paso a paso, todas las operaciones que se pueden hacer.
 * Después de cada paso espera a que se presione Enter en la consola,
 * para poder explicar lo que pasó en pantalla.
 */
public class Demo {

    private static final Scanner IN = new Scanner(System.in);
    private static SlotMachine sm;

    public static void main(String[] args) {
        titulo("1. Crear la máquina vacía y hacerla visible");
        sm = new SlotMachine();
        sm.makeVisible();
        estado();

        titulo("2. Agregar los 4 tipos de rueda: normal, lefty, reverse y rebel");
        sm.addWheel("normal", 1);
        sm.addWheel("lefty", 2);
        sm.addWheel("reverse", 3);
        sm.addWheel("rebel", 4);
        estado();

        titulo("3. Agregar los 3 tipos de símbolo: normal (cuadrado), ephemeral (círculo) y shy (triángulo)");
        sm.addSymbol("normal", 1, "red");
        sm.addSymbol("ephemeral", 2, "blue");
        sm.addSymbol("shy", 3, "green");
        estado();

        titulo("4. spin(): gira todas las ruedas al azar (la lefty copia a la normal)");
        sm.spin();
        estado();

        titulo("5. spin(1): gira solo la rueda 1 al azar");
        sm.spin(1);
        estado();

        titulo("6. spin(1, 1) y spin(3, 1): la normal avanza 1, la reverse retrocede 1");
        sm.placeSymbol(1, "red");
        sm.placeSymbol(3, "red");
        estado();
        sm.spin(1, 1);
        sm.spin(3, 1);
        estado();

        titulo("7. placeSymbol(1, \"green\"): el shy queda seleccionado y se esconde");
        sm.placeSymbol(1, "green");
        estado();
        titulo("   placeSymbol(1, \"green\") otra vez: el shy se vuelve a mostrar");
        sm.placeSymbol(1, "green");
        estado();

        titulo("8. Ephemeral: cada giro de su rueda el círculo azul se encoge");
        sm.placeSymbol(3, "blue");
        for (int i = 0; i < 4; i++) {
            sm.spin(3, 3);
        }
        estado();

        titulo("9. lock(1): la rueda 1 queda fija; spin() no la mueve");
        sm.placeSymbol(1, "red");
        sm.lock(1);
        sm.spin();
        estado();
        titulo("   unlock(1): la rueda 1 vuelve a girar");
        sm.unlock(1);
        estado();

        titulo("10. Rebel (rueda 4): no se deja bloquear, intercambiar ni eliminar (sale un error en cada uno)");
        sm.lock(4);
        System.out.println("   lock(4) -> ok = " + sm.ok());
        sm.swap(1, 4);
        System.out.println("   swap(1, 4) -> ok = " + sm.ok());
        sm.delWheel(4);
        System.out.println("   delWheel(4) -> ok = " + sm.ok());
        pausa();

        titulo("11. swap(1, 3): intercambia dos ruedas que sí se dejan");
        sm.swap(1, 3);
        estado();

        titulo("12. spin(String[]): poner todo en rojo -> JACKPOT (el marco se pone verde)");
        sm.spin(new String[]{"red", "red", "red", "red"});
        System.out.println("   isJackpot() = " + sm.isJackpot());
        estado();

        titulo("13. delSymbol(\"blue\") y delWheel(2): eliminar un símbolo y una rueda");
        sm.delSymbol("blue");
        sm.delWheel(2);
        estado();

        titulo("14. Errores: tipo de rueda y de símbolo que no existen, color repetido");
        sm.addWheel("xyz", 1);
        System.out.println("   addWheel(\"xyz\", 1) -> ok = " + sm.ok());
        sm.addSymbol("xyz", 1, "yellow");
        System.out.println("   addSymbol(\"xyz\", 1, \"yellow\") -> ok = " + sm.ok());
        sm.addSymbol(1, "red");
        System.out.println("   addSymbol(1, \"red\") -> ok = " + sm.ok());
        pausa();

        titulo("15. exit(): se oculta la máquina");
        sm.exit();
        estado();

        titulo("16. SlotMachine(5): máquina de 5 ruedas y 5 colores, al azar");
        sm = new SlotMachine(5);
        sm.makeVisible();
        estado();
        sm.makeInvisible();

        titulo("17. SlotMachineContest: solve(4) calcula los giros y simulate(4) los muestra");
        SlotMachineContest contest = new SlotMachineContest();
        int[][] acciones = contest.solve(4);
        System.out.println("   solve(4) = " + Arrays.deepToString(acciones) + "   ({rueda, pasos})");
        contest.simulate(4);
        pausa();

        System.out.println("\nFin de la demostración.");
        System.exit(0);
    }

    /** Imprime el título de un paso. */
    private static void titulo(String texto) {
        System.out.println("\n" + texto);
    }

    /** Imprime lo que se puede consultar de la máquina y espera Enter. */
    private static void estado() {
        System.out.println("   configuration()   = " + Arrays.toString(sm.configuration()));
        System.out.println("   symbols()         = " + Arrays.toString(sm.symbols()));
        System.out.println("   distinctSymbols() = " + sm.distinctSymbols());
        System.out.println("   ok()              = " + sm.ok());
        pausa();
    }

    /** Espera a que se presione Enter. */
    private static void pausa() {
        System.out.print("   [Enter para seguir]");
        if (IN.hasNextLine()) {
            IN.nextLine();
        }
    }
}
