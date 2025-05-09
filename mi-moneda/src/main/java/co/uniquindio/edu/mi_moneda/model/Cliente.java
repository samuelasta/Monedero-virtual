package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor//crea el constructor con todos los parametros
@NoArgsConstructor//crea el constructor vacío
@Data  //@Data crea los getters, setters, toString, EqualsAndHashCode, etc
@ToString(exclude = {"puntos", "monederos", "historialTransacciones", "notificaciones", "transaccionesProgramadas"})
@Document(collection = "Cliente") //@Document significa que será un bloque en la base de datos
public class Cliente implements identificable {

    @Id//le asigna un identificador único al documento en mongo(tambien monngo podria crear uno default)
    private String id;

    private String nombre;
    private String email;
    private String password;
    @JsonIgnore // Excluir de la serialización JSON
    @Transient // Excluir de la persistencia MongoDB
    private Puntos puntos;
    private String rango;

    @JsonIgnore // Excluir de la serialización JSON
    @Transient // Excluir de la persistencia MongoDB
    private SimpleList<Monedero> monederos;
    @JsonIgnore // Excluir de la serialización JSON
    @Transient // Excluir de la persistencia MongoDB
    private DoubleList<Transaccion> historialTransacciones;

    //es una lista(cola de prioridad)
    @JsonIgnore // Excluir de la serialización JSON
    @Transient // Excluir de la persistencia MongoDB
    private QueueTransactionProgramed transaccionesProgramadas;
    @JsonIgnore // Excluir de la serialización JSON
    @Transient // Excluir de la persistencia MongoDB
    private SimpleList<Notificacion> notificaciones;

}
