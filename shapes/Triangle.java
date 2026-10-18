package shapes;

import java.awt.Polygon;

/**
 * A triangle that can be manipulated and that draws itself on a canvas.
 * Its position is the top vertex.
 *
 * @author  Michael Kolling and David J. Barnes (Modified: extends Shape, Cycle 4)
 * @version 2.0
 */
public class Triangle extends Shape{

    public static int VERTICES = 3;

    private int height;
    private int width;

    /**
     * Create a new triangle at default position with default color.
     */
    public Triangle(){
        super(140, 15, "green");
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
     * Draw the triangle with current specifications on screen.
     */
    @Override
    protected void draw(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            int[] xpoints = { xPosition, xPosition + (width / 2), xPosition - (width / 2) };
            int[] ypoints = { yPosition, yPosition + height, yPosition + height };
            canvas.draw(this, color, new Polygon(xpoints, ypoints, 3));
            canvas.wait(10);
        }
    }
}
