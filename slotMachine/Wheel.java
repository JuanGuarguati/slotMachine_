import java.util.ArrayList;

/**
 * Representa una rueda dentro de la máquina tragamonedas.
 * Almacena una secuencia ordenada de símbolos, gestiona el símbolo activo visible
 * y controla el desplazamiento en el Canvas mediante su posición horizontal.
 * 
 * @author Juan David Guarguati, Juan Pablo Suarez Rubiano
 * @version 1.0
 */
public class Wheel {
    
    private ArrayList<Symbol> symbols;
    private int visibleSymbolIndex;    
    private int xPosition;

    /**
     * Construye una nueva rueda vacía en una coordenada horizontal específica.
     * Propósito: Inicializar la rueda sin símbolos y establecer el punto base en pantalla.
     * Estado: `symbols` queda vacío y `visibleSymbolIndex` inicia en 0.
     * 
     * @param xPosition Posición horizontal (eje X) dentro del Canvas.
     */
    public Wheel(int xPosition) {
        this.symbols = new ArrayList<>();
        this.visibleSymbolIndex = 0;
        this.xPosition = xPosition;
    }

    /**
     * Inserta un nuevo símbolo en la posición deseada dentro de la rueda.
     * Propósito: Permitir la adición de figuras/colores respetando los límites de inserción.
     * Estado: Incrementa el tamaño de `symbols`. Ajusta posiciones menores o iguales a 1 
     * al inicio, y posiciones mayores al tamaño al final de la lista.
     * 
     * @param pos Posición de inserción deseada (base 1).
     * @param color Nombre del color del símbolo en estándar CSS.
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
     * Elimina todas las apariciones de un símbolo que coincidan con el color especificado.
     * Propósito: Limpiar o reconfigurar la secuencia de símbolos de la rueda.
     * Estado: Modifica la lista `symbols`. Si el índice visible queda fuera de rango 
     * tras la eliminación, reinicia `visibleSymbolIndex` a 0 para prevenir un `IndexOutOfBoundsException`.
     * 
     * @param symbolColor Nombre del color del símbolo que se desea remover.
     */
    public void delSymbol(String symbolColor) {
        symbols.removeIf(s -> s.getColor().equalsIgnoreCase(symbolColor));
        if (visibleSymbolIndex >= symbols.size() && !symbols.isEmpty()) {
            visibleSymbolIndex = 0;
        }
    }

    /**
     * Avanza de forma circular al siguiente símbolo de la rueda.
     * Propósito: Simular la rotación de la rueda cambiando el símbolo activo.
     * Estado: Oculta visualmente el símbolo actual, actualiza `visibleSymbolIndex` 
     * mediante la operación módulo para reiniciar el ciclo al llegar al final.
     */
    public void spin() {
        if (!symbols.isEmpty()) {
            Symbol current = getVisibleSymbol();
            if (current != null) {
                current.makeInvisible();
            }
            visibleSymbolIndex = (visibleSymbolIndex + 1) % symbols.size();
        }
    }

    /**
     * Consulta el símbolo que se encuentra actualmente al frente o visible en la rueda.
     * Propósito: Proporcionar acceso al estado actual de la rueda sin alterar su estructura.
     * Estado: No modifica el estado interno.
     * 
     * @return El objeto `Symbol` en la posición actual, o `null` si la rueda no tiene símbolos.
     */
    public Symbol getVisibleSymbol() {
        if (symbols.isEmpty()) return null;
        return symbols.get(visibleSymbolIndex);
    }

    /**
     * Obtiene la colección completa de símbolos almacenados en la rueda.
     * Propósito: Permitir lecturas o validaciones sobre toda la secuencia.
     * Estado: No modifica el estado interno.
     * 
     * @return Lista con los símbolos de la rueda.
     */
    public ArrayList<Symbol> getSymbols() { 
        return symbols; 
    }

    /**
     * Consulta la coordenada X asignada a la rueda.
     * Propósito: Conocer la ubicación gráfica de la rueda en el Canvas.
     * Estado: No modifica el estado interno.
     * 
     * @return Posición horizontal entera.
     */
    public int getXPosition() { 
        return xPosition; 
    }
}