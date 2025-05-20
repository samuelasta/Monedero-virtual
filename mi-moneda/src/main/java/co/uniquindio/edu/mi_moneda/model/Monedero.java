package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(collection = "Monederos")
public class Monedero implements identificable {

    @Id
    private String id;

    private String nombre;
    @JsonBackReference
    private Cliente propietario;
    private TipoMonedero tipoMonedero;
    private double saldo;

    @Indexed(unique = true)
    private String numeroCuenta; // Unique account number for transfers

    private LocalDateTime fechaCreacion;
    private boolean activo;
    private DoubleList<Transaccion> historialTransacciones;

    @Override
    public String getId() {
        return id;
    }

    /**
     * Generate a formatted account number for display
     * Format: XXXX-XXXX-XXXX-1234
     * @return Formatted account number
     */
    public String getNumeroCuentaFormateado() {
        if (numeroCuenta == null || numeroCuenta.length() < 16) {
            return numeroCuenta;
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append("-");
            }
            formatted.append(numeroCuenta.charAt(i));
        }
        return formatted.toString();
    }
}