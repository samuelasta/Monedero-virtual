package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.PatronGasto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PatronGastoRepository extends MongoRepository<PatronGasto,String> {
}
