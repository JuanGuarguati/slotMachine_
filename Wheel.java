import java.util.ArrayList;
import java.util.Random;

/**
 * Representa una rueda (eje físico) de la máquina tragamonedas.
 * Gestiona la lista de símbolos (cada uno es un Rectangle de color), el
 * índice del símbolo actualmente visible y su coordenada estática (el eje
 * físico). Visualmente solo se ven los cuadrados de color.
 * 
 * @version 5.0 
 */
public class Wheel {
    private ArrayList<Symbol> symbols;
    private int visibleSymbolIndex;
    private int xPosition;
    private boolean locked;
    private Random random;

    /**
     * Construye una nueva rueda anclada a una posición horizontal fija.
     * @param xPosition La coordenada X del eje.
     */
    public Wheel(int xPosition) {
        this.symbols = new ArrayList<>();
        this.visibleSymbolIndex = 0;
        this.xPosition = xPosition;
        this.locked = false;
        this.random = new Random();
    }

    /**
     * Adiciona un símbolo a la rueda en una posición específica.
     * @param pos Posición de inserción (base 1).
     * @param color Color del nuevo símbolo.
     */
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

    /**
     * Elimina todos los símbolos de un color específico en esta rueda.
     * @param symbolColor El color del símbolo a eliminar.
     */
    public void delSymbol(String symbolColor) {
        symbols.removeIf(s -> s.getColor().equalsIgnoreCase(symbolColor));
        if (visibleSymbolIndex >= symbols.size() && !symbols.isEmpty()) {
            visibleSymbolIndex = 0;
        }
    }

    /**
     * Consulta, sin modificar el estado de la rueda, si existe un símbolo
     * registrado con el color indicado. Útil para validar operaciones
     * (por ejemplo spin(String[])) antes de aplicarlas.
     * @param color Color a buscar.
     * @return true si la rueda tiene registrado ese color.
     */
    public boolean hasColor(String color) {
        for (Symbol s : symbols) {
            if (s.getColor().equalsIgnoreCase(color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gira la rueda a una posición ALEATORIA si no está bloqueada.
     * ("Tirón" de casino: Requisitos 4 y 11 sin pasos.)
     */
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
     * Rota la rueda de forma DETERMINISTA `steps` posiciones hacia
     * adelante (cíclico, módulo la cantidad de símbolos registrados).
     * A diferencia de spin(), el mismo `steps` siempre produce el mismo
     * desplazamiento relativo — equivale a "rotar la rueda por j
     * posiciones" en la máquina física. No verifica lock(): esa
     * responsabilidad es de SlotMachine.
     * @param steps Cantidad de posiciones a avanzar (puede ser negativo,
     *        aunque SlotMachine solo permite llamarlo con valores >= 0).
     */
    public void rotate(int steps) {
        if (symbols.isEmpty()) return;
        Symbol current = getVisibleSymbol();
        if (current != null) {
            current.makeInvisible();
        }
        int size = symbols.size();
        visibleSymbolIndex = ((visibleSymbolIndex + steps) % size + size) % size;
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
    
    /**
     * Obtiene el símbolo que se encuentra actualmente activo o visible en esta rueda.
     * @return El objeto Symbol visible o null si la rueda está vacía.
     */
    public Symbol getVisibleSymbol() {
        if (symbols.isEmpty()) return null;
        return symbols.get(visibleSymbolIndex);
    }
    
    /**
     * Fija la rueda para prevenir que gire.
     */
    public void lock() {
        this.locked = true;
    }

    /**
     * Libera la rueda para permitir que gire nuevamente.
     */
    public void unlock() {
        this.locked = false;
    }

    /**
     * Consulta el estado de bloqueo de la rueda.
     * @return true si la rueda está bloqueada.
     */
    public boolean isLocked() {
        return locked;
    }
    
    /**
     * Consulta los símbolos registrados en la rueda.
     * @return Lista de símbolos de la rueda.
     */
    public ArrayList<Symbol> getSymbols() { 
        return symbols; 
    }
    
    /**
     * Consulta la coordenada horizontal de este eje.
     * @return Posición X.
     */
    public int getXPosition() { 
        return xPosition; 
    }

    /**
     * Actualiza la posición física del eje y arrastra todos sus símbolos.
     * @param newX La nueva coordenada X del eje.
     */
    public void updateAxisPosition(int newX) {
        int deltaX = newX - this.xPosition;
        if (deltaX == 0) return;

        this.xPosition = newX;
        for (Symbol s : symbols) {
            s.shiftHorizontal(deltaX);
        }
    }

    /**
     * Hace visible el eje: muestra el cuadrado de color del símbolo activo.
     */
    public void makeVisible() {
        Symbol active = getVisibleSymbol();
        if (active != null) active.makeVisible();
    }

    /**
     * Oculta el eje: oculta el cuadrado de color del símbolo activo.
     */
    public void makeInvisible() {
        Symbol active = getVisibleSymbol();
        if (active != null) active.makeInvisible();
    }

    /**
     * Intercambia los símbolos y el estado de visibilidad con otra rueda,
     * manteniendo inalterable el xPosition de ambos ejes (sin dependencias externas).
     * @param other La otra rueda con la que se intercambiarán los símbolos.
     */
    public void swapContentWith(Wheel other) {
        Symbol thisVisible = this.getVisibleSymbol();
        if (thisVisible != null) thisVisible.makeInvisible();

        Symbol otherVisible = other.getVisibleSymbol();
        if (otherVisible != null) otherVisible.makeInvisible();

        String[] colorsThis = new String[this.symbols.size()];
        for (int i = 0; i < this.symbols.size(); i++) {
            colorsThis[i] = this.symbols.get(i).getColor();
        }

        String[] colorsOther = new String[other.symbols.size()];
        for (int i = 0; i < other.symbols.size(); i++) {
            colorsOther[i] = other.symbols.get(i).getColor();
        }

        int tempIndex = this.visibleSymbolIndex;
        this.visibleSymbolIndex = other.visibleSymbolIndex;
        other.visibleSymbolIndex = tempIndex;

        this.symbols.clear();
        for (int i = 0; i < colorsOther.length; i++) {
            this.symbols.add(new Symbol(colorsOther[i], this.xPosition, 100));
        }

        other.symbols.clear();
        for (int i = 0; i < colorsThis.length; i++) {
            other.symbols.add(new Symbol(colorsThis[i], other.xPosition, 100));
        }
    }
}