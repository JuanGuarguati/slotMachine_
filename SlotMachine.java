import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Simulador de Máquina Tragamonedas ajustado estrictamente a los 11 métodos
 * asociados a los 8 Requisitos Funcionales del laboratorio.
 * 
 * @version 1.0 (2026-08)
 */
public class SlotMachine {
    private ArrayList<Wheel> wheels;
    private boolean isVisible;
    private boolean ok;

    /**
     * 1. Requisito 1: Crear una máquina tragamonedas.
     */
    public SlotMachine() {
        this.wheels = new ArrayList<>();
        this.isVisible = false;
        this.ok = true;
    }
        
    /**
     * 2. Requisito 2a: Adicionar una rueda.
     * @param pos Posición de inserción (base 1).
     */
    public void addWheel(int pos) {
        int index = adjustPos(pos, wheels.size() + 1) - 1;
        int xOffset = 50 + (index * 80);
        Wheel newWheel = new Wheel(xOffset);
        wheels.add(index, newWheel);
        this.ok = true;
        refreshVisibility();
    }

    /**
     * 3. Requisito 2b: Eliminar una rueda.
     * @param pos Posición de la rueda a eliminar (base 1).
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para eliminar.");
            return;
        }
        int index = adjustPos(pos, wheels.size()) - 1;
        Wheel removed = wheels.remove(index);
        Symbol s = removed.getVisibleSymbol();
        if (s != null) s.makeInvisible();
        this.ok = true;
        refreshVisibility();
    }

    /**
     * Requisito 10a:Fijar una rueda para que no gire.
     * @param wheel Posición de la rueda a fijar (base 1).
     */
    public void lock(int wheel) {
          if (wheels.isEmpty()) {
            notifyError("No hay ruedas para fijar.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        wheels.get(index).lock();
        this.ok = true;
    }
    
    /**
     * Requisito 10b:Soltar una rueda previamente fijada.
     * @param wheel Posición de la rueda a soltar (base 1).
     */
    public void unlock(int wheel) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para soltar.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        wheels.get(index).unlock();
        this.ok = true;
    }
    
    /**
     * 4. Requisito 3a: Adicionar un símbolo.
     * @param pos Posición del símbolo en las ruedas.
     * @param color Nombre del color en estándar CSS.
     */
    public void addSymbol(int pos, String color) {
        if (wheels.isEmpty()) {
            notifyError("No existen ruedas para adicionar símbolos.");
            return;
        }
        for (Wheel wheel : wheels) {
            wheel.addSymbol(pos, color);
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * 5. Requisito 3b: Eliminar un símbolo por su color.
     * @param symbol Color del símbolo a eliminar.
     */
    public void delSymbol(String symbol) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas registradas.");
            return;
        }
        for (Wheel wheel : wheels) {
            wheel.delSymbol(symbol);
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * 6. Requisito 4: Girar las ruedas de la máquina.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para girar.");
            return;
        }
        for (Wheel w : wheels) {
            w.spin();
        }
        this.ok = true;
        refreshVisibility();
    }

    /**
     * 7. Requisito 5: Consultar los símbolos visibles de la máquina.
     * @return Arreglo con los colores de los símbolos visibles ordenados de izquierda a derecha.
     */
    public String[] configuration() {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            Symbol active = wheels.get(i).getVisibleSymbol();
            config[i] = (active != null) ? active.getColor() : "empty";
        }
        this.ok = true;
        return config;
    }

    /**
     * 8. Requisito 6: Consultar si la configuración es la ganadora (Jackpot).
     * @return true si todos los símbolos visibles coinciden.
     */
    public boolean isJackpot() {
        if (wheels.size() < 2) {
            this.ok = false;
            return false;
        }
        String targetColor = null;
        for (Wheel wheel : wheels) {
            Symbol s = wheel.getVisibleSymbol();
            if (s == null) {
                this.ok = false;
                return false;
            }
            if (targetColor == null) {
                targetColor = s.getColor();
            } else if (!targetColor.equalsIgnoreCase(s.getColor())) {
                this.ok = false;
                return false;
            }
        }
        this.ok = true;
        if (this.isVisible) {
            JOptionPane.showMessageDialog(null, "¡ESTADO GANADOR (JACKPOT)!", "Slot Machine", JOptionPane.INFORMATION_MESSAGE);
        }
        return true;
    }

    /**
     * 9. Requisito 7a: Hacer visible el simulador.
     */
    public void makeVisible() {
        this.isVisible = true;
        refreshVisibility();
        this.ok = true;
    }

    /**
     * 10. Requisito 7b: Hacer invisible el simulador.
     */
    public void makeInvisible() {
        this.isVisible = false;
        for (Wheel wheel : wheels) {
            Symbol active = wheel.getVisibleSymbol();
            if (active != null) active.makeInvisible();
        }
        this.ok = true;
    }

    /**
     * 11. Requisito 8: Terminar el simulador.
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }
    
    /**
     * Requisito 9: Intercambiar dos ruedas de posición.
     * @param wheel1 Posición de la primera rueda (desde 1).
     * @param wheel2 Posición de la segunda rueda (desde 1).
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.size() < 2) {
            notifyError("Se necesitan al menos dos ruedas para intercambiar.");
            return;
        }
        int index1 = adjustPos(wheel1, wheels.size()) - 1;
        int index2 = adjustPos(wheel2, wheels.size()) - 1;
        Collections.swap(wheels, index1, index2);
        this.ok = true;
    }
        
    /**
     * 12. Requisito 11: Rotar una sola rueda a un color aleatorio.
     * @param wheel Posición de la rueda a girar (desde 1).
     */
    public void spin(int wheel) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para girar.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        wheels.get(index).spin();
        this.ok = true;
        refreshVisibility();
    }
    
    /**
     * 13. Requisito 11: Rotar una rueda un número determinado de pasos.
     * Si el simulador está visible, cada paso se muestra individualmente.
     * @param wheel Posición de la rueda a girar (base 1).
     * @param steps Número de pasos a girar. Debe ser mayor o igual a 0.
     */
    public void spin(int wheel, int steps) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas para girar.");
            return;
        }
        if (steps < 0) {
            notifyError("El número de pasos no puede ser negativo.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        Wheel w = wheels.get(index);
        for (int i = 0; i < steps; i++) {
            w.spin();
            if (this.isVisible) {
                Symbol active = w.getVisibleSymbol();
                if (active != null) active.makeVisible();
            }
        }
        this.ok = true;
    }
    
    /**
     * 14. Requisito 12: Dejar la máquina en una configuración dada de colores.
     * @param setSymbols Arreglo con el color deseado para cada rueda,
     * en el mismo orden en que están las ruedas en la máquina.
     */
    public void spin(String[] setSymbols) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas registradas.");
            return;
        }
        if (setSymbols.length != wheels.size()) {
            notifyError("El número de colores no coincide con el número de ruedas.");
            return;
        }
        for (int i = 0; i < wheels.size(); i++) {
            boolean found = wheels.get(i).setVisibleColor(setSymbols[i]);
            if (!found) {
                notifyError("Una rueda no contiene el color indicado.");
                return;
            }
        }
        this.ok = true;
        refreshVisibility();
    }
    
    /**
     * 15. Dejar visible en una rueda específica el símbolo del color indicado.
     * @param wheel Posición de la rueda (base 1).
     * @param symbol Color del símbolo que se desea dejar visible.
     */
    public void placeSymbol(int wheel, String symbol) {
        if (wheels.isEmpty()) {
            notifyError("No hay ruedas registradas.");
            return;
        }
        int index = adjustPos(wheel, wheels.size()) - 1;
        boolean found = wheels.get(index).setVisibleColor(symbol);
        if (!found) {
            notifyError("La rueda no contiene ese color.");
            return;
        }
        this.ok = true;
        refreshVisibility();
    }
    
    /**
     * 16. Consultar los colores de símbolos registrados en la máquina.
     * @return Arreglo con los colores disponibles en las ruedas.
     */
    public String[] symbols() {
        if (wheels.isEmpty()) return new String[0];
        ArrayList<Symbol> list = wheels.get(0).getSymbols();
        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i).getColor();
        }
        this.ok = true;
        return result;
    }
    
    /**
     * 17. Consultar la cantidad de colores distintos usados en la máquina.
     * @return Número de colores diferentes registrados.
     */
    public int distinctSymbols() {
        java.util.Set<String> distinct = new java.util.HashSet<>();
        for (Wheel w : wheels) {
            for (Symbol s : w.getSymbols()) {
                distinct.add(s.getColor().toLowerCase());
            }
        }
        this.ok = true;
        return distinct.size();
    }

    /**
     * Indica si se logró realizar la última operación.
     * @return true si la última operación fue exitosa.
     */
    public boolean ok() {
        return this.ok;
    }

    private int adjustPos(int pos, int max) {
        if (pos < 1) return 1;
        if (pos > max) return max;
        return pos;
    }

    private void refreshVisibility() {
        if (this.isVisible) {
            for (Wheel wheel : wheels) {
                Symbol active = wheel.getVisibleSymbol();
                if (active != null) active.makeVisible();
            }
        }
    }

    private void notifyError(String msg) {
        this.ok = false;
        if (this.isVisible) {
            JOptionPane.showMessageDialog(null, msg, "Error Slot Machine", JOptionPane.ERROR_MESSAGE);
        }
    }
}