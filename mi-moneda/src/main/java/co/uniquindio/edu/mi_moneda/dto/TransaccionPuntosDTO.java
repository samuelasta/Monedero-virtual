package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccionPuntos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionPuntosDTO {
    private String id;
    private TipoTransaccionPuntos tipo;
    private double cantidad;
    private LocalDateTime fecha;
    private String fechaFormateada;
    private String motivo;
    private String cantidadFormateada;

    /**
     * Crea un DTO a partir de la entidad TransaccionPuntos
     */
    public static TransaccionPuntosDTO fromEntity(TransaccionPuntos transaccion) {
        if (transaccion == null) {
            return null;
        }

        TransaccionPuntosDTO dto = new TransaccionPuntosDTO();
        dto.setId(transaccion.getId());
        dto.setTipo(transaccion.getTipo());
        dto.setCantidad(transaccion.getCantidad());
        dto.setFecha(transaccion.getFecha());
        dto.setMotivo(transaccion.getMotivo());

        // Formatear fecha
        if (transaccion.getFecha() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
            dto.setFechaFormateada(transaccion.getFecha().format(formatter));
        }

        // Formatear cantidad
        dto.setCantidadFormateada(String.format("%.0f pts", transaccion.getCantidad()));

        return dto;
    }
}