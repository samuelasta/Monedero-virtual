package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransaccionPuntosRepository extends MongoRepository<TransaccionPuntos,String> {
}
