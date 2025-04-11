package co.uniquindio.edu.mi_moneda.listasPropias;

import co.uniquindio.edu.mi_moneda.model.TransaccionProgramada;
import lombok.Data;

@Data
public class NodeQueue {
    private TransaccionProgramada transaccion;
    private NodeQueue nextNode;

    public NodeQueue(TransaccionProgramada transaccion) {
        this.transaccion = transaccion;
        this.nextNode = null;
    }

}
