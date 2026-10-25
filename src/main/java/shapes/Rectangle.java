package shapes;

/**
 * Rectángulo que se dibuja en el lienzo.
 *
 * @author Michael Kolling y David J. Barnes (modificada por el equipo)
 * @version 5.0
 */
public class Rectangle extends Shape{

    public static final int EDGES = 4;

    private int height;
    private int width;

    /**
     * Create a new rectangle at default position with default color.
     */
    public Rectangle(){
        super(70, 15, "magenta");
        height = 30;
        width = 40;
    }

    /**
     * Change the size to the new size.
     * @param newHeight the new height in pixels. newHeight must be >=0.
     * @param newWidth the new width in pixels. newWidth must be >=0.
     */
    public void changeSize(int newHeight, int newWidth) {
        erase();
        height = newHeight;
        width = newWidth;
        draw();
    }

    /**
     * Draw the rectangle with current specifications on screen.
     */
    @Override
    protected void draw() {
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.draw(this, color,
                new java.awt.Rectangle(xPosition, yPosition, width, height));
            canvas.wait(10);
        }
    }
}
