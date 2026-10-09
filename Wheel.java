import java.util.ArrayList;
import java.util.Random;
import shapes.Rectangle;

/**
 * Representa una rueda (eje físico) de la máquina tragamonedas.
 *
 * Es una clase abstracta: guarda lo común a todas las ruedas (sus
 * símbolos, el índice del símbolo visible, su posición y si está
 * bloqueada) y deja que cada tipo de rueda redefina solo lo que cambia.
 * Debajo de cada rueda se dibuja una marca (un Rectangle) cuya forma y
 * color dependen del tipo, para distinguir los tipos a simple vista.
 *
 * Tipos disponibles (Requisitos 17 y 19):
 * - "normal": la rueda de los ciclos anteriores.
 * - "lefty": al girar copia el estado de la rueda de su izquierda.
 * - "rebel": no se deja bloquear, ni intercambiar, ni eliminar.
 * - "reverse" (propuesta propia): gira en sentido contrario.
 *
 * Para agregar un tipo nuevo basta con crear una subclase y añadir una
 * línea en create(...): SlotMachine no cambia (extensibilidad).
 *
 * @version 6.0 (Ciclo 4)
 */
public abstract class Wheel {

    /** Tipo de rueda normal. */
    public static final String NORMAL = "normal";

    /** Tipo de rueda zurda: copia a la rueda de su izquierda. */
    public static final String LEFTY = "lefty";

    /** Tipo de rueda rebelde: no se bloquea, intercambia ni elimina. */
    public static final String REBEL = "rebel";

    /** Tipo de rueda inversa: gira hacia atrás (propuesta propia). */
    public static final String REVERSE = "reverse";

    /** Lo que muestra configuration() en una rueda sin símbolos. */
    public static final String EMPTY_COLOR = "white";

    /** Coordenada Y de referencia de los símbolos de la rueda. */
    private static final int SYMBOL_Y = 100;

    /** Coordenada Y superior de la marca de tipo (debajo del marco). */
    private static final int MARKER_Y = 112;

    private ArrayList<Symbol> symbols;
    private int visibleSymbolIndex;
    private int xPosition;
    private boolean locked;
    private Random random;
    private Rectangle marker;

    /**
     * Construye la parte común de una rueda anclada a una posición
     * horizontal y le pide al tipo concreto que dé forma a su marca.
     * @param xPosition La coordenada X del eje.
     */
    protected Wheel(int xPosition) {
        this.symbols = new ArrayList<>();
        this.visibleSymbolIndex = 0;
        this.xPosition = xPosition;
        this.locked = false;
        this.random = new Random();
        this.marker = new Rectangle();
        this.marker.moveHorizontal(xPosition - 60);
        this.marker.moveVertical(MARKER_Y - 15);
        styleMarker(marker);
    }

    /**
     * Fábrica de ruedas: crea una rueda del tipo indicado.
     * @param type Tipo de rueda ("normal", "lefty", "rebel" o "reverse");
     *        no distingue mayúsculas.
     * @param xPosition Coordenada X del eje.
     * @return La rueda creada, o null si el tipo no existe.
     */
    public static Wheel create(String type, int xPosition) {
        if (type == null) return null;
        switch (type.toLowerCase()) {
            case NORMAL:  return new NormalWheel(xPosition);
            case LEFTY:   return new LeftyWheel(xPosition);
            case REBEL:   return new RebelWheel(xPosition);
            case REVERSE: return new ReverseWheel(xPosition);
            default:      return null;
        }
    }

    /**
     * Da tamaño, color y posición a la marca que identifica el tipo de
     * rueda. La marca empieza en la esquina izquierda, debajo del símbolo.
     * @param marker La marca a la que se le da estilo.
     */
    protected abstract void styleMarker(Rectangle marker);

    /**
     * Adiciona un símbolo del tipo indicado en una posición.
     * @param type Tipo de símbolo.
     * @param pos Posición de inserción (base 1).
     * @param color Color del nuevo símbolo.
     * @return true si se agregó; false si el tipo de símbolo no existe.
     */
    public boolean addSymbol(String type, int pos, String color) {
        Symbol newSymbol = Symbol.create(type, color, this.xPosition, SYMBOL_Y);
        if (newSymbol == null) return false;
        int index = pos - 1;
        if (index <= 0) {
            symbols.add(0, newSymbol);
        } else if (index >= symbols.size()) {
            symbols.add(newSymbol);
        } else {
            symbols.add(index, newSymbol);
        }
        return true;
    }

    /**
     * Elimina todos los símbolos de un color en esta rueda y los borra
     * del lienzo.
     * @param symbolColor El color del símbolo a eliminar.
     */
    public void delSymbol(String symbolColor) {
        hideActive();
        for (Symbol s : symbols) {
            if (s.getColor().equalsIgnoreCase(symbolColor)) {
                s.makeInvisible();
            }
        }
        symbols.removeIf(s -> s.getColor().equalsIgnoreCase(symbolColor));
        if (visibleSymbolIndex >= symbols.size()) {
            visibleSymbolIndex = 0;
        }
    }

    /**
     * Consulta, sin modificar la rueda, si tiene un símbolo del color
     * indicado.
     * @param color Color a buscar.
     * @return true si la rueda tiene registrado ese color.
     */
    public boolean hasColor(String color) {
        for (Symbol s : symbols) {
            if (s.getColor().equalsIgnoreCase(color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Mueve la rueda a una posición ALEATORIA. Solo mueve: los efectos del
     * giro se aplican después con finishSpin(...).
     */
    public void spin() {
        if (symbols.isEmpty()) return;
        hideActive();
        visibleSymbolIndex = random.nextInt(symbols.size());
    }

    /**
     * Rota la rueda `steps` posiciones hacia adelante, en forma cíclica.
     * Solo mueve: los efectos del giro se aplican después con
     * finishSpin(...). No verifica lock(): eso lo hace SlotMachine.
     * @param steps Cantidad de posiciones a avanzar (negativo retrocede).
     */
    public void rotate(int steps) {
        if (symbols.isEmpty()) return;
        hideActive();
        int size = symbols.size();
        visibleSymbolIndex = ((visibleSymbolIndex + steps) % size + size) % size;
    }

    /**
     * Termina un giro de la rueda: avisa a todos sus símbolos que la rueda
     * giró (el ephemeral se encoge) y luego aplica lo que hace el tipo de
     * rueda al quedar quieta, con afterSpin(...).
     * @param left La rueda de la izquierda, o null si es la primera.
     */
    public void finishSpin(Wheel left) {
        for (Symbol s : symbols) {
            s.spun();
        }
        afterSpin(left);
    }

    /**
     * Lo que hace el tipo de rueda al quedar quieta después de un giro.
     * Por defecto selecciona el símbolo que quedó visible (el shy
     * alterna). La rueda lefty lo redefine para copiar a su vecina.
     * @param left La rueda de la izquierda, o null si es la primera.
     */
    protected void afterSpin(Wheel left) {
        select();
    }

    /**
     * Avisa al símbolo visible que fue seleccionado.
     */
    public void select() {
        Symbol active = getVisibleSymbol();
        if (active != null) active.selected();
    }

    /**
     * Deja visible el primer símbolo del color indicado. Solo mueve: no es
     * un giro ni una selección.
     * @param color Color a buscar.
     * @return true si se encontró el color; false si no existe.
     */
    public boolean setVisibleColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equalsIgnoreCase(color)) {
                hideActive();
                visibleSymbolIndex = i;
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el símbolo que la rueda muestra actualmente.
     * @return El símbolo visible o null si la rueda está vacía.
     */
    public Symbol getVisibleSymbol() {
        if (symbols.isEmpty()) return null;
        return symbols.get(visibleSymbolIndex);
    }

    /**
     * Consulta el color que se ve en la ventana de la rueda.
     * @return El color del símbolo visible; "" si está escondido (shy);
     *         EMPTY_COLOR ("white") si la rueda no tiene símbolos.
     */
    public String getShownColor() {
        Symbol active = getVisibleSymbol();
        if (active == null) return EMPTY_COLOR;
        if (active.isHidden()) return "";
        return active.getColor();
    }


    /**
     * Consulta si la rueda se deja bloquear.
     * @return true por defecto.
     */
    public boolean isLockable() {
        return true;
    }

    /**
     * Consulta si la rueda se deja intercambiar.
     * @return true por defecto.
     */
    public boolean isSwappable() {
        return true;
    }

    /**
     * Consulta si la rueda se deja eliminar.
     * @return true por defecto.
     */
    public boolean isRemovable() {
        return true;
    }

    /**
     * Fija la rueda para que no gire. SlotMachine pregunta antes
     * isLockable().
     */
    public void lock() {
        this.locked = true;
    }

    /**
     * Libera la rueda para que pueda girar.
     */
    public void unlock() {
        this.locked = false;
    }

    /**
     * Consulta si la rueda está bloqueada.
     * @return true si está bloqueada.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Consulta los símbolos registrados en la rueda.
     * @return Lista de símbolos de la rueda.
     */
    public ArrayList<Symbol> getSymbols() {
        return symbols;
    }

    /**
     * Consulta la coordenada horizontal del eje.
     * @return Posición X.
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * Mueve el eje a una nueva posición y arrastra sus símbolos y su marca.
     * @param newX La nueva coordenada X del eje.
     */
    public void updateAxisPosition(int newX) {
        int deltaX = newX - this.xPosition;
        if (deltaX == 0) return;
        this.xPosition = newX;
        for (Symbol s : symbols) {
            s.shiftHorizontal(deltaX);
        }
        marker.moveHorizontal(deltaX);
    }

    /**
     * Hace visible la rueda: su marca de tipo y su símbolo activo.
     */
    public void makeVisible() {
        marker.makeVisible();
        showActive();
    }

    /**
     * Dibuja solo el símbolo activo (la marca no cambia al girar).
     */
    public void showActive() {
        Symbol active = getVisibleSymbol();
        if (active != null) active.makeVisible();
    }

    /**
     * Oculta la rueda: su marca de tipo y su símbolo activo.
     */
    public void makeInvisible() {
        marker.makeInvisible();
        hideActive();
    }

    /**
     * Oculta el símbolo activo, si lo hay.
     */
    private void hideActive() {
        Symbol active = getVisibleSymbol();
        if (active != null) active.makeInvisible();
    }
}
