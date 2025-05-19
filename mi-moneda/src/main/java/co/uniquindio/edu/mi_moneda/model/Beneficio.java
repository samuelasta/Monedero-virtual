package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.model.enums.TipoBeneficio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Beneficios")
public class Beneficio {

    @Id
    private String id;

    private String nombre;
    private String descripcion;
    private double costePuntos;
    private TipoBeneficio tipo;
    private double valor;
}
