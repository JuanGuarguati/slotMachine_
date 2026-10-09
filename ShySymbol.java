import shapes.Triangle;

/**
 * Símbolo tímido (Requisito 18): se dibuja como un triángulo de color y
 * alterna su estado de visible a invisible cada vez que queda seleccionado
 * en su rueda. Empieza visible: la primera selección lo esconde, la
 * siguiente lo vuelve a mostrar, y así sucesivamente.
 *
 * Mientras está escondido no muestra su color: configuration() da "" en
 * su rueda, y no cuenta en distinctSymbols() ni en isJackpot().
 *
 * @version 4.0 (Ciclo 4)
 */
public class ShySymbol extends Symbol {

    private Triangle shape;
    private boolean hidden;

    /**
     * Construye un símbolo tímido, inicialmente sin esconder.
     * @param color Color del símbolo.
     * @param x Coordenada X del eje de la rueda.
     * @param y Coordenada Y de referencia de la rueda.
     */
    public ShySymbol(String color, int x, int y) {
        super(color);
        hidden = false;
        shape = new Triangle();
        shape.changeSize(SIZE, SIZE);
        shape.changeColor(color);
        shape.moveHorizontal(x - 115);
        shape.moveVertical(y - 50);
    }

    /**
     * Fue seleccionado en la rueda: alterna entre escondido y visible.
     */
    @Override
    public void selected() {
        setHidden(!hidden);
    }

    /**
     * Consulta si el símbolo está escondido.
     * @return true si está escondido.
     */
    @Override
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Pone el símbolo escondido o no escondido. Si la rueda se está
     * dibujando, el cambio se ve de inmediato.
     * @param hidden true para esconderlo.
     */
    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        if (hidden) {
            shape.makeInvisible();
        } else if (isVisible()) {
            shape.makeVisible();
        }
    }

    /**
     * Desplaza el triángulo en el eje horizontal.
     * @param distance Distancia en píxeles.
     */
    @Override
    public void shiftHorizontal(int distance) {
        shape.moveHorizontal(distance);
    }

    /**
     * Dibuja el triángulo, salvo que el símbolo esté escondido.
     */
    @Override
    protected void show() {
        if (!hidden) {
            shape.makeVisible();
        }
    }

    /**
     * Borra el triángulo.
     */
    @Override
    protected void hide() {
        shape.makeInvisible();
    }
}
