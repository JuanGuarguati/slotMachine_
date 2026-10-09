import org.junit.Test;
import static org.junit.Assert.*;
import javax.swing.JOptionPane;

/**
 * Pruebas de aceptación del Ciclo 4, preparadas para la presentación.
 * Cada prueba muestra el simulador, ejecuta un escenario paso a paso con
 * pausas y al final le pregunta al usuario si lo que vio es correcto.
 * La prueba pasa solo si el usuario responde "Sí".
 *
 * Se ejecutan con pantalla (no sirven en modo invisible).
 *
 * @version 1.0 (Ciclo 4)
 */
public class SlotMachineAcceptanceTest {

    /** Pausa entre pasos del escenario, en milisegundos. */
    private static final int PAUSE = 1500;

    /**
     * Escenario 1: tipos de ruedas.
     * Se crean las cuatro ruedas (normal, lefty, rebel y reverse) y se
     * comprueba a la vista que:
     * 1. Cada tipo tiene una marca diferente debajo: línea gris (normal),
     *    cuadrado azul a la izquierda (lefty), barra roja (rebel) y
     *    cuadrado verde a la derecha (reverse).
     * 2. La lefty, al girar, copia el color de la rueda de su izquierda.
     * 3. La rebel no se deja bloquear ni eliminar (aparece un mensaje de
     *    error en cada intento).
     * 4. La reverse gira hacia atrás: de rojo pasa a verde, no a azul.
     */
    @Test
    public void wheelTypesShouldBehaveAndLookDifferent() {
        // Arrange
        SlotMachine machine = new SlotMachine();
        machine.addWheel("normal", 1);
        machine.addWheel("lefty", 2);
        machine.addWheel("rebel", 3);
        machine.addWheel("reverse", 4);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "blue");
        machine.addSymbol(3, "green");
        machine.spin(new String[]{"green", "red", "blue", "red"});
        machine.makeVisible();
        info("Ruedas: normal, lefty, rebel y reverse.\n"
            + "Observe la marca de cada una debajo del marco.");

        // Act
        info("La lefty (rueda 2) va a girar: debe copiar el verde de la rueda 1.");
        machine.spin(2, 1);
        pause();
        info("Se intentará bloquear y luego eliminar la rebel (rueda 3).\n"
            + "Deben salir dos mensajes de error.");
        machine.lock(3);
        machine.delWheel(3);
        info("La reverse (rueda 4) está en rojo y gira 1 paso:\n"
            + "debe quedar en verde (hacia atrás), no en azul.");
        machine.spin(4, 1);
        pause();

        // Assert
        boolean correct = ask("¿La lefty copió el verde, la rebel siguió en su sitio\n"
            + "y la reverse quedó en verde?");
        machine.makeInvisible();
        assertTrue(correct);
    }

    /**
     * Escenario 2: tipos de símbolos.
     * Se crean tres ruedas con un símbolo de cada tipo y se comprueba a la
     * vista que:
     * 1. Cada tipo tiene una figura diferente: cuadrado (normal), círculo
     *    (ephemeral) y triángulo (shy).
     * 2. El ephemeral se encoge en cada giro hasta quedar como un punto.
     * 3. El shy se esconde cuando lo seleccionan y reaparece cuando lo
     *    vuelven a seleccionar.
     */
    @Test
    public void symbolTypesShouldBehaveAndLookDifferent() {
        // Arrange
        SlotMachine machine = new SlotMachine();
        machine.addWheel(1);
        machine.addWheel(2);
        machine.addWheel(3);
        machine.addSymbol("normal", 1, "red");
        machine.addSymbol("ephemeral", 2, "blue");
        machine.addSymbol("shy", 3, "green");
        machine.spin(new String[]{"red", "blue", "red"});
        machine.makeVisible();
        info("Rueda 1: cuadrado rojo (normal).\n"
            + "Rueda 2: círculo azul (ephemeral).\n"
            + "Rueda 3: cuadrado rojo; el triángulo verde (shy) está en esa rueda.");

        // Act
        info("La rueda 2 dará 8 giros completos:\n"
            + "el círculo azul debe encogerse hasta quedar como un punto.");
        for (int i = 0; i < 8; i++) {
            machine.spin(2, 3);
        }
        pause();
        info("La rueda 3 girará 2 pasos hasta el shy:\n"
            + "al ser seleccionado se ESCONDE (el espacio queda vacío).");
        machine.spin(3, 2);
        pause();
        info("La rueda 3 dará una vuelta completa y volverá al shy:\n"
            + "al ser seleccionado otra vez debe REAPARECER el triángulo.");
        machine.spin(3, 3);
        pause();

        // Assert
        boolean correct = ask("¿El círculo quedó como un punto y el triángulo\n"
            + "se escondió y luego reapareció?");
        machine.makeInvisible();
        assertTrue(correct);
    }

    /**
     * Muestra una instrucción al usuario.
     * @param message Texto a mostrar.
     */
    private void info(String message) {
        JOptionPane.showMessageDialog(null, message, "Prueba de aceptación",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Pregunta al usuario si el resultado observado es correcto.
     * @param question Pregunta a mostrar.
     * @return true si el usuario respondió "Sí".
     */
    private boolean ask(String question) {
        int answer = JOptionPane.showConfirmDialog(null, question,
            "Prueba de aceptación", JOptionPane.YES_NO_OPTION);
        return answer == JOptionPane.YES_OPTION;
    }

    /**
     * Pausa el escenario para que el usuario alcance a ver el resultado.
     */
    private void pause() {
        try {
            Thread.sleep(PAUSE);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
