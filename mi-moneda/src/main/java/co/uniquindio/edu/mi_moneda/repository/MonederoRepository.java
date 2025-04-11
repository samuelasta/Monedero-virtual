package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Monedero;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MonederoRepository extends MongoRepository<Monedero,String> {
}
