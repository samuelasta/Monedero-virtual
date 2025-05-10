package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import com.fasterxml.jackson.annotation.JsonBackReference;
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

    // Esto evita la recursión infinita durante la serialización/deserialización ya que le dice a Jackson que no intente serializar nuevamente el objeto Cliente cuando está procesando un objeto Puntos.
    @JsonBackReference
    private Cliente cliente;
    private int puntosAcumulados;

    private SimpleList<TransaccionPuntos> historialPuntos;

}
