package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.OwnMap;
import co.uniquindio.edu.mi_moneda.model.enums.CategoriaGasto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "Patron de gasto")
public class PatronGasto {

    @Id
    private String id;

    private Cliente cliente;

    // es una lista (Map)
    private OwnMap distribucionGastos;
    private LocalDateTime periodo;
}
