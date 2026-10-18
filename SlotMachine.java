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
 * La máquina es un ArrayList de ruedas y cada rueda es un ArrayList de
 * símbolos. Desde el Ciclo 4 hay varios tipos de rueda (normal, lefty,
 * rebel, reverse) y de símbolo (normal, ephemeral, shy). La máquina solo
 * conoce las clases abstractas Wheel y Symbol y crea los objetos con sus
 * fábricas, así que un tipo nuevo no obliga a cambiar esta clase.
 *
 * La máquina guarda además la lista de símbolos registrados (tipo y
 * color): toda rueda nueva nace con una copia de esos símbolos, y no se
 * pueden registrar dos símbolos del mismo color.
 *
 * Detrás de las ruedas hay un Rectangle grande (el marco) de color fijo
 * que se pone verde cuando hay Jackpot.
 *
 * @version 7.0 (Ciclo 4)
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

    /** Tamaño mínimo de la maratón (n entre 3 y 50). */
    private static final int MIN_N = 3;

    /** Tamaño máximo de la maratón (n entre 3 y 50). */
    private static final int MAX_N = 50;

    /** Separación horizontal, en píxeles, entre dos ejes consecutivos. */
    private static final int AXIS_GAP = 36;

    /** Color fijo del marco cuando no hay Jackpot. */
    private static final String FRAME_COLOR = "#d4c08a";

    /** Color del marco cuando hay Jackpot (ganador). */
    private static final String JACKPOT_COLOR = "#006400";

    private ArrayList<Wheel> wheels;
    private ArrayList<String> symbolTypes;
    private ArrayList<String> symbolColors;
    private boolean isVisible;
    private boolean ok;
    private Rectangle frame;

    /**
     * Requisito 1: Crear una máquina tragamonedas vacía.
     */
    public SlotMachine() {
        this.wheels = new ArrayList<>();
        this.symbolTypes = new ArrayList<>();
        this.symbolColors = new ArrayList<>();
        this.isVisible = false;
        this.ok = true;
        this.frame = new Rectangle();
        this.frame.moveHorizontal(-20);
        this.frame.moveVertical(40);
    }
    
    /**
     * Requisito 13 (Extensión): Crea una máquina de n ruedas y n símbolos,
     * inicializada aleatoriamente. Igual que en la maratón, n debe estar
     * entre 3 y 50; si no, la máquina queda vacía y ok() es false. Todas
     * las ruedas y los símbolos son de tipo normal.
     * @param n Cantidad de ruedas y símbolos distintos (entre 3 y 50).
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
     * Requisito 2a: Adicionar una rueda normal.
     * @param pos Posición de inserción (base 1).
     */
    public void addWheel(int pos) {
        addWheel(Wheel.NORMAL, pos);
    }

    /**
     * Requisito 16: Adicionar una rueda del tipo indicado. La rueda nace
     * con una copia de todos los símbolos registrados en la máquina, en el
     * mismo orden y del mismo tipo. Si el tipo no existe, no se agrega
     * nada y ok() queda en false.
     * @param type Tipo de rueda ("normal", "lefty", "rebel" o "reverse").
     * @param pos Posición de inserción (base 1).
     */
    public void addWheel(String type, int pos) {
        Wheel newWheel = Wheel.create(type, 0);
        if (newWheel == null) {
            notifyError("No existe el tipo de rueda " + type + ".");
            return;
        }
        for (int i = 0; i < symbolColors.size(); i++) {
            newWheel.addSymbol(symbolTypes.get(i), i + 1, symbolColors.get(i));
        }

        int index = adjustPos(pos, wheels.size() + 1) - 1;
        wheels.add(index, newWheel);

        if (this.isVisible) {
            newWheel.makeVisible();
        }
        
        recalculateAxes(); 
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 2b: Eliminar una rueda. Una rueda rebel no se deja
     * eliminar (Requisito 17).
     * @param pos Posición de la rueda a eliminar (base 1).
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para eliminar.");
            return;
        }
        int index = adjustPos(pos, wheels.size()) - 1;
        if (!wheels.get(index).isRemovable()) {
            notifyError("La rueda " + (index + 1) + " no se deja eliminar.");
            return;
        }
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
     * Requisito 10a: Fijar una rueda para que no gire. Una rueda rebel no
     * se deja bloquear (Requisito 17).
     * @param wheel Posición de la rueda a fijar (base 1).
     * @return La posición (base 1, ya ajustada) del eje que quedó
     *         bloqueado, o -1 si no había ruedas o la rueda no se deja
     *         bloquear.
     */
    public int lock(int wheel) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para fijar.");
            return -1;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        if (!wheels.get(index).isLockable()) {
            notifyError("La rueda " + (index + 1) + " no se deja bloquear.");
            return -1;
        }
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
     * Requisito 3a: Adicionar un símbolo normal.
     * @param pos Posición del símbolo en las ruedas.
     * @param color Nombre del color en estándar CSS.
     */
    public void addSymbol(int pos, String color) {
        addSymbol(Symbol.NORMAL, pos, color);
    }

    /**
     * Requisito 16: Adicionar un símbolo del tipo indicado. El símbolo
     * queda registrado en la máquina (así las ruedas que se agreguen
     * después también lo tienen) y se agrega a cada rueda que no esté
     * bloqueada; las bloqueadas se omiten. Funciona aunque todavía no haya
     * ruedas. Se rechaza, con ok() en false, si el tipo no existe o si ya
     * hay un símbolo de ese color.
     * @param type Tipo de símbolo ("normal", "ephemeral" o "shy").
     * @param pos Posición del símbolo en las ruedas (base 1).
     * @param color Nombre del color en estándar CSS.
     */
    public void addSymbol(String type, int pos, String color) {
        if (Symbol.create(type, color, 0, 0) == null) {
            notifyError("No existe el tipo de símbolo " + type + ".");
            return;
        }
        for (String registered : symbolColors) {
            if (registered.equalsIgnoreCase(color)) {
                notifyError("Ya existe un símbolo de color " + color + ".");
                return;
            }
        }

        int index = adjustPos(pos, symbolColors.size() + 1) - 1;
        symbolTypes.add(index, type.toLowerCase());
        symbolColors.add(index, color);

        for (Wheel wheel : wheels) {
            if (!wheel.isLocked()) {
                wheel.addSymbol(type, pos, color);
            }
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 3b: Eliminar un símbolo por su color. Se quita de los
     * símbolos registrados y de las ruedas que no estén bloqueadas. Se
     * rechaza si no hay ningún símbolo registrado de ese color.
     * @param symbol Color del símbolo a eliminar.
     */
    public void delSymbol(String symbol) {
        int index = -1;
        for (int i = 0; i < symbolColors.size(); i++) {
            if (symbolColors.get(i).equalsIgnoreCase(symbol)) {
                index = i;
            }
        }
        if (index == -1) {
            notifyError("No existe un símbolo de color " + symbol + ".");
            return;
        }
        symbolTypes.remove(index);
        symbolColors.remove(index);

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
     * no estén bloqueadas (comportamiento de "tirón" de casino). Las
     * ruedas giran de izquierda a derecha, para que una lefty copie a su
     * vecina ya girada. Cada rueda, al terminar, aplica los efectos del
     * giro con finishSpin(...).
     */
    public void spin() {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para girar.");
            return;
        }
        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (!w.isLocked()) {
                w.spin();
                w.finishSpin(i > 0 ? wheels.get(i - 1) : null);
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
        w.finishSpin(index > 0 ? wheels.get(index - 1) : null);
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
     * Si steps es mayor que 0 cuenta como un giro: al final se aplican sus
     * efectos con finishSpin(...) (la lefty copia, el ephemeral se encoge,
     * el shy alterna).
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
        if (steps > 0) {
            w.finishSpin(index > 0 ? wheels.get(index - 1) : null);
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
     * existencia de colores) y solo si todo es correcto se aplica. No es
     * un giro (pone un color exacto), pero cada símbolo que queda visible
     * cuenta como seleccionado.
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
            wheels.get(i).select();
        }

        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 9: Intercambiar dos ruedas de posición manteniendo los ejes
     * estáticos. Se intercambian las ruedas completas (su tipo, sus
     * símbolos y su estado): cada una pasa al eje de la otra. Toda la
     * lógica está en este método. Se rechaza si las posiciones no son
     * válidas, si alguna rueda está bloqueada o si alguna no se deja
     * intercambiar (la rebel, Requisito 17).
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

        Wheel w1 = wheels.get(wheel1 - 1);
        Wheel w2 = wheels.get(wheel2 - 1);

        if (!w1.isSwappable()) {
            notifyError("La rueda " + wheel1 + " no se deja intercambiar.");
            return;
        }
        if (!w2.isSwappable()) {
            notifyError("La rueda " + wheel2 + " no se deja intercambiar.");
            return;
        }
        if (w1.isLocked()) {
            notifyError("El eje " + wheel1 + " está bloqueado y no puede modificarse.");
            return;
        }
        if (w2.isLocked()) {
            notifyError("El eje " + wheel2 + " está bloqueado y no puede modificarse.");
            return;
        }
        if (wheel1 == wheel2) {
            this.ok = true;
            return;
        }

        int x1 = w1.getXPosition();
        int x2 = w2.getXPosition();
        wheels.set(wheel1 - 1, w2);
        wheels.set(wheel2 - 1, w1);
        w1.updateAxisPosition(x2);
        w2.updateAxisPosition(x1);

        this.ok = true;
        refreshVisibility();
    }

    /**
     * Deja visible en una rueda específica el símbolo del color indicado.
     * Rechazada si el eje está bloqueado. No es un giro, pero el símbolo
     * cuenta como seleccionado (aunque ya estuviera visible).
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
        w.select();
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 5: Consultar los símbolos visibles de la máquina.
     * @return Arreglo con el color que se ve en cada rueda: "" si su
     *         símbolo está escondido (shy) y "white" si no tiene símbolos.
     */
    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).getShownColor();
        }
        this.ok = true;
        return config;
    }

    /**
     * Consultar los colores de los símbolos REGISTRADOS en la máquina (los
     * disponibles, no necesariamente los que se ven ahora). Funciona
     * aunque todavía no haya ruedas.
     * @return Arreglo con los colores registrados, en orden.
     */
    public String[] symbols() {
        this.ok = true;
        return symbolColors.toArray(new String[0]);
    }
    
    /**
     * Consultar la cantidad de colores DISTINTOS VISIBLES actualmente en
     * la máquina (equivale al valor "k" del problema de la maratón: "the
     * number of distinct symbols in the sequence she can currently see").
     * SlotMachineContest lo usa como medida para comparar posiciones, no
     * como condición de Jackpot (para eso está isJackpot()). Un símbolo
     * shy escondido no aporta color.
     * @return Número de colores visibles diferentes en este momento.
     */
    public int distinctSymbols() {
        Set<String> visible = new HashSet<>();
        for (Wheel w : wheels) {
            Symbol active = w.getVisibleSymbol();
            if (active != null && !active.isHidden()) {
                visible.add(active.getColor().toLowerCase());
            }
        }
        this.ok = true;
        return visible.size();
    }

    /**
     * Requisito 6: Consultar si la configuración VISIBLE es la ganadora (Jackpot).
     * ok() queda en true si la consulta fue válida (2 o más ruedas, todas con
     * símbolo visible), sin importar si el resultado es true o false; queda
     * en false solo si la consulta no pudo evaluarse. Un símbolo shy
     * escondido no aporta color, así que impide el Jackpot.
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
     * Requisito 8: Terminar el simulador: lo oculta. No llama a
     * System.exit(0), porque eso cerraría también BlueJ y cortaría las
     * pruebas de unidad que llaman exit() al terminar (por ejemplo las
     * compartidas de SlotMachineCC4Test).
     */
    public void exit() {
        makeInvisible();
        this.ok = true;
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
                wheel.showActive();
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
     * isJackpot() y updateFrame(). Un símbolo shy escondido no muestra
     * color, así que no coincide con ninguno.
     * @return true si hay al menos 2 ruedas y todas muestran el mismo color.
     */
    private boolean allSameColor() {
        if (wheels.size() < 2) return false;
        Symbol first = wheels.get(0).getVisibleSymbol();
        if (first == null) return false;
        for (Wheel wheel : wheels) {
            Symbol active = wheel.getVisibleSymbol();
            if (active == null || active.isHidden()
                    || !first.getColor().equalsIgnoreCase(active.getColor())) {
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