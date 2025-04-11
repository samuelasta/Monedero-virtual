package co.uniquindio.edu.mi_moneda.listasPropias;

import lombok.Data;

@Data
public class Node<T>{
    T value;
    Node<T> nextNodo;

    public Node(T value) {
        this.value = value;
        this.nextNodo = null;
    }
}
