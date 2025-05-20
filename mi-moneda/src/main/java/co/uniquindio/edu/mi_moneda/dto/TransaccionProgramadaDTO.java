package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.model.TransaccionProgramada;
import co.uniquindio.edu.mi_moneda.model.enums.Periodo;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionProgramadaDTO {
    private String id;
    private TipoTransaccion tipo;
    private double monto;
    private LocalDateTime fechaProgramada;
    private String descripcion;
    private String origenNombre;
    private String destinoNombre;
    private boolean recurrente;
    private Periodo periodoRecurrencia;
    private int prioridad;
    private String montoFormateado;
    private String fechaFormateada;

    /**
     * Crea un DTO a partir de la entidad TransaccionProgramada
     */
    public static TransaccionProgramadaDTO fromEntity(TransaccionProgramada programada) {
        if (programada == null) {
            return null;
        }

        TransaccionProgramadaDTO dto = new TransaccionProgramadaDTO();
        dto.setId(programada.getId());
        dto.setTipo(programada.getTipo());
        dto.setMonto(programada.getMonto());
        dto.setFechaProgramada(programada.getFechaProgramada());
        dto.setDescripcion(programada.getDescripcion());
        dto.setRecurrente(programada.isRecurrente());
        dto.setPeriodoRecurrencia(programada.getPeriodoRecurrencia());
        dto.setPrioridad(programada.getPrioridad());

        // Formatear fecha
        if (programada.getFechaProgramada() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
            dto.setFechaFormateada(programada.getFechaProgramada().format(formatter));
        }

        // Formatear monto
        dto.setMontoFormateado(String.format("%,.2f", programada.getMonto()));

        // Nombres de monederos
        if (programada.getOrigen() != null) {
            dto.setOrigenNombre(programada.getOrigen().getNombre());
        }

        if (programada.getDestino() != null) {
            dto.setDestinoNombre(programada.getDestino().getNombre());
        }

        return dto;
    }
}