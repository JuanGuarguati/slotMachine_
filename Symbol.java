import shapes.*;

/**
 * Representa un símbolo individual dentro de una rueda de la máquina tragamonedas.
 * El símbolo es EXCLUSIVAMENTE un cuadrado sólido de un color: no tiene texto,
 * números ni íconos. Gestiona su color, visibilidad y posición física en el
 * lienzo (Canvas).
 * 
 * @version 3.0 
 */
public class Symbol {
    private String color;
    private boolean isVisible;
    private Rectangle shape;

    /**
     * Construye un nuevo símbolo.
     * @param color El color del símbolo en formato estándar.
     * @param x La coordenada X inicial del símbolo (X del eje al que pertenece).
     * @param y La coordenada Y inicial del símbolo.
     */
    public Symbol(String color, int x, int y) {
        this.color = color;
        this.isVisible = false;
        this.shape = new Rectangle();
        this.shape.changeSize(30, 30);   // cuadrado real: el color ES el símbolo
        this.shape.changeColor(color);
        this.shape.moveHorizontal(x - 60);
        this.shape.moveVertical(y - 50);
    }

    /**
     * Consulta el color del símbolo.
     * @return El color actual.
     */
    public String getColor() { 
        return color; 
    }
    
    /**
     * Consulta si el símbolo es visible actualmente.
     * @return true si es visible, false en caso contrario.
     */
    public boolean isVisible() { 
        return isVisible; 
    }

    /**
     * Hace visible el símbolo en el lienzo.
     */
    public void makeVisible() {
        this.isVisible = true;
        if (shape != null) shape.makeVisible();
    }

    /**
     * Oculta el símbolo del lienzo.
     */
    public void makeInvisible() {
        this.isVisible = false;
        if (shape != null) shape.makeInvisible();
    }

    /**
     * Desplaza físicamente la figura en el eje horizontal.
     * Utilizado para reacomodar símbolos cuando los ejes cambian de posición.
     * @param distance La distancia en píxeles a desplazar.
     */
    public void shiftHorizontal(int distance) {
        if (this.shape != null) {
            this.shape.moveHorizontal(distance);
        }
    }
}