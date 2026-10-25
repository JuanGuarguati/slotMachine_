import shapes.Rectangle;

/**
 * Rueda zurda: al girar copia el estado de la rueda de su izquierda.
 *
 * @author Juan David Guarguati Guarguati, Juan Pablo Suárez Rubiano
 * @version 5.0
 */
public class LeftyWheel extends Wheel {

    /**
     * Construye una rueda zurda.
     * @param xPosition La coordenada X del eje.
     */
    public LeftyWheel(int xPosition) {
        super(xPosition);
    }

    /**
     * Al quedar quieta copia el estado de la rueda de la izquierda: su
     * color visible y si está escondido. Si no puede copiar, se comporta
     * como una rueda normal.
     * @param left La rueda de la izquierda, o null si es la primera.
     */
    @Override
    protected void afterSpin(Wheel left) {
        Symbol leftSymbol = (left == null) ? null : left.getVisibleSymbol();
        if (leftSymbol != null && setVisibleColor(leftSymbol.getColor())) {
            getVisibleSymbol().setHidden(leftSymbol.isHidden());
        } else {
            super.afterSpin(left);
        }
    }

    /**
     * Marca de la rueda zurda: cuadrado azul de 12x12 pegado a la izquierda.
     * @param marker La marca a la que se le da estilo.
     */
    @Override
    protected void styleMarker(Rectangle marker) {
        marker.changeSize(12, 12);
        marker.changeColor("blue");
    }
}
