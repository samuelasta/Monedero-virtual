package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MonederoRepository extends MongoRepository<Monedero,String> {

    List<Monedero> findByPropietarioIdAndTipoMonedero(String clienteId, TipoMonedero tipo);
    /**
     * Find a wallet by its account number
     * @param numeroCuenta The account number to search for
     * @return Optional containing the wallet if found
     */
    Optional<Monedero> findByNumeroCuenta(String numeroCuenta);

    /**
     * Check if a wallet with the given account number exists
     * @param numeroCuenta The account number to check
     * @return true if a wallet with that account number exists
     */
    boolean existsByNumeroCuenta(String numeroCuenta);

    /**
     * Find all wallets owned by a specific client
     * @param propietario The client who owns the wallets
     * @return List of wallets owned by the client
     */
    List<Monedero> findByPropietario(Cliente propietario);
}