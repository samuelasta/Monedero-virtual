package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import co.uniquindio.edu.mi_moneda.model.enums.CategoriaGasto;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Transacciones")
public class Transaccion implements identificable {

    @Id //identificador en la base de datos
    private String id;

    //atributos principales de la transaccion
    private TipoTransaccion tipo;
    private double monto;
    private LocalDateTime fecha;
    private String descripcion;
    private CategoriaGasto categoria;

    //relaciones con los otros objetos(monederos)
    private Monedero origen;
    private Monedero destino;
    private boolean activa;

    @Override
    public String getId() {
        return id;
    }
}
