package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import co.uniquindio.edu.mi_moneda.model.enums.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Notificaciones")
public class Notificacion implements identificable {

    private String id;
    private TipoNotificacion tipo;
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leida;
    private Transaccion transaccionAsociada;

    @Override
    public String getId() {
        return id;
    }
}
