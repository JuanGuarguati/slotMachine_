import shapes.Rectangle;

/**
 * Rueda normal: la rueda de los ciclos anteriores. Usa todo el
 * comportamiento de Wheel sin cambios. Su marca es una línea gris delgada.
 *
 * @version 4.0 (Ciclo 4)
 */
public class NormalWheel extends Wheel {

    /**
     * Construye una rueda normal.
     * @param xPosition La coordenada X del eje.
     */
    public NormalWheel(int xPosition) {
        super(xPosition);
    }

    /**
     * Marca de la rueda normal: línea gris de 30x4.
     * @param marker La marca a la que se le da estilo.
     */
    @Override
    protected void styleMarker(Rectangle marker) {
        marker.changeSize(4, 30);
        marker.changeColor("gray");
    }
}
