import shapes.Rectangle;

/**
 * Símbolo normal: un cuadrado sólido de color. Es el símbolo de los ciclos
 * anteriores; no cambia al girar ni al ser seleccionado.
 *
 * @version 4.1 (Ciclo 4)
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
}
