package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonederoDTO {
    private String id;
    private String nombre;
    private TipoMonedero tipoMonedero;
    private double saldo;
    private String numeroCuenta;
    private String numeroCuentaFormateado;
    private LocalDateTime fechaCreacion;
    private boolean activo;

    /**
     * Crea un DTO a partir de la entidad Monedero
     */
    public static MonederoDTO fromEntity(Monedero monedero) {
        if (monedero == null) {
            return null;
        }

        MonederoDTO dto = new MonederoDTO();
        dto.setId(monedero.getId());
        dto.setNombre(monedero.getNombre());
        dto.setTipoMonedero(monedero.getTipoMonedero());
        dto.setSaldo(monedero.getSaldo());
        dto.setNumeroCuenta(monedero.getNumeroCuenta());
        dto.setNumeroCuentaFormateado(monedero.getNumeroCuentaFormateado());
        dto.setFechaCreacion(monedero.getFechaCreacion());
        dto.setActivo(monedero.isActivo());

        return dto;
    }
}