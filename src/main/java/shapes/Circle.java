package shapes;

import java.awt.geom.Ellipse2D;

/**
 * Círculo que se dibuja en el lienzo.
 *
 * @author Michael Kolling y David J. Barnes (modificada por el equipo)
 * @version 5.0
 */
public class Circle extends Shape{

    public static final double PI = 3.1416;

    private int diameter;

    /**
     * Create a new circle at default position with default color.
     */
    public Circle(){
        super(20, 15, "blue");
        diameter = 30;
    }

    /**
     * Change the size.
     * @param newDiameter the new size (in pixels). Size must be >=0.
     */
    public void changeSize(int newDiameter){
        erase();
        diameter = newDiameter;
        draw();
    }

    /**
     * Draw the circle with current specifications on screen.
     */
    @Override
    protected void draw(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                new Ellipse2D.Double(xPosition, yPosition, diameter, diameter));
            canvas.wait(10);
        }
    }
}
