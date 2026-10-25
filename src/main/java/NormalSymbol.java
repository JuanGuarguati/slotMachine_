import shapes.Rectangle;

/**
 * Símbolo normal: un cuadrado de color que no cambia.
 *
 * @author Juan David Guarguati Guarguati, Juan Pablo Suárez Rubiano
 * @version 5.0
 */
public class NormalSymbol extends Symbol {

    /**
     * Construye un símbolo normal: un cuadrado de SIZE x SIZE.
     * @param color Color del símbolo.
     * @param x Coordenada X del eje de la rueda.
     * @param y Coordenada Y de referencia de la rueda.
     */
    public NormalSymbol(String color, int x, int y) {
        super(color);
        Rectangle square = new Rectangle();
        square.changeSize(SIZE, SIZE);
        square.changeColor(color);
        square.moveHorizontal(x - 60);
        square.moveVertical(y - 50);
        shape = square;
    }

    /**
     * Un símbolo normal no cambia al girar su rueda.
     */
    @Override
    public void spun() {
        // El símbolo normal no reacciona al giro.
    }

    /**
     * Un símbolo normal no cambia al ser seleccionado.
     */
    @Override
    public void selected() {
        // El símbolo normal no reacciona a la selección.
    }

    /**
     * Un símbolo normal nunca se esconde.
     * @param hidden Se ignora.
     */
    @Override
    public void setHidden(boolean hidden) {
        // El símbolo normal no tiene estado escondido.
    }
}
