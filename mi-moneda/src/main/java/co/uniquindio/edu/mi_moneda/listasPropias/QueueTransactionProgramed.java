package co.uniquindio.edu.mi_moneda.listasPropias;

import co.uniquindio.edu.mi_moneda.model.TransaccionProgramada;
import lombok.Data;


@Data
public class QueueTransactionProgramed {

    private int size;
    private NodeQueue firstNode;
    private NodeQueue lastNode;

    public QueueTransactionProgramed() {
        this.size = 0;
        this.firstNode = null;
        this.lastNode = null;
    }

    // agrega un elemento nuevo a la cola
    public void enqueue(TransaccionProgramada transaccion) {
        NodeQueue newNode = new NodeQueue(transaccion);

        if (isEmpty()) {
            firstNode = newNode;
            lastNode = newNode;
        } else if (transaccion.compareTo(firstNode.getTransaccion()) < 0) {
            // Insertar al inicio
            newNode.setNextNode(firstNode);
            firstNode = newNode;
        } else {
            NodeQueue aux = firstNode;
            while (aux.getNextNode() != null && transaccion.compareTo(aux.getNextNode().getTransaccion()) > 0) {
                aux = aux.getNextNode();
            }

            // Insertar después del aux
            newNode.setNextNode(aux.getNextNode());
            aux.setNextNode(newNode);

            // Si fue al final, actualiza lastNode
            if (newNode.getNextNode() == null) {
                lastNode = newNode;
            }
            size++;
        }
    }

    //elimina un elemento de la cola
    public void remove(TransaccionProgramada transaccion) {
        // elimina el primer nodo
        if (firstNode.getTransaccion().getId().equals(transaccion.getId())) {
            firstNode = firstNode.getNextNode();
            if (firstNode == null) {
                lastNode = null; // la lista quedó vacía
            }
            size--;
            return;
        }
        NodeQueue current = firstNode;

        // para encontrar el nodo anterior al que vamos a eliminar
        while (current.getNextNode() != null) {
            if (current.getNextNode().getTransaccion().getId().equals(transaccion.getId())) {
                // Si el nodo a eliminar es el último
                if (current.getNextNode() == lastNode) {
                    lastNode = current;
                }
                // Saltamos el nodo a eliminar
                current.setNextNode(current.getNextNode().getNextNode());
                size--;
                return;
            }
            current = current.getNextNode();
        }
    }

    public boolean search (String id){
            boolean found = false;
            NodeQueue aux = firstNode;
            while (aux != null) {
                if (id.equals(aux.getTransaccion().getId())) {
                    return true;
                }
                aux = aux.getNextNode();
            }
            return found;
        }

        public boolean isEmpty () {
            return firstNode == null;
        }
    }

