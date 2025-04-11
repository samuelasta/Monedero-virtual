package co.uniquindio.edu.mi_moneda.model;

import co.uniquindio.edu.mi_moneda.model.enums.Periodo;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "Transacciones programadas")
public class TransaccionProgramada implements Comparable<TransaccionProgramada>{

    @Id
    private String id;

    private TipoTransaccion tipo;
    private double monto;
    private LocalDateTime fechaProgramada;
    private String descripcion;
    private Monedero origen;
    private Monedero destino;
    private boolean recurrente;
    private Periodo periodoRecurrencia;
    private int prioridad;

    @Override
    public int compareTo(@NotNull TransaccionProgramada o) {
        // Compara por fecha primero
        int comparacionFecha = this.fechaProgramada.compareTo(o.fechaProgramada);//(negativo si o es mayor o 1 si o es menor)

        // Si las fechas son iguales, compara por prioridad
        if (comparacionFecha != 0) {
            return comparacionFecha; // Fecha más cercana primero
        }

        // Si fechas iguales, prioridad más alta primero (mayor prioridad = menor número)
        return Integer.compare(this.prioridad, o.prioridad);
    }
}
