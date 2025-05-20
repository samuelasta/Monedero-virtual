package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.model.Puntos;
import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuntosDTO {
    private String id;
    private double puntosAcumulados;
    private List<TransaccionPuntosDTO> historialPuntos;

    /**
     * Crea un DTO a partir de la entidad Puntos
     */
    public static PuntosDTO fromEntity(Puntos puntos) {
        if (puntos == null) {
            return null;
        }

        PuntosDTO dto = new PuntosDTO();
        dto.setId(puntos.getId());
        dto.setPuntosAcumulados(puntos.getPuntosAcumulados());

        // Convertir historial de puntos
        List<TransaccionPuntosDTO> historialDto = new ArrayList<>();
        if (puntos.getHistorialPuntos() != null && !puntos.getHistorialPuntos().isEmpty()) {
            Node<TransaccionPuntos> current = puntos.getHistorialPuntos().getFirstNode();
            while (current != null) {
                historialDto.add(TransaccionPuntosDTO.fromEntity(current.getValue()));
                current = current.getNextNodo();
            }
        }
        dto.setHistorialPuntos(historialDto);

        return dto;
    }
}