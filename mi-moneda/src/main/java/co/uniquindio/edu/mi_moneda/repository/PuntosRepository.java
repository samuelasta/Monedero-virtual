package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Puntos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuntosRepository extends MongoRepository<Puntos,String> {

    /**
     * Busca los puntos asociados a un cliente específico
     * @param cliente El cliente del cual se buscan los puntos
     * @return El objeto Puntos asociado al cliente
     */
    Puntos findByCliente(Cliente cliente);
}
