package co.uniquindio.edu.mi_moneda.model;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccionPuntos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
//import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Transacciones de puntos")
public class TransaccionPuntos {

    @Id
    private String id;

    private TipoTransaccionPuntos tipo;
    private int cantidad;
    private LocalDateTime fecha;
    private String motivo;

    private Monedero monederoOrigen;
    private Monedero monederoDestino;

    //@Nullable//cuando se instancie la clase, es opcional que tenga una transaccion asociada
    private Transaccion transaccionAsociada;

}
