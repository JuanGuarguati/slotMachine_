import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Clase de pruebas de unidad compartida entre los equipos del curso
 * (Ciclo 4). Cada equipo aporta un mínimo de dos casos de prueba.
 * Autores: Juan Pablo Suárez Rubiano, Juan David Guarguati Guarguati (GgSr)
 *
 * Las pruebas de otros equipos que usan solo la interfaz de SlotMachine
 * están activas. Las que dependen de las clases internas de otro equipo
 * (constructores y métodos que nuestro diseño no tiene) quedan comentadas
 * al final, con el motivo de por qué no pasan.
 *
 * @version 2.0 (Ciclo 4)
 */
public class SlotMachineCC4Test {

    /** Máquina vacía (pruebas de RojasH y CuervoC). */
    private SlotMachine slotMachine;

    /** Máquina con ruedas normal, lefty y rebel (pruebas de Gomez-Rojas). */
    private SlotMachine machine;

    @Before
    public void setUp() {
        slotMachine = new SlotMachine();

        // Gomez-Rojas: normal, lefty, rebel, cada una con 3 símbolos distintos
        machine = new SlotMachine();
        String[] types = {"normal", "lefty", "rebel"};
        for (int w = 1; w <= 3; w++) {
            machine.addWheel(types[w - 1], w);
            machine.addSymbol(1, "red");
            machine.addSymbol(2, "green");
            machine.addSymbol(3, "blue");
        }
    }

    @After
    public void clean() {
        machine.exit();
    }

    // ------------------------------------------------------------------
    // GgSr (Suárez - Guarguati)
    // ------------------------------------------------------------------

    /**
     * Qué debería hacer: una rueda lefty, al girar, debe quedar mostrando
     * el mismo color que la rueda de su izquierda.
     */
    @Test
    public void accordingGgSrLeftyWheelShouldCopyItsLeftWheel() {
        // Arrange
        SlotMachine m = new SlotMachine();
        m.addWheel("normal", 1);
        m.addWheel("lefty", 2);
        m.addSymbol(1, "red");
        m.addSymbol(2, "blue");
        m.spin(new String[]{"blue", "red"});

        // Act
        m.spin(2, 1);

        // Assert
        assertEquals("blue", m.configuration()[1]);
    }

    /**
     * Qué no debería hacer: una rueda rebel no se debe dejar eliminar.
     */
    @Test
    public void accordingGgSrShouldNotDeleteRebelWheel() {
        // Arrange
        SlotMachine m = new SlotMachine();
        m.addWheel("rebel", 1);

        // Act
        m.delWheel(1);

        // Assert
        assertFalse(m.ok());
        assertEquals(1, m.configuration().length);
    }

    /**
     * Qué no debería hacer: una rueda rebel no se debe dejar bloquear.
     */
    @Test
    public void accordingGgSrShouldNotLockRebelWheel() {
        // Arrange
        SlotMachine m = new SlotMachine();
        m.addWheel("rebel", 1);

        // Act
        int locked = m.lock(1);

        // Assert
        assertEquals(-1, locked);
        assertFalse(m.ok());
    }

    /**
     * Qué debería hacer: un símbolo ephemeral debe encogerse con cada giro
     * hasta quedar como un punto.
     */
    @Test
    public void accordingGgSrEphemeralSymbolShouldEndAsAPoint() {
        // Arrange
        EphemeralSymbol symbol = new EphemeralSymbol("red", 50, 100);

        // Act
        for (int i = 0; i < 30; i++) {
            symbol.spun();
        }

        // Assert
        assertTrue(symbol.isPoint());
    }

    // ------------------------------------------------------------------
    // RojasH
    // ------------------------------------------------------------------

    /**
     * Verifica que se puedan agregar simbolos de los tres tipos.
     */
    @Test
    public void shouldAddSymbolsOfEveryType() {
        slotMachine.addSymbol("normal", 1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol("shy", 3, "green");
        assertTrue(slotMachine.ok());
        assertEquals(3, slotMachine.symbols().length);
    }

    /**
     * Verifica que la rueda rebel no se deje bloquear, intercambiar
     * ni eliminar, y que la maquina quede igual despues de intentarlo.
     */
    @Test
    public void shouldRebelWheelRefuseLockSwapAndDelete() {
        slotMachine.addSymbol(1, "red");
        slotMachine.addSymbol(2, "blue");
        slotMachine.addWheel("rebel", 1);
        slotMachine.addWheel(2);
        slotMachine.placeSymbol(1, "red");
        slotMachine.placeSymbol(2, "blue");
        slotMachine.lock(1);
        assertFalse(slotMachine.ok());
        slotMachine.swap(1, 2);
        assertFalse(slotMachine.ok());
        slotMachine.delWheel(1);
        assertFalse(slotMachine.ok());
        assertEquals(2, slotMachine.configuration().length);
        assertEquals("red", slotMachine.configuration()[0]);
        assertEquals("blue", slotMachine.configuration()[1]);
    }

    /**
     * Verifica que la maquina funcione con los tres tipos de rueda y los
     * tres tipos de simbolo a la vez: todos los giros son exitosos y la
     * lefty copia siempre a la rueda de su izquierda.
     */
    @Test
    public void shouldSpinMachineWithEveryWheelAndSymbolType() {
        slotMachine.addSymbol("normal", 1, "red");
        slotMachine.addSymbol("ephemeral", 2, "blue");
        slotMachine.addSymbol("shy", 3, "green");
        slotMachine.addWheel("normal", 1);
        slotMachine.addWheel("lefty", 2);
        slotMachine.addWheel("rebel", 3);
        for (int i = 0; i < 10; i++) {
            slotMachine.spin();
            assertTrue(slotMachine.ok());
            String[] config = slotMachine.configuration();
            assertEquals(config[0], config[1]);
        }
    }

    // ------------------------------------------------------------------
    // Grupo 6: CañonA - PaezP
    // ------------------------------------------------------------------

    @Test
    public void acordingCaPpShouldNotSwapRebelWheel() {
        SlotMachine m = new SlotMachine();
        m.addWheel("normal", 1);
        m.addWheel("rebel", 2);

        m.swap(1, 2);

        assertFalse(m.ok());
    }

    @Test
    public void acordingCaPpShouldNotDeleteRebelWheelbutShouldDeleteNormalWheel() {
        SlotMachine m = new SlotMachine();
        m.addWheel("normal", 1);
        m.addWheel("rebel", 2);

        m.delWheel(2);

        assertFalse(m.ok());
        assertEquals(2, m.configuration().length);

        m.delWheel(1);
        assertTrue(m.ok());
        assertEquals(1, m.configuration().length);
    }

    // ------------------------------------------------------------------
    // Grupo 4: Gomez - Rojas (usan la máquina "machine" del setUp)
    // ------------------------------------------------------------------

    @Test
    public void gomRojLeftyDeberiaCopiarALaRuedaNormalDeSuIzquierda() {
        machine.placeSymbol(1, "blue");
        machine.spin(2);
        assertTrue(machine.ok());
        assertEquals("blue", machine.configuration()[1]);
    }

    @Test
    public void gomRojRebelNoDeberiaBloquearseIntercambiarseNiEliminarse() {
        String[] original = machine.configuration();
        machine.lock(3);
        assertFalse(machine.ok());
        machine.swap(3, 1);
        assertFalse(machine.ok());
        machine.delWheel(3);
        assertFalse(machine.ok());
        assertArrayEquals(original, machine.configuration());
        assertEquals(3, machine.configuration().length);
    }

    @Test
    public void gomRojShyInvisibleNoDeberiaAportarColorAlJackpot() {
        SlotMachine shyMachine = new SlotMachine();
        for (int w = 1; w <= 2; w++) {
            shyMachine.addWheel(w);
            shyMachine.addSymbol("shy", 1, "red");
        }
        assertTrue(shyMachine.isJackpot());
        shyMachine.placeSymbol(2, "red");
        assertFalse(shyMachine.isJackpot());
        assertEquals("", shyMachine.configuration()[1]);
        shyMachine.exit();
    }

    // ------------------------------------------------------------------
    // Grupo 2: Davila - Orozco
    // ------------------------------------------------------------------

    @Test
    public void accordingToDoOLrebelWheelRejectsLockDeleteAndSwap() {
        SlotMachine m = new SlotMachine(3);
        m.addWheel("rebel", 4);
        assertTrue(m.ok());
        m.lock(4);
        assertFalse(m.ok());
        m.delWheel(4);
        assertFalse(m.ok());
        m.swap(1, 4);
        assertFalse(m.ok());
    }

    @Test
    public void accordingToDoOLleftyWheelCopiesItsLeftNeighbour() {
        SlotMachine m = new SlotMachine(3);
        m.addWheel("lefty", 4);
        m.spin();
        String[] shown = m.configuration();
        assertEquals(shown[2], shown[3]);
    }

    // ------------------------------------------------------------------
    // Grupo 6: CuervoC - InfanteC
    // ------------------------------------------------------------------

    /**
     * Verifica que no se pueda agregar un simbolo de un tipo inexistente.
     */
    @Test
    public void accordingCcIcshouldNotAddSymbolWithUnknownType() {
        slotMachine.addWheel(1);

        slotMachine.addSymbol("giant", 1, "red");

        assertFalse(slotMachine.ok());
        assertArrayEquals(new String[]{"white"}, slotMachine.configuration());
    }

    // ==================================================================
    // PRUEBAS COMENTADAS: no pasan con nuestro diseño
    // ==================================================================

    /*
     * BustosL - GomezG: shouldDecreaseEphemeralSymbolSize
     *
     * Por qué no pasa: usa el constructor EphemeralSymbol(String, String)
     * y los métodos getSize() y onWheelSpin(). En nuestro diseño el
     * constructor es EphemeralSymbol(color, x, y), y el método del giro se
     * llama spun(). La idea que prueba (que el ephemeral se encoja en cada
     * giro) sí la cumplimos; la probamos en
     * accordingGgSrEphemeralSymbolShouldEndAsAPoint.
     *
    @Test
    public void shouldDecreaseEphemeralSymbolSize() {
        EphemeralSymbol symbol = new EphemeralSymbol("red", "red");
        int initialSize = symbol.getSize();
        symbol.onWheelSpin();
        assertTrue(symbol.getSize() < initialSize);
    }
     */

    /*
     * BustosL - GomezG: shouldAlternateShySymbolVisibility
     *
     * Por qué no pasa: usa el constructor ShySymbol(String, String) y los
     * métodos isShyVisible() y onSelected(). En nuestro diseño el
     * constructor es ShySymbol(color, x, y), y el método de la selección
     * se llama selected(). La idea que prueba (que el shy alterne al ser
     * seleccionado) sí la cumplimos; la prueba
     * gomRojShyInvisibleNoDeberiaAportarColorAlJackpot.
     *
    @Test
    public void shouldAlternateShySymbolVisibility() {
        ShySymbol symbol = new ShySymbol("blue", "blue");
        boolean initialState = symbol.isShyVisible();
        symbol.onSelected();
        assertNotEquals(initialState, symbol.isShyVisible());
        symbol.onSelected();
        assertEquals(initialState, symbol.isShyVisible());
    }
     */

    /*
     * CuervoC - InfanteC: accordingCcIcshouldShrinkEphemeralWhenItReachesTheWindow
     *
     * Por qué no pasa:
     * 1. Hace new Wheel() y new Symbol("black"). En nuestro diseño Wheel y
     *    Symbol son clases abstractas (cada tipo es una subclase), así que
     *    no se pueden instanciar.
     * 2. Usa addSymbolWheel, rotateOnce, getSymbol y getCurrentSize, que
     *    nuestras clases no tienen (usamos addSymbol, rotate,
     *    getVisibleSymbol).
     * 3. Espera un tamaño exacto de 40, que depende de los tamaños que
     *    eligió ese equipo.
     * 4. Choca con la prueba de BustosL: allá EphemeralSymbol recibe dos
     *    String y aquí uno solo, así que no se pueden cumplir las dos.
     *
    @Test
    public void accordingCcIcshouldShrinkEphemeralWhenItReachesTheWindow() {
        Wheel wheel = new Wheel();
        wheel.addSymbolWheel(new EphemeralSymbol("red"));
        wheel.addSymbolWheel(new Symbol("black"));
        wheel.rotateOnce();
        wheel.rotateOnce();
        assertEquals("red", wheel.getSymbol().getColor());
        assertEquals(40, ((EphemeralSymbol) wheel.getSymbol()).getCurrentSize());
    }
     */

    /*
     * CuervoC - InfanteC: accordingCcIcshouldHideShyWhenItIsSelectedInTheWheel
     *
     * Por qué no pasa: igual que la anterior, hace new Wheel() y
     * new Symbol("black") sobre clases abstractas, usa ShySymbol(String)
     * (nuestro constructor es ShySymbol(color, x, y)) y los métodos
     * addSymbolWheel, rotateOnce y getSymbol, que no existen en nuestro
     * diseño.
     *
    @Test
    public void accordingCcIcshouldHideShyWhenItIsSelectedInTheWheel() {
        Wheel wheel = new Wheel();
        wheel.addSymbolWheel(new ShySymbol("red"));
        wheel.addSymbolWheel(new Symbol("black"));
        wheel.rotateOnce();
        wheel.rotateOnce();
        assertEquals("red", wheel.getSymbol().getColor());
        assertTrue(((ShySymbol) wheel.getSymbol()).isHidden());
    }
     */
}
