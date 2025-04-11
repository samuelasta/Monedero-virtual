package co.uniquindio.edu.mi_moneda.listasPropias;

import lombok.Data;

//  List<Transaccion> historialTransacciones

@Data
public class DoubleNode<T>{
    private T value;
    private DoubleNode<T> nextNodo;
    private DoubleNode<T> previousNodo;

    public DoubleNode(T value) {
        this.value = value;
        this.nextNodo = null;
        this.previousNodo = null;
    }
}
