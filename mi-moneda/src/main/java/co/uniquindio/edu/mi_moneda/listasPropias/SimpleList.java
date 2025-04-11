package co.uniquindio.edu.mi_moneda.listasPropias;

import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import lombok.Data;

import java.util.NoSuchElementException;

//  List<Monedero> monederos
//  List<TransaccionPuntos> historialPuntos

@Data
public class SimpleList<T extends identificable>{
    private int size;
    private Node<T> firstNode;
    private Node<T> lastNode;

    public SimpleList() {
        this.size = 0;
        this.firstNode = null;
        this.lastNode = null;
    }

    public void add(T value) {
        Node<T> newNode = new Node<>(value);
        if(isEmpty()){
            firstNode = newNode;
            lastNode = newNode;
        }
        else{
            lastNode.setNextNodo(newNode);
            lastNode = newNode;
        }
        size++;
    }

    public void remove(String id) {
        Node<T> aux = firstNode;
        Node<T> previous = null;

        while (aux != null) {
            if (aux.getValue().getId().equals(id)) {
                if (previous == null) {
                    // Eliminar el primero
                    firstNode = aux.getNextNodo();
                } else {
                    previous.setNextNodo(aux.getNextNodo());
                }
                size--;
                return;
            }
            previous = aux;
            aux = aux.getNextNodo();
        }
    }

    public boolean search(String id) {
        Node<T> aux = firstNode;
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
