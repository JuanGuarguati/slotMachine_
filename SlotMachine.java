import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import javax.swing.JOptionPane;
import shapes.Canvas;
import shapes.Rectangle;

/**
 * Simulador de Máquina Tragamonedas ajustado estrictamente a los métodos
 * asociados a los Requisitos Funcionales del laboratorio.
 *
 * La máquina es un ArrayList de ruedas; cada rueda es un ArrayList de
 * símbolos y cada símbolo es un Rectangle de color (el color ES el
 * símbolo). Detrás de las ruedas hay un Rectangle grande (el marco) de
 * color fijo que se pone verde cuando hay Jackpot. No modifica el fondo
 * del Canvas ni usa imágenes: todo se dibuja con Rectangle.
 * 
 * @version 6.0 
 */
public class SlotMachine {
    
    /**
     * Paleta de 50 colores distintos para poder crear máquinas de hasta
     * n = 50 (límite de la maratón). Los primeros son nombres de color y el
     * resto códigos hexadecimales "#RRGGBB". No incluye blanco porque no se
     * distinguiría sobre el lienzo blanco.
     */
    private static final String[] PALETTE = {
        "red", "blue", "green", "yellow", "purple", "orange", "cyan",
        "magenta", "black", "gray", "pink", "darkgray", "lightgray", "brown",
        "#8c0000", "#38518c", "#5b8c15", "#e600c9", "#5ce6ce", "#e68c22",
        "#3000bf", "#51bf4d", "#bf1d53", "#00588c", "#858c38", "#73158c",
        "#00e672", "#e6785c", "#2233e6", "#48bf00", "#bf4d99", "#1db9bf",
        "#8c6900", "#5e388c", "#158c29", "#e6001d", "#5c96e6", "#ade622",
        "#bf00bf", "#4dbf9d", "#bf601d", "#11008c", "#468c38", "#8c154c",
        "#00ade6", "#e6df5c", "#a422e6", "#00bf47", "#bf564d", "#1d3fbf"
    };

    /** Tamaño mínimo de la maratón (3 <= n <= 50). */
    private static final int MIN_N = 3;

    /** Tamaño máximo de la maratón (3 <= n <= 50). */
    private static final int MAX_N = 50;

    /** Separación horizontal, en píxeles, entre dos ejes consecutivos. */
    private static final int AXIS_GAP = 36;

    /** Color fijo del marco cuando no hay Jackpot. */
    private static final String FRAME_COLOR = "#d4c08a";

    /** Color del marco cuando hay Jackpot (ganador). */
    private static final String JACKPOT_COLOR = "#006400";

    private ArrayList<Wheel> wheels;
    private boolean isVisible;
    private boolean ok;
    private Rectangle frame;

    /**
     * Requisito 1: Crear una máquina tragamonedas vacía.
     */
    public SlotMachine() {
        this.wheels = new ArrayList<>();
        this.isVisible = false;
        this.ok = true;
        this.frame = new Rectangle();
        this.frame.moveHorizontal(-20);
        this.frame.moveVertical(40);
    }
    
    /**
     * Requisito 13 (Extensión): Crea una máquina de n ruedas y n símbolos,
     * inicializada aleatoriamente. Igual que en la maratón, n debe estar
     * entre 3 y 50; si no, la máquina queda vacía y ok() es false.
     * @param n Cantidad de ruedas y símbolos distintos (3 <= n <= 50).
     */
    public SlotMachine(int n) {
        this(); 
        if (n < MIN_N || n > MAX_N) {
            this.ok = false;
            return;
        }

        for (int i = 1; i <= n; i++) {
            this.addWheel(i);
        }

        for (int i = 0; i < n; i++) {
            this.addSymbol(1, PALETTE[i]);
        }

        java.util.Random rand = new java.util.Random();
        for (int i = 1; i <= n; i++) {
            int randomSteps = rand.nextInt(n * 3); 
            this.spin(i, randomSteps); 
        }
        
        this.ok = true;
    }
        
    /**
     * Requisito 2a: Adicionar una rueda.
     * @param pos Posición de inserción (base 1).
     */
    public void addWheel(int pos) {
        int index = adjustPos(pos, wheels.size() + 1) - 1;
        Wheel newWheel = new Wheel(0);
        wheels.add(index, newWheel);

        if (this.isVisible) {
            newWheel.makeVisible();
        }
        
        recalculateAxes(); 
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 2b: Eliminar una rueda.
     * @param pos Posición de la rueda a eliminar (base 1).
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para eliminar.");
            return;
        }
        int index = adjustPos(pos, wheels.size()) - 1;
        Wheel removed = wheels.remove(index);

        removed.makeInvisible();
        for (Symbol s : removed.getSymbols()) {
            s.makeInvisible();
        }
        
        recalculateAxes();
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 10a: Fijar una rueda para que no gire.
     * @param wheel Posición de la rueda a fijar (base 1).
     * @return La posición (base 1, ya ajustada) del eje que quedó
     *         bloqueado, o -1 si no había ruedas para fijar.
     */
    public int lock(int wheel) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para fijar.");
            return -1;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        wheels.get(index).lock();
        this.ok = true;
        return index + 1;
    }
    
    /**
     * Requisito 10b: Soltar una rueda previamente fijada.
     * @param wheel Posición de la rueda a soltar (base 1).
     */
    public void unlock(int wheel) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para soltar.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        wheels.get(index).unlock();
        this.ok = true;
    }
    
    /**
     * Requisito 3a: Adicionar un símbolo. Las ruedas bloqueadas se omiten:
     * su lista de colores no se modifica mientras estén lock().
     * @param pos Posición del símbolo en las ruedas.
     * @param color Nombre del color en estándar CSS.
     */
    public void addSymbol(int pos, String color) {
        if (wheels.isEmpty()) {
            notifyError("No existen ruedas para adicionar símbolos.");
            return;
        }
        for (Wheel wheel : wheels) {
            if (!wheel.isLocked()) {
                wheel.addSymbol(pos, color);
            }
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 3b: Eliminar un símbolo por su color. Las ruedas bloqueadas
     * se omiten.
     * @param symbol Color del símbolo a eliminar.
     */
    public void delSymbol(String symbol) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas registradas.");
            return;
        }
        for (Wheel wheel : wheels) {
            if (!wheel.isLocked()) {
                wheel.delSymbol(symbol);
            }
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 4: Girar ALEATORIAMENTE todas las ruedas de la máquina que
     * no estén bloqueadas (comportamiento de "tirón" de casino).
     */
    public void spin() {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para girar.");
            return;
        }
        for (Wheel w : wheels) {
            if (!w.isLocked()) {
                w.spin();
            }
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 11: Rotar ALEATORIAMENTE una sola rueda.
     * Rechazada si el eje está bloqueado.
     * @param wheel Posición de la rueda a girar (base 1).
     */
    public void spin(int wheel) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para girar.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        Wheel w = wheels.get(index);

        if (w.isLocked()) {
            notifyError("El eje " + (index + 1) + " está bloqueado y no puede modificarse.");
            return;
        }

        w.spin();
        this.ok = true;
        refreshVisibility();
    }
    
    /**
     * Requisito 11: Rotar una rueda un número determinado de pasos, de
     * forma DETERMINISTA y CÍCLICA (no aleatoria): equivale a "rotar la
     * rueda `wheel` por `steps` posiciones" tal como en la máquina física.
     * Si el simulador está visible, cada paso se muestra individualmente
     * con pausa (Requisito de Usabilidad 1, ciclo 2). Rechazada si el eje
     * está bloqueado. Si la máquina está visible, al terminar el giro se
     * consulta isJackpot(), que muestra el aviso de JACKPOT si se ganó.
     * @param wheel Posición de la rueda a girar (base 1).
     * @param steps Número de pasos a rotar (no puede ser negativo).
     */
    public void spin(int wheel, int steps) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para girar.");
            return;
        }
        if (steps < 0) {
            notifyError("El número de pasos no puede ser negativo.");
            return;
        }
        
        int index = adjustPos(wheel, wheels.size()) - 1;
        Wheel w = wheels.get(index);
        
        if (w.isLocked()) {
            notifyError("El eje " + (index + 1) + " está bloqueado y no puede modificarse.");
            return; 
        }

        for (int i = 0; i < steps; i++) {
            w.rotate(1);
            if (this.isVisible) {
                refreshVisibility();
                pause();
            }
        }
        refreshVisibility();
        if (this.isVisible) {
            isJackpot();
        }
        this.ok = true;
    }
    
    /**
     * Requisito 12: Dejar la máquina en una configuración dada de colores.
     * La operación es atómica: primero se valida todo (tamaño, bloqueos,
     * existencia de colores) y solo si todo es correcto se aplica.
     * @param setSymbols Arreglo con el color deseado para cada rueda.
     */
    public void spin(String[] setSymbols) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas registradas.");
            return;
        }
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            notifyError("El número de colores no coincide con el número de ruedas.");
            return;
        }

        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (w.isLocked()) {
                notifyError("El eje " + (i + 1) + " está bloqueado y no puede modificarse.");
                return;
            }
            if (!w.hasColor(setSymbols[i])) {
                notifyError("Una rueda no contiene el color indicado.");
                return;
            }
        }

        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).setVisibleColor(setSymbols[i]);
        }

        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 9: Intercambiar dos ruedas de posición manteniendo los ejes estáticos.
     * Rechazada si cualquiera de los dos ejes involucrados está bloqueado.
     * @param wheel1 Posición de la primera rueda (desde 1).
     * @param wheel2 Posición de la segunda rueda (desde 1).
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.size() < 2) {
            notifyError("Se necesitan al menos dos ruedas para intercambiar.");
            return;
        }
        
        if (wheel1 < 1 || wheel1 > wheels.size() || wheel2 < 1 || wheel2 > wheels.size()) {
            notifyError("Posiciones de rueda inválidas.");
            return;
        }

        int index1 = wheel1 - 1;
        int index2 = wheel2 - 1;

        if (index1 == index2) {
            this.ok = true;
            return;
        }

        Wheel w1 = wheels.get(index1);
        Wheel w2 = wheels.get(index2);

        if (w1.isLocked()) {
            notifyError("El eje " + wheel1 + " está bloqueado y no puede modificarse.");
            return;
        }
        if (w2.isLocked()) {
            notifyError("El eje " + wheel2 + " está bloqueado y no puede modificarse.");
            return;
        }

        w1.swapContentWith(w2);

        this.ok = true;
        refreshVisibility();
    }

    /**
     * Deja visible en una rueda específica el símbolo del color indicado.
     * Rechazada si el eje está bloqueado.
     * @param wheel Posición de la rueda (base 1).
     * @param symbol Color del símbolo que se desea dejar visible.
     */
    public void placeSymbol(int wheel, String symbol) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas registradas.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        Wheel w = wheels.get(index);

        if (w.isLocked()) {
            notifyError("El eje " + (index + 1) + " está bloqueado y no puede modificarse.");
            return;
        }

        boolean found = w.setVisibleColor(symbol);
        if (!found) {
            notifyError("La rueda no contiene ese color.");
            return;
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 5: Consultar los símbolos visibles de la máquina.
     * @return Arreglo con los colores de los símbolos visibles.
     */
    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            Symbol active = wheels.get(i).getVisibleSymbol();
            config[i] = (active != null) ? active.getColor() : "empty";
        }
        this.ok = true;
        return config;
    }

    /**
     * Consultar los colores de símbolos REGISTRADOS en la máquina (los
     * disponibles en cada rueda, no necesariamente los que se ven ahora).
     * @return Arreglo con los colores disponibles en la primera rueda.
     */
    public String[] symbols() {
        if (wheels.isEmpty()) {
            this.ok = false;
            return new String[0];
        }
        ArrayList<Symbol> list = wheels.get(0).getSymbols();
        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i).getColor();
        }
        this.ok = true;
        return result;
    }
    
    /**
     * Consultar la cantidad de colores DISTINTOS VISIBLES actualmente en
     * la máquina (equivale al valor "k" del problema de la maratón: "the
     * number of distinct symbols in the sequence she can currently see").
     * SlotMachineContest lo usa como medida para comparar posiciones, no
     * como condición de Jackpot (para eso está isJackpot()).
     * @return Número de colores visibles diferentes en este momento.
     */
    public int distinctSymbols() {
        Set<String> visible = new HashSet<>();
        for (Wheel w : wheels) {
            Symbol active = w.getVisibleSymbol();
            if (active != null) {
                visible.add(active.getColor().toLowerCase());
            }
        }
        this.ok = true;
        return visible.size();
    }

    /**
     * Requisito 6: Consultar si la configuración VISIBLE es la ganadora (Jackpot).
     * ok() queda en true si la consulta fue válida (>=2 ruedas, todas con
     * símbolo visible), sin importar si el resultado es true o false; queda
     * en false solo si la consulta no pudo evaluarse.
     * @return true si todos los símbolos visibles coinciden.
     */
    public boolean isJackpot() {
        if (wheels.size() < 2) {
            this.ok = false;
            return false;
        }
        for (Wheel wheel : wheels) {
            if (wheel.getVisibleSymbol() == null) {
                this.ok = false;
                return false;
            }
        }

        boolean jackpot = allSameColor();
        this.ok = true;

        if (jackpot && this.isVisible) {
            JOptionPane.showMessageDialog(null, "¡ESTADO GANADOR (JACKPOT)!", "Slot Machine", JOptionPane.INFORMATION_MESSAGE);
        }
        return jackpot;
    }

    /**
     * Requisito 7a: Hacer visible el simulador en pantalla.
     */
    public void makeVisible() {
        this.isVisible = true;
        for (Wheel wheel : wheels) {
            wheel.makeVisible();
        }
        refreshVisibility();
        this.ok = true;
    }

    /**
     * Requisito 7b: Hacer invisible el simulador.
     */
    public void makeInvisible() {
        this.isVisible = false;
        frame.makeInvisible();
        for (Wheel wheel : wheels) {
            wheel.makeInvisible();
        }
        this.ok = true;
    }

    /**
     * Requisito 8: Terminar el simulador.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }
    
    /**
     * Indica si se logró realizar la última operación exitosamente.
     * @return true si la última operación fue exitosa, false en caso de error.
     */
    public boolean ok() {
        return this.ok;
    }

    /**
     * Ajusta la posición de entrada para evitar índices fuera de rango.
     * @param pos La posición dada por el usuario.
     * @param max El tamaño máximo permitido.
     * @return La posición ajustada en base 1.
     */
    private int adjustPos(int pos, int max) {
        if (pos < 1) return 1;
        if (pos > max) return max;
        return pos;
    }

    /**
     * Actualiza la visualización: tamaño del lienzo, marco y símbolos. El
     * marco se dibuja primero y los símbolos después, para que los
     * cuadrados de color queden por encima del marco.
     */
    private void refreshVisibility() {
        if (this.isVisible) {
            ensureCanvasSize();
            updateFrame();
            for (Wheel wheel : wheels) {
                Symbol active = wheel.getVisibleSymbol();
                if (active != null) active.makeVisible();
            }
        }
    }

    /**
     * Garantiza que el Canvas sea lo suficientemente grande para mostrar
     * todas las ruedas actuales, sin importar cuántas sean.
     */
    private void ensureCanvasSize() {
        if (wheels.isEmpty()) return;
        int requiredWidth = 120 + (wheels.size() - 1) * AXIS_GAP;
        Canvas.getCanvas().ensureSize(requiredWidth, 250);
    }

    /**
     * Ajusta el marco para que cubra todas las ruedas y le pone el color
     * fijo, o verde si la configuración visible es Jackpot. Si no hay
     * ruedas, el marco se oculta.
     */
    private void updateFrame() {
        if (wheels.isEmpty()) {
            frame.makeInvisible();
            return;
        }
        int width = (wheels.size() - 1) * AXIS_GAP + 50;
        frame.changeSize(50, width);
        frame.changeColor(allSameColor() ? JACKPOT_COLOR : FRAME_COLOR);
        frame.makeVisible();
    }

    /**
     * Compara los colores visibles de todas las ruedas, sin efectos
     * secundarios (no cambia ok() ni muestra mensajes). La usan
     * isJackpot() y updateFrame().
     * @return true si hay al menos 2 ruedas y todas muestran el mismo color.
     */
    private boolean allSameColor() {
        if (wheels.size() < 2) return false;
        Symbol first = wheels.get(0).getVisibleSymbol();
        if (first == null) return false;
        for (Wheel wheel : wheels) {
            Symbol active = wheel.getVisibleSymbol();
            if (active == null || !first.getColor().equalsIgnoreCase(active.getColor())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Muestra un mensaje de error y marca el estado de la máquina como falso.
     * @param msg El mensaje de error a mostrar.
     */
    private void notifyError(String msg) {
        this.ok = false;
        if (this.isVisible) {
            JOptionPane.showMessageDialog(null, msg, "Error Slot Machine", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Alinea estructuralmente todos los ejes (ruedas) de la máquina.
     * Se debe llamar cada vez que la estructura de ejes cambie.
     */
    private void recalculateAxes() {
        for (int i = 0; i < wheels.size(); i++) {
            int correctX = 50 + (i * AXIS_GAP);
            wheels.get(i).updateAxisPosition(correctX);
        }
    }

    /**
     * Pausa la ejecución brevemente para animar los giros en el Canvas.
     * Se usa un valor bajo (25ms) porque spin(wheel,steps) puede
     * ejecutarse cientos o miles de veces seguidas durante simulate().
     */
    private void pause() {
        try {
            Thread.sleep(25); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}