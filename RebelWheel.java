import shapes.Rectangle;

/**
 * Rueda rebelde (Requisito 17): no se deja bloquear, ni intercambiar, ni
 * eliminar. Gira como una rueda normal. Su marca es una barra roja gruesa.
 *
 * @version 4.0 (Ciclo 4)
 */
public class RebelWheel extends Wheel {

    /**
     * Construye una rueda rebelde.
     * @param xPosition La coordenada X del eje.
     */
    public RebelWheel(int xPosition) {
        super(xPosition);
    }

    /**
     * La rueda rebelde no se deja bloquear.
     * @return false siempre.
     */
    @Override
    public boolean isLockable() {
        return false;
    }

    /**
     * La rueda rebelde no se deja intercambiar.
     * @return false siempre.
     */
    @Override
    public boolean isSwappable() {
        return false;
    }

    /**
     * La rueda rebelde no se deja eliminar.
     * @return false siempre.
     */
    @Override
    public boolean isRemovable() {
        return false;
    }

    /**
     * Marca de la rueda rebelde: barra roja de 30x12.
     * @param marker La marca a la que se le da estilo.
     */
    @Override
    protected void styleMarker(Rectangle marker) {
        marker.changeSize(12, 30);
        marker.changeColor("red");
    }
}
