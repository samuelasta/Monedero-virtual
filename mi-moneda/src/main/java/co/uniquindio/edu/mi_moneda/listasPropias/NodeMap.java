package co.uniquindio.edu.mi_moneda.listasPropias;

import co.uniquindio.edu.mi_moneda.model.enums.CategoriaGasto;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import lombok.Data;

@Data
public class NodeMap {
    private CategoriaGasto categoriaGasto;
    private double monto;
    private NodeMap nextNode;

    public NodeMap(CategoriaGasto categoriaGasto, double monto) {
        this.categoriaGasto = categoriaGasto;
        this.monto = monto;
        this.nextNode = null;
    }
}
