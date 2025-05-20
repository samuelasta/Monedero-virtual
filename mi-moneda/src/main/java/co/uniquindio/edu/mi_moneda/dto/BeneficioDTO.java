package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.model.Beneficio;
import co.uniquindio.edu.mi_moneda.model.enums.TipoBeneficio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeneficioDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private double costePuntos;
    private TipoBeneficio tipo;
    private double valor;
    private String costePuntosFormateado;

    /**
     * Crea un DTO a partir de la entidad Beneficio
     */
    public static BeneficioDTO fromEntity(Beneficio beneficio) {
        if (beneficio == null) {
            return null;
        }

        BeneficioDTO dto = new BeneficioDTO();
        dto.setId(beneficio.getId());
        dto.setNombre(beneficio.getNombre());
        dto.setDescripcion(beneficio.getDescripcion());
        dto.setCostePuntos(beneficio.getCostePuntos());
        dto.setTipo(beneficio.getTipo());
        dto.setValor(beneficio.getValor());

        // Formatear coste de puntos
        dto.setCostePuntosFormateado(String.format("%.0f pts", beneficio.getCostePuntos()));

        return dto;
    }
}