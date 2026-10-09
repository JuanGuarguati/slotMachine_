import shapes.Rectangle;

/**
 * Símbolo normal: un cuadrado sólido de color. Es el símbolo de los ciclos
 * anteriores; no cambia al girar ni al ser seleccionado.
 *
 * @version 4.0 (Ciclo 4)
 */
public class NormalSymbol extends Symbol {

    private Rectangle shape;

    /**
     * Construye un símbolo normal.
     * @param color Color del símbolo.
     * @param x Coordenada X del eje de la rueda.
     * @param y Coordenada Y de referencia de la rueda.
     */
    public NormalSymbol(String color, int x, int y) {
        super(color);
        shape = new Rectangle();
        shape.changeSize(SIZE, SIZE);
        shape.changeColor(color);
        shape.moveHorizontal(x - 60);
        shape.moveVertical(y - 50);
    }

    /**
     * Desplaza el cuadrado en el eje horizontal.
     * @param distance Distancia en píxeles.
     */
    @Override
    public void shiftHorizontal(int distance) {
        shape.moveHorizontal(distance);
    }

    /**
     * Dibuja el cuadrado.
     */
    @Override
    protected void show() {
        shape.makeVisible();
    }

    /**
     * Borra el cuadrado.
     */
    @Override
    protected void hide() {
        shape.makeInvisible();
    }
}
