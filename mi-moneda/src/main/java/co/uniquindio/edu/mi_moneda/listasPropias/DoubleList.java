package co.uniquindio.edu.mi_moneda.listasPropias;

import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//
@Data
public class DoubleList<T extends identificable>{
    private int size;
    private DoubleNode<T> firstNode;
    private DoubleNode<T> lastNode;

    public DoubleList() {
        this.size = 0;
        this.firstNode = null;
        this.lastNode = null;
    }

    public void add(T value) {
        DoubleNode<T> newNode = new DoubleNode<>(value);
        if (isEmpty()) {
            firstNode = newNode;
            lastNode = newNode;
        }
        else {
            firstNode.setNextNodo(newNode);
            newNode.setPreviousNodo(lastNode);
            lastNode = newNode;
        }
        size++;
    }

    public void remove(String id){
        DoubleNode<T> aux = firstNode;

        while (aux != null) {
            if (aux.getValue().getId().equals(id)) {

                // Si es el primer nodo
                if (aux == firstNode) {
                    firstNode = aux.getNextNodo();
                    if (firstNode != null) {
                        firstNode.setPreviousNodo(null);
                    } else {
                        lastNode = null; // lista queda vacía
                    }

                    // Si es el último nodo
                } else if (aux == lastNode) {
                    lastNode = aux.getPreviousNodo();
                    lastNode.setNextNodo(null);

                    // Nodo en medio
                } else {
                    aux.getPreviousNodo().setNextNodo(aux.getNextNodo());
                    aux.getNextNodo().setPreviousNodo(aux.getPreviousNodo());
                }
                size--;
                return;
            }
            aux = aux.getNextNodo();
        }
    }

    public boolean search(String id) {
        DoubleNode<T> aux = firstNode;
        while (aux != null) {
            if (aux.getValue().getId().equals(id)) {
                return true;
            }
            aux = aux.getNextNodo();
        }
        return false;
    }

    public boolean isEmpty() {
        return firstNode == null;
    }
}
