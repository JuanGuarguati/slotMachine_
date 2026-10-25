import shapes.Shape;

/**
 * Símbolo de una rueda. Guarda lo común a todos los tipos de símbolo
 * (normal, ephemeral y shy).
 *
 * @author Juan David Guarguati Guarguati, Juan Pablo Suárez Rubiano
 * @version 5.0
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

    /** Figura con la que se dibuja el símbolo; la crea cada subclase. */
    protected Shape shape;

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
     * copiar el estado de su vecina. Solo tiene efecto en los tipos que se
     * esconden.
     * @param hidden true para esconderlo.
     */
    public abstract void setHidden(boolean hidden);

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
     * Avisa al símbolo que su rueda dio un giro. Cada tipo decide cómo
     * reacciona.
     */
    public abstract void spun();

    /**
     * Avisa al símbolo que quedó seleccionado (es el que muestra su rueda).
     * Cada tipo decide cómo reacciona.
     */
    public abstract void selected();

    /**
     * Desplaza la figura del símbolo en el eje horizontal. Se usa cuando
     * los ejes de la máquina cambian de posición.
     * @param distance Distancia en píxeles.
     */
    public void shiftHorizontal(int distance) {
        shape.moveHorizontal(distance);
    }

    /**
     * Dibuja la figura del símbolo.
     */
    protected void show() {
        shape.makeVisible();
    }

    /**
     * Borra la figura del símbolo.
     */
    protected void hide() {
        shape.makeInvisible();
    }
}
