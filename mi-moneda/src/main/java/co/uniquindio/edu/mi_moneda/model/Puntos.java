package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class Puntos {

    @Id
    private String id;

    private Cliente cliente;
    private int puntosAcumulados;

    private SimpleList<TransaccionPuntos> historialPuntos;

}
