package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccionPuntos;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransaccionRepository extends MongoRepository<Transaccion,String> {



    List<Transaccion> findTransaccionById(String idCliente);
}
