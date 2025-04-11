package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "Monederos")
public class Monedero implements identificable {

    @Id
    private String id;

    private String nombre;
    private Cliente propietario;
    private TipoMonedero tipoMonedero;
    private double saldo;
    private DoubleList<Transaccion> historialTransacciones;

    @Override
    public String getId() {
        return id;
    }
}
