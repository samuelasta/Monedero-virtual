package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
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

    @JsonManagedReference  // Esta anotación indica que esta es la parte "principal" de la referencia
    @Transient
    private Puntos puntos;

    private String rango;

    @JsonManagedReference
    //@Transient
    private SimpleList<Monedero> monederos;
    //@Transient
    private DoubleList<Transaccion> historialTransacciones;
    private QueueTransactionProgramed transaccionesProgramadas;
    private SimpleList<Notificacion> notificaciones;

}
