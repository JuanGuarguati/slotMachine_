import java.util.ArrayList;
import java.util.Random;

public class Wheel {
    private ArrayList<Symbol> symbols;
    private int visibleSymbolIndex;
    private int xPosition;
    private boolean locked;
    private Random random;

    public Wheel(int xPosition) {
        this.symbols = new ArrayList<>();
        this.visibleSymbolIndex = 0;
        this.xPosition = xPosition;
        this.locked = false;
        this.random = new Random();
    }

    public void addSymbol(int pos, String color) {
        int index = pos - 1;
        Symbol newSymbol = new Symbol(color, this.xPosition, 100);
        if (index <= 0) {
            symbols.add(0, newSymbol);
        } else if (index >= symbols.size()) {
            symbols.add(newSymbol);
        } else {
            symbols.add(index, newSymbol);
        }
    }

    public void delSymbol(String symbolColor) {
        symbols.removeIf(s -> s.getColor().equalsIgnoreCase(symbolColor));
        if (visibleSymbolIndex >= symbols.size() && !symbols.isEmpty()) {
            visibleSymbolIndex = 0;
        }
    }

    public void spin() {
        if (locked) return;
        if (!symbols.isEmpty()) {
            Symbol current = getVisibleSymbol();
            if (current != null) {
                current.makeInvisible();
            }
            visibleSymbolIndex = random.nextInt(symbols.size());
        }
    }
    
    /**
     * Busca entre los símbolos de la rueda uno que tenga el color indicado
     * y lo deja como el símbolo visible.
     * @param color Color a buscar.
     * @return true si se encontró y se fijó el color; false si no existe.
     */
    public boolean setVisibleColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equalsIgnoreCase(color)) {
                Symbol current = getVisibleSymbol();
                if (current != null) current.makeInvisible();
                visibleSymbolIndex = i;
                return true;
            }
        }
        return false;
    }
    
    public Symbol getVisibleSymbol() {
        if (symbols.isEmpty()) return null;
        return symbols.get(visibleSymbolIndex);
    }
    
    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public boolean isLocked() {
        return locked;
    }
    
    public ArrayList<Symbol> getSymbols() { return symbols; }
    public int getXPosition() { return xPosition; }
}