package co.uniquindio.edu.mi_moneda.services.interfaces;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.model.Cliente;

public interface ClienteService {

    /**
     * Actualiza la información del perfil de un cliente
     * @param cliente Cliente con la información actualizada
     * @throws Exception Si ocurre un error durante la actualización
     */
    void actualizarPerfil(Cliente cliente) throws Exception;

    /**
     * Registra un nuevo cliente en el sistema
     * @param id ID del cliente (puede ser proporcionado o generado)
     * @param nombre Nombre completo del cliente
     * @param email Correo electrónico del cliente
     * @param password Contraseña del cliente
     * @throws RuntimeException Si ya existe un cliente con el mismo email o ID
     */
    void registrarCliente(String id, String nombre, String email, String password);

    /**
     * Autentica un cliente en el sistema
     * @param email Correo electrónico del cliente
     * @param password Contraseña del cliente
     * @return Cliente autenticado
     * @throws Exception Si las credenciales son inválidas
     */
    Cliente autenticarCliente(String email, String password) throws Exception;

    /**
     * Busca un cliente por su ID
     * @param id ID del cliente
     * @return Cliente encontrado
     * @throws Exception Si no se encuentra el cliente
     */
    Cliente buscarClientePorId(String id) throws Exception;

    /**
     * Busca un cliente por su ID y lo convierte a DTO
     * @param id ID del cliente
     * @return ClienteDTO con la información básica del cliente
     * @throws Exception Si no se encuentra el cliente
     */
    ClienteDTO buscarClienteDTOPorId(String id) throws Exception;

    /**
     * Busca un cliente por su email
     * @param email Email del cliente
     * @return Cliente encontrado
     * @throws Exception Si no se encuentra el cliente
     */
    Cliente buscarClientePorEmail(String email) throws Exception;

    /**
     * Busca un cliente por el número de cuenta de uno de sus monederos
     * @param numeroCuenta Número de cuenta a buscar
     * @return Cliente propietario del monedero con ese número de cuenta
     * @throws Exception Si no se encuentra el monedero o el cliente
     */
    Cliente buscarClientePorNumeroCuenta(String numeroCuenta) throws Exception;
}