import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Simulador de Máquina Tragamonedas que coordina un conjunto de ruedas
 * y sus símbolos asociados.
 * Administra el estado visual en el Canvas, evalúa jugadas ganadoras (Jackpot)
 * y notifica errores o eventos mediante ventanas modales según el estado de visibilidad.
 * 
 * @author Juan David Guarguati, Juan Pablo Suarez Rubiano
 * @version 1.0 (2026-08)
 */
public class SlotMachine {
    
    private ArrayList<Wheel> wheels;    
    private boolean isVisible;    
    private boolean ok;

    /**
     * Requisito 1: Crear una máquina tragamonedas.
     * Propósito: Inicializar una nueva máquina sin ruedas y en modo invisible.
     * Estado: Instancia `wheels` vacía, asigna `isVisible = false` y establece `ok = true`.
     */
    public SlotMachine() {
        this.wheels = new ArrayList<>();
        this.isVisible = false;
        this.ok = true;
    }

    /**
     * Requisito 2a: Adicionar una rueda.
     * Propósito: Insertar una nueva rueda en la posición especificada recalculando la posición gráfica X.
     * Estado: Incrementa la lista `wheels`. Si `pos` excede los límites, se ajusta automáticamente.
     * Actualiza el Canvas si el simulador está visible y marca `ok = true`.
     * 
     * @param pos Posición de inserción deseada (base 1).
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
     * Requisito 2b: Eliminar una rueda.
     * Propósito: Remover la rueda ubicada en la posición indicada y ocultar su figura gráfica.
     * Estado: Modifica la lista `wheels`. Si la máquina está vacía, falla asignando `ok = false`.
     * De lo contrario, remueve la rueda, oculta su símbolo y asigna `ok = true`.
     * 
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
     * Requisito 3a: Adicionar un símbolo.
     * Propósito: Insertar un símbolo del color dado en la misma posición de todas las ruedas existentes.
     * Estado: Modifica la secuencia interna de cada `Wheel`. Asigna `ok = true` si existen ruedas,
     * o `ok = false` y despliega error si no hay ruedas registradas.
     * 
     * @param pos Posición de inserción del símbolo en la rueda (base 1).
     * @param color Nombre del color del símbolo en estándar CSS.
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
     * Requisito 3b: Eliminar un símbolo por su color.
     * Propósito: Remover todas las instancias de un símbolo específico en todas las ruedas.
     * Estado: Actualiza la lista de símbolos de cada `Wheel`. Marca `ok = true` si la operación
     * se ejecuta sobre ruedas existentes, o `ok = false` si la máquina está vacía.
     * 
     * @param symbol Nombre del color del símbolo a remover.
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
     * Requisito 4: Girar las ruedas de la máquina.
     * Propósito: Avanzar simultáneamente todas las ruedas al siguiente símbolo de su secuencia.
     * Estado: Cambia el símbolo activo en cada `Wheel`. Refresca la vista en el Canvas y asigna `ok = true`.
     * Si no hay ruedas, marca `ok = false` y notifica el error.
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
     * Requisito 5: Consultar los símbolos visibles de la máquina.
     * Propósito: Obtener una representación en texto del estado actual visible de izquierda a derecha.
     * Estado: No modifica la estructura. Asigna `ok = true`.
     * 
     * @return Arreglo de `String` con los colores de los símbolos actualmente activos en cada rueda.
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
     * Requisito 6: Consultar si la configuración es la ganadora (Jackpot).
     * Propósito: Evaluar si todas las ruedas muestran simultáneamente el mismo símbolo (color).
     * Estado: Asigna `ok = true` si la evaluación es válida (al menos 2 ruedas con símbolos coincidentes).
     * Si el simulador es visible y resulta ganador, despliega un mensaje emergente `JOptionPane`.
     * 
     * @return `true` si todos los símbolos visibles coinciden; `false` en caso contrario o si hay menos de 2 ruedas.
     */
    public boolean isjackpot() {
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
     * Requisito 7a: Hacer visible el simulador.
     * Propósito: Activar la representación gráfica de la máquina y sus símbolos en el Canvas.
     * Estado: Cambia `isVisible = true`, hace visibles los símbolos activos de las ruedas y asigna `ok = true`.
     */
    public void makeVisible() {
        this.isVisible = true;
        refreshVisibility();
        this.ok = true;
    }

    /**
     * Requisito 7b: Hacer invisible el simulador.
     * Propósito: Ocultar la representación gráfica del simulador en el Canvas sin perder el estado.
     * Estado: Cambia `isVisible = false`, oculta las figuras en pantalla y asigna `ok = true`.
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
     * Requisito 8: Terminar el simulador.
     * Propósito: Cerrar la aplicación y liberar los recursos gráficos.
     * Estado: Oculta el Canvas y finaliza la ejecución de la JVM (`System.exit(0)`).
     */
    public void exit() {
        makeInvisible();
        System.exit(0);
    }

    /**
     * Consulta de verificación de estado de la última operación.
     * Propósito: Permitir al usuario o pruebas unitarias conocer si la acción previa se realizó exitosamente.
     * Estado: No altera el estado interno de la máquina.
     * 
     * @return `true` si la última operación fue exitosa; `false` si ocurrió un error o la acción fue inválida.
     */
    public boolean ok() {
        return this.ok;
    }

    /**
     * Ajusta un índice ingresado por el usuario para garantizar que se mantenga dentro de los límites válidos.
     * 
     * @param pos Posición solicitada por el usuario.
     * @param max Límite superior permitido.
     * @return La posición ajustada (mínimo 1, máximo `max`).
     */
    private int adjustPos(int pos, int max) {
        if (pos < 1) return 1;
        if (pos > max) return max;
        return pos;
    }

    /**
     * Redibuja en el Canvas los símbolos activos de cada rueda si la máquina se encuentra visible.
     */
    private void refreshVisibility() {
        if (this.isVisible) {
            for (Wheel wheel : wheels) {
                Symbol active = wheel.getVisibleSymbol();
                if (active != null) active.makeVisible();
            }
        }
    }

    /**
     * Registra un error de operación, actualizando la bandera de estado y mostrando
     * una alerta emergente si la máquina está visible.
     * 
     * @param msg Mensaje descriptivo del error ocurrido.
     */
    private void notifyError(String msg) {
        this.ok = false;
        if (this.isVisible) {
            JOptionPane.showMessageDialog(null, msg, "Error Slot Machine", JOptionPane.ERROR_MESSAGE);
        }
    }
}