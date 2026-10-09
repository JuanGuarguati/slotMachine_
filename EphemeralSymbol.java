import shapes.Circle;

/**
 * Símbolo efímero (Requisito 18): se dibuja como un círculo de color que,
 * en cada giro de su rueda, se encoge hasta quedar como un punto. Empieza
 * con 30 px de diámetro, pierde 4 px por giro y se queda en 2 px. El
 * círculo se encoge hacia su centro, así que siempre queda en medio de la
 * celda. Aunque sea un punto, su color sigue contando.
 *
 * @version 4.0 (Ciclo 4)
 */
public class EphemeralSymbol extends Symbol {

    /** Píxeles que pierde el diámetro en cada giro. */
    private static final int SHRINK_STEP = 4;

    /** Diámetro final: el símbolo queda como un punto. */
    private static final int POINT_SIZE = 2;

    private Circle shape;
    private int diameter;

    /**
     * Construye un símbolo efímero del tamaño completo de la celda.
     * @param color Color del símbolo.
     * @param x Coordenada X del eje de la rueda.
     * @param y Coordenada Y de referencia de la rueda.
     */
    public EphemeralSymbol(String color, int x, int y) {
        super(color);
        diameter = SIZE;
        shape = new Circle();
        shape.changeSize(diameter);
        shape.changeColor(color);
        shape.moveHorizontal(x - 10);
        shape.moveVertical(y - 50);
    }

    /**
     * Su rueda giró: el círculo se encoge SHRINK_STEP píxeles, sin bajar
     * de POINT_SIZE. Se mueve la mitad de lo que se encoge para que siga
     * centrado en la celda.
     */
    @Override
    public void spun() {
        if (diameter <= POINT_SIZE) return;
        int newDiameter = Math.max(POINT_SIZE, diameter - SHRINK_STEP);
        int offset = (diameter - newDiameter) / 2;
        diameter = newDiameter;
        shape.changeSize(diameter);
        shape.moveHorizontal(offset);
        shape.moveVertical(offset);
    }

    /**
     * Consulta el diámetro actual del círculo.
     * @return Diámetro en píxeles.
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * Consulta si el símbolo ya quedó reducido a un punto.
     * @return true si su diámetro es el mínimo.
     */
    public boolean isPoint() {
        return diameter == POINT_SIZE;
    }

    /**
     * Desplaza el círculo en el eje horizontal.
     * @param distance Distancia en píxeles.
     */
    @Override
    public void shiftHorizontal(int distance) {
        shape.moveHorizontal(distance);
    }

    /**
     * Dibuja el círculo.
     */
    @Override
    protected void show() {
        shape.makeVisible();
    }

    /**
     * Borra el círculo.
     */
    @Override
    protected void hide() {
        shape.makeInvisible();
    }
}
