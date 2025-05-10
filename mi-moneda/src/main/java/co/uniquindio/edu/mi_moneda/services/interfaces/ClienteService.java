package co.uniquindio.edu.mi_moneda.services.interfaces;

import co.uniquindio.edu.mi_moneda.model.Cliente;

public interface ClienteService {

    /**
     * Registra un nuevo cliente en el sistema
     * @param id ID del cliente (puede ser proporcionado o generado)
     * @param nombre Nombre completo del cliente
     * @param email Correo electrónico del cliente
     * @param password Contraseña del cliente
     * @return Cliente registrado
     * @throws RuntimeException Si ya existe un cliente con el mismo email o ID
     */
    Cliente registrarCliente(String id, String nombre, String email, String password);

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

    Cliente buscarClientePorEmail(String email) throws Exception;
}