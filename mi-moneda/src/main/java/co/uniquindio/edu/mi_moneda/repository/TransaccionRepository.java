package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccionPuntos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends MongoRepository<Transaccion,String> {

    /**
     * Busca transacciones de puntos asociadas a una transacción financiera específica
     * @param transaccion La transacción financiera asociada
     * @return Lista de transacciones de puntos asociadas
     */
    List<TransaccionPuntos> findByTransaccionAsociada(Transaccion transaccion);

    /**
     * Busca transacciones de puntos de un tipo específico
     * @param tipo El tipo de transacción de puntos
     * @return Lista de transacciones de puntos del tipo especificado
     */
    List<TransaccionPuntos> findByTipo(TipoTransaccionPuntos tipo);

    /**
     * Busca transacciones de puntos realizadas en un rango de fechas
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de transacciones de puntos en el rango de fechas
     */
    List<TransaccionPuntos> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
