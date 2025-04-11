package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.TransaccionProgramada;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransaccionProgramadaRepository extends MongoRepository<TransaccionProgramada,String> {
}
