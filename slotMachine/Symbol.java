import shapes.*;

public class Symbol {
    private String color;
    private boolean isVisible;
    private Rectangle shape;

    public Symbol(String color, int x, int y) {
        this.color = color;
        this.isVisible = false;
        this.shape = new Rectangle();
        this.shape.changeColor(color);
        this.shape.moveHorizontal(x - 60);
        this.shape.moveVertical(y - 50);
    }

    public String getColor() { return color; }
    public boolean isVisible() { return isVisible; }

    public void makeVisible() {
        this.isVisible = true;
        if (shape != null) shape.makeVisible();
    }

    public void makeInvisible() {
        this.isVisible = false;
        if (shape != null) shape.makeInvisible();
    }
}