package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Notificacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificacionRepository extends MongoRepository<Notificacion,String> {
}
