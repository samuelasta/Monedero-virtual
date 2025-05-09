package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "cliente")
@Document
public class Puntos {

    @Id
    private String id;

    private Cliente cliente;
    private int puntosAcumulados;

    private SimpleList<TransaccionPuntos> historialPuntos;

}
