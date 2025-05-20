package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.model.Notificacion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private String id;
    private TipoNotificacion tipo;
    private String mensaje;
    private LocalDateTime fecha;
    private String fechaFormateada;
    private boolean leida;

    /**
     * Crea un DTO a partir de la entidad Notificacion
     */
    public static NotificacionDTO fromEntity(Notificacion notificacion) {
        if (notificacion == null) {
            return null;
        }

        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(notificacion.getId());
        dto.setTipo(notificacion.getTipo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setFecha(notificacion.getFecha());
        dto.setLeida(notificacion.isLeida());

        // Formatear fecha
        if (notificacion.getFecha() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
            dto.setFechaFormateada(notificacion.getFecha().format(formatter));
        }

        return dto;
    }
}