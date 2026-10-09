/**
 * Representa un símbolo de una rueda de la máquina tragamonedas.
 *
 * Es una clase abstracta: guarda lo que todos los símbolos tienen en común
 * (su color y si se están dibujando) y deja que cada tipo decida con qué
 * figura se dibuja y cómo reacciona cuando su rueda gira o cuando él queda
 * seleccionado.
 *
 * Tipos disponibles (Requisito 18):
 * - "normal": cuadrado de color (el de los ciclos anteriores).
 * - "ephemeral": círculo que en cada giro de su rueda se encoge hasta
 *   quedar como un punto.
 * - "shy": triángulo que alterna entre visible e invisible cada vez que
 *   queda seleccionado en su rueda.
 *
 * Para agregar un tipo nuevo basta con crear una subclase y añadir una
 * línea en create(...): SlotMachine y Wheel no cambian (extensibilidad).
 *
 * @version 4.0 (Ciclo 4)
 */
public abstract class Symbol {

    /** Tipo de símbolo normal (cuadrado). */
    public static final String NORMAL = "normal";

    /** Tipo de símbolo efímero (círculo que se encoge). */
    public static final String EPHEMERAL = "ephemeral";

    /** Tipo de símbolo tímido (triángulo que se esconde). */
    public static final String SHY = "shy";

    /** Lado, en píxeles, de la celda donde se dibuja un símbolo. */
    protected static final int SIZE = 30;

    private String color;
    private boolean isVisible;

    /**
     * Construye la parte común de un símbolo.
     * @param color Color del símbolo.
     */
    protected Symbol(String color) {
        this.color = color;
        this.isVisible = false;
    }

    /**
     * Fábrica de símbolos: crea un símbolo del tipo indicado.
     * @param type Tipo de símbolo ("normal", "ephemeral" o "shy"); no
     *        distingue mayúsculas.
     * @param color Color del símbolo.
     * @param x Coordenada X del eje de la rueda dueña del símbolo.
     * @param y Coordenada Y de referencia de la rueda.
     * @return El símbolo creado, o null si el tipo no existe.
     */
    public static Symbol create(String type, String color, int x, int y) {
        if (type == null) return null;
        switch (type.toLowerCase()) {
            case NORMAL:    return new NormalSymbol(color, x, y);
            case EPHEMERAL: return new EphemeralSymbol(color, x, y);
            case SHY:       return new ShySymbol(color, x, y);
            default:        return null;
        }
    }


    /**
     * Consulta el color del símbolo.
     * @return El color del símbolo.
     */
    public String getColor() {
        return color;
    }

    /**
     * Consulta si el símbolo se está dibujando (es el símbolo activo de una
     * rueda visible).
     * @return true si se está dibujando.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Consulta si el símbolo está escondido. Un símbolo escondido sigue
     * siendo el seleccionado de su rueda, pero no muestra su color: no se
     * dibuja y no cuenta en configuration(), distinctSymbols() ni
     * isJackpot(). Por defecto ningún símbolo se esconde.
     * @return false por defecto.
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Pone el símbolo escondido o no escondido. Lo usa la rueda lefty para
     * copiar el estado de su vecina. Por defecto no hace nada, porque solo
     * los tipos que se esconden tienen ese estado.
     * @param hidden true para esconderlo.
     */
    public void setHidden(boolean hidden) {
    }

    /**
     * Hace visible el símbolo en el lienzo.
     */
    public void makeVisible() {
        this.isVisible = true;
        show();
    }

    /**
     * Oculta el símbolo del lienzo.
     */
    public void makeInvisible() {
        this.isVisible = false;
        hide();
    }

    /**
     * Avisa al símbolo que su rueda dio un giro. Por defecto no hace nada;
     * los tipos que cambian con cada giro lo redefinen.
     */
    public void spun() {
    }

    /**
     * Avisa al símbolo que quedó seleccionado (es el que muestra su rueda).
     * Por defecto no hace nada; los tipos que reaccionan al ser
     * seleccionados lo redefinen.
     */
    public void selected() {
    }

    /**
     * Desplaza la figura del símbolo en el eje horizontal. Se usa cuando
     * los ejes de la máquina cambian de posición.
     * @param distance Distancia en píxeles.
     */
    public abstract void shiftHorizontal(int distance);

    /**
     * Dibuja la figura del símbolo.
     */
    protected abstract void show();

    /**
     * Borra la figura del símbolo.
     */
    protected abstract void hide();
}
