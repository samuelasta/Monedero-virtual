package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//esta interfaz es el acceso directo a la base de datos, trae ya estos metodos:
//save(T entity)
//
//findAll()
//
//findById(String id)
//
//deleteById(String id)
//
//existsById(String id
//puedo crear metodos personalizados
@Repository
public interface ClienteRepository extends MongoRepository<Cliente,String> {

    /**
     * Busca un cliente por su dirección de correo electrónico
     * @param email El correo electrónico del cliente
     * @return Un Optional que contiene el cliente si existe
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Verifica si existe un cliente con el correo electrónico proporcionado
     * @param email El correo electrónico a verificar
     * @return true si existe un cliente con ese correo, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca clientes por su rango
     * @param rango El rango de los clientes a buscar
     * @return Una lista de clientes que tienen el rango especificado
     */
    List<Cliente> findByRango(String rango);
}
