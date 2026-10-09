import shapes.Rectangle;

/**
 * Rueda inversa (Requisito 19, tipo propuesto por el equipo): gira en
 * sentido contrario. Cada paso de spin(wheel, steps) la lleva al símbolo
 * ANTERIOR en vez del siguiente. Su marca es un cuadrado verde a la
 * derecha (al revés que la zurda).
 *
 * @version 4.0 (Ciclo 4)
 */
public class ReverseWheel extends Wheel {

    /**
     * Construye una rueda inversa.
     * @param xPosition La coordenada X del eje.
     */
    public ReverseWheel(int xPosition) {
        super(xPosition);
    }

    /**
     * Rota la rueda `steps` posiciones hacia atrás.
     * @param steps Cantidad de posiciones a retroceder.
     */
    @Override
    public void rotate(int steps) {
        super.rotate(-steps);
    }

    /**
     * Marca de la rueda inversa: cuadrado verde de 12x12 pegado a la derecha.
     * @param marker La marca a la que se le da estilo.
     */
    @Override
    protected void styleMarker(Rectangle marker) {
        marker.changeSize(12, 12);
        marker.changeColor("green");
        marker.moveHorizontal(18);
    }
}
