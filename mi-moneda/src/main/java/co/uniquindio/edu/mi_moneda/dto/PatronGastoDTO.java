package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.listasPropias.NodeMap;
import co.uniquindio.edu.mi_moneda.model.PatronGasto;
import co.uniquindio.edu.mi_moneda.model.enums.CategoriaGasto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatronGastoDTO {
    private String id;
    private String clienteNombre;
    private Map<String, Double> distribucionGastos;
    private LocalDateTime periodo;

    /**
     * Crea un DTO a partir de la entidad PatronGasto
     */
    public static PatronGastoDTO fromEntity(PatronGasto patronGasto) {
        if (patronGasto == null) {
            return null;
        }

        PatronGastoDTO dto = new PatronGastoDTO();
        dto.setId(patronGasto.getId());
        dto.setPeriodo(patronGasto.getPeriodo());

        // Nombre del cliente
        if (patronGasto.getCliente() != null) {
            dto.setClienteNombre(patronGasto.getCliente().getNombre());
        }

        // Convertir OwnMap a Map estándar
        Map<String, Double> distribucion = new HashMap<>();
        if (patronGasto.getDistribucionGastos() != null) {
            NodeMap current = patronGasto.getDistribucionGastos().getFirstNode();
            while (current != null) {
                CategoriaGasto categoria = current.getCategoriaGasto();
                if (categoria != null) {
                    String nombreCategoria = formatearNombreCategoria(categoria);
                    distribucion.put(nombreCategoria, current.getMonto());
                }
                current = current.getNextNode();
            }
        }
        dto.setDistribucionGastos(distribucion);

        return dto;
    }

    /**
     * Formatea el nombre de una categoría de gasto para mostrarlo más legible
     */
    private static String formatearNombreCategoria(CategoriaGasto categoria) {
        if (categoria == null) {
            return "Desconocido";
        }

        switch (categoria) {
            case TRANSPORTE:
                return "Transporte";
            case COMIDA:
                return "Alimentación";
            case SALUD:
                return "Salud";
            case ENTRETENIMIENTO:
                return "Entretenimiento";
            case EDUCACION:
                return "Educación";
            case OTROS:
                return "Otros";
            default:
                return categoria.name();
        }
    }
}