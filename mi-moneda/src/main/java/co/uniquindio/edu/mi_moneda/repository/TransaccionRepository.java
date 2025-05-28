package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccionPuntos;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends MongoRepository<Transaccion,String> {



    List<Transaccion> findTransaccionById(String id);

    // Buscar por monedero origen
    List<Transaccion> findByOrigenId(String monederoId);

    // Buscar por monedero destino
    List<Transaccion> findByDestinoId(String monederoId);

    // Buscar por monedero origen o destino
    @Query("{'$or': [{'origen.$id': ?0}, {'destino.$id': ?0}]}")
    List<Transaccion> findByOrigenIdOrDestinoId(String monederoId);

    // Buscar transacciones por múltiples monederos
    @Query("{'$or': [{'origen.$id': {'$in': ?0}}, {'destino.$id': {'$in': ?0}}]}")
    List<Transaccion> findByMonederosIds(List<String> monederosIds);

    // Buscar por fecha (las más recientes primero)
    List<Transaccion> findByOrigenIdOrDestinoIdOrderByFechaDesc(String origenId, String destinoId);

    // Buscar transacciones activas
    List<Transaccion> findByActivaTrueOrderByFechaDesc();
}
