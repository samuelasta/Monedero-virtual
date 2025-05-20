package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.CategoriaGasto;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    private String id;
    private TipoTransaccion tipo;
    private double monto;
    private LocalDateTime fecha;
    private String fechaFormateada;
    private String descripcion;
    private CategoriaGasto categoria;
    private String origenNombre;
    private String destinoNombre;
    private boolean activa;
    private String montoFormateado;

    /**
     * Crea un DTO a partir de la entidad Transaccion
     */
    public static TransaccionDTO fromEntity(Transaccion transaccion) {
        if (transaccion == null) {
            return null;
        }

        TransaccionDTO dto = new TransaccionDTO();
        dto.setId(transaccion.getId());
        dto.setTipo(transaccion.getTipo());
        dto.setMonto(transaccion.getMonto());
        dto.setFecha(transaccion.getFecha());
        dto.setDescripcion(transaccion.getDescripcion());
        dto.setCategoria(transaccion.getCategoria());
        dto.setActiva(transaccion.isActiva());

        // Formatear fecha
        if (transaccion.getFecha() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
            dto.setFechaFormateada(transaccion.getFecha().format(formatter));
        }

        // Formatear monto
        dto.setMontoFormateado(String.format("%,.2f", transaccion.getMonto()));

        // Nombres de monederos
        if (transaccion.getOrigen() != null) {
            dto.setOrigenNombre(transaccion.getOrigen().getNombre());
        }

        if (transaccion.getDestino() != null) {
            dto.setDestinoNombre(transaccion.getDestino().getNombre());
        }

        return dto;
    }
}