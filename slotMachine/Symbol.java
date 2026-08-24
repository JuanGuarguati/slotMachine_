import shapes.*;

/**
 * Representa un símbolo individual dentro de las ruedas de la máquina tragamonedas.
 * Encapsula la información del color y gestiona la posición y visibilidad
 * de la figura geométrica (Rectangle) proyectada en el Canvas.
 * 
 * @author Juan David Guarguati, Juan Pablo Suarez Rubiano
 * @version 1.0 
 */
public class Symbol {
    
    private String color;    
    private boolean isVisible;    
    private Rectangle shape;

    /**
     * Construye un nuevo símbolo con un color y una ubicación de coordenadas específicas.
     * Propósito: Instanciar la figura geométrica base, asignarle color y trasladarla
     * a las coordenadas relativas dentro de la rueda.
     * Estado: Asigna `color`, establece `isVisible = false` e inicializa y posiciona la figura `shape`.
     * 
     * @param color Nombre del color para la figura en formato CSS.
     * @param x Coordenada de destino en el eje horizontal (eje X).
     * @param y Coordenada de destino en el eje vertical (eje Y).
     */
    public Symbol(String color, int x, int y) {
        this.color = color;
        this.isVisible = false;
        this.shape = new Rectangle();
        this.shape.changeColor(color);
        this.shape.moveHorizontal(x - 60);
        this.shape.moveVertical(y - 50);
    }

    /**
     * Consulta el color asignado a este símbolo.
     * Propósito: Permitir la verificación de coincidencias de color en jugadas (Jackpot).
     * Estado: No modifica el estado interno.
     * 
     * @return El nombre del color registrado.
     */
    public String getColor() { 
        return color; 
    }
    
    /**
     * Consulta el estado actual de visibilidad del símbolo.
     * Propósito: Determinar si la figura está desplegada activamente en el Canvas.
     * Estado: No modifica el estado interno.
     * 
     * @return `true` si la figura se dibuja en pantalla; `false` en caso contrario.
     */
    public boolean isVisible() { 
        return isVisible; 
    }

    /**
     * Dibuja la figura del símbolo en la pantalla gráfica.
     * Propósito: Hacer visible la representación geométrica en el Canvas cuando la rueda lo activa.
     * Estado: Cambia `isVisible = true` y ejecuta el redibujado de la figura `shape`.
     */
    public void makeVisible() {
        this.isVisible = true;
        if (shape != null) shape.makeVisible();
    }

    /**
     * Oculta la figura del símbolo de la pantalla gráfica.
     * Propósito: Borrar del Canvas la figura cuando la rueda gira o el simulador pasa a modo invisible.
     * Estado: Cambia `isVisible = false` y borra la figura `shape` de la vista.
     */
    public void makeInvisible() {
        this.isVisible = false;
        if (shape != null) shape.makeInvisible();
    }
}