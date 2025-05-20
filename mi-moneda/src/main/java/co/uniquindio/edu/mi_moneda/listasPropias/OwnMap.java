package co.uniquindio.edu.mi_moneda.listasPropias;

import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import co.uniquindio.edu.mi_moneda.model.enums.CategoriaGasto;
import lombok.Data;

import java.util.UUID;

@Data
public class OwnMap {

    private String id = UUID.randomUUID().toString();
    private int size;
    private NodeMap firstNode;
    private NodeMap lastNode;

    public OwnMap() {
        this.size = 0;
        this.firstNode = null;
        this.lastNode = null;
    }
    // metodo para añadir un nuevo map a la lista
    public void put(CategoriaGasto categoriaGasto, double valor) {
        //si ya hay alguna creada con esa clave:
        NodeMap nodeAux = new NodeMap(categoriaGasto, valor);
        while(nodeAux != null) {
            if(nodeAux.getCategoriaGasto().equals(categoriaGasto)) {
                nodeAux.setMonto(nodeAux.getMonto() + valor);
                return;
            }
            nodeAux = nodeAux.getNextNode();
        }
        //si aun no está creada con esa clave
        NodeMap newNode = new NodeMap(categoriaGasto, valor);
        if(firstNode == null) {
            firstNode = newNode;
            lastNode = newNode;
        }
        else {
            lastNode.setNextNode(newNode);
            lastNode = newNode;
        }
        size++;
    }
    // devuelve el dinero que tenga cada categoria de gasto
    public double getAmountCategory(CategoriaGasto categoriaGasto) {
        NodeMap nodeAux = firstNode;
        while(nodeAux != null) {
            if(nodeAux.getCategoriaGasto().equals(categoriaGasto)) {
                return nodeAux.getMonto();
            }
            nodeAux = nodeAux.getNextNode();
        }
        return 0;
    }

    public boolean search(CategoriaGasto categoriaGasto) {
        NodeMap nodeAux = firstNode;
        while(nodeAux != null) {
            if(nodeAux.getCategoriaGasto().equals(categoriaGasto)) {
                return true;
            }
            nodeAux = nodeAux.getNextNode();
        }
        return false;
    }

}
