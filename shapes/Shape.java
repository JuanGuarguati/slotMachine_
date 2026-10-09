package shapes;

/**
 * A shape that can be manipulated and that draws itself on a canvas.
 * It keeps what every shape has in common (position, color and
 * visibility) and the way to move it, show it and hide it. Each concrete
 * shape only says how it is drawn.
 *
 * @author  Michael Kolling and David J. Barnes (Modified: Shape hierarchy, Cycle 4)
 * @version 2.0
 */
public abstract class Shape{

    protected int xPosition;
    protected int yPosition;
    protected String color;
    protected boolean isVisible;

    /**
     * Create a new shape at the given position with the given color.
     * @param xPosition the initial x coordinate
     * @param yPosition the initial y coordinate
     * @param color the initial color
     */
    protected Shape(int xPosition, int yPosition, String color){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.color = color;
        this.isVisible = false;
    }

    /**
     * Make this shape visible. If it was already visible, do nothing.
     */
    public void makeVisible(){
        isVisible = true;
        draw();
    }

    /**
     * Make this shape invisible. If it was already invisible, do nothing.
     */
    public void makeInvisible(){
        erase();
        isVisible = false;
    }

    /**
     * Move the shape a few pixels to the right.
     */
    public void moveRight(){
        moveHorizontal(20);
    }

    /**
     * Move the shape a few pixels to the left.
     */
    public void moveLeft(){
        moveHorizontal(-20);
    }

    /**
     * Move the shape a few pixels up.
     */
    public void moveUp(){
        moveVertical(-20);
    }

    /**
     * Move the shape a few pixels down.
     */
    public void moveDown(){
        moveVertical(20);
    }

    /**
     * Move the shape horizontally.
     * @param distance the desired distance in pixels
     */
    public void moveHorizontal(int distance){
        erase();
        xPosition += distance;
        draw();
    }

    /**
     * Move the shape vertically.
     * @param distance the desired distance in pixels
     */
    public void moveVertical(int distance){
        erase();
        yPosition += distance;
        draw();
    }

    /**
     * Slowly move the shape horizontally.
     * @param distance the desired distance in pixels
     */
    public void slowMoveHorizontal(int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            xPosition += delta;
            draw();
        }
    }

    /**
     * Slowly move the shape vertically.
     * @param distance the desired distance in pixels
     */
    public void slowMoveVertical(int distance){
        int delta;

        if(distance < 0) {
            delta = -1;
            distance = -distance;
        } else {
            delta = 1;
        }

        for(int i = 0; i < distance; i++){
            yPosition += delta;
            draw();
        }
    }

    /**
     * Change the color.
     * @param newColor the new color (a color name or "#RRGGBB").
     */
    public void changeColor(String newColor){
        color = newColor;
        draw();
    }

    /**
     * Draw the shape with current specifications on screen. Each shape
     * draws itself in a different way.
     */
    protected abstract void draw();

    /**
     * Erase the shape on screen.
     */
    protected void erase(){
        if(isVisible) {
            Canvas canvas = Canvas.getCanvas();
            canvas.erase(this);
        }
    }
}
