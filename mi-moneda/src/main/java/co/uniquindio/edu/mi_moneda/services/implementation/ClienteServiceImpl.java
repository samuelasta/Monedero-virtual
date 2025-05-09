package co.uniquindio.edu.mi_moneda.services.implementation;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Puntos;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.repository.PuntosRepository;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PuntosRepository puntosRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Cliente registrarCliente(String id, String nombre, String email, String password) {
        // Verificar si ya existe un cliente con ese email
        if (clienteRepository.existsByEmail(email)) {
            throw new RuntimeException("Ya existe un usuario con ese correo electrónico");
        }

        // Verificar si ya existe un cliente con ese ID
        if (id != null && !id.trim().isEmpty() && clienteRepository.existsById(id)) {
            throw new RuntimeException("Ya existe un usuario con ese ID");
        }

        // Crear nuevo cliente
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombre(nombre);
        cliente.setEmail(email);
        cliente.setPassword(passwordEncoder.encode(password));
        cliente.setRango("BRONCE");

        // Inicializar colecciones
        cliente.setMonederos(new SimpleList<>());
        cliente.setHistorialTransacciones(new DoubleList<>());
        cliente.setNotificaciones(new SimpleList<>());
        cliente.setTransaccionesProgramadas(new QueueTransactionProgramed());

        // Crear y asociar puntos
        Puntos puntos = new Puntos();
        puntos.setId(UUID.randomUUID().toString());
        puntos.setCliente(cliente);
        puntos.setPuntosAcumulados(0);
        puntos.setHistorialPuntos(new SimpleList<>());

        cliente.setPuntos(puntos);

        // Guardar cliente y puntos
        puntosRepository.save(puntos);
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente autenticarCliente(String email, String password) throws Exception {
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);

        if (clienteOpt.isEmpty()) {
            throw new Exception("Correo electrónico o contraseña incorrectos");
        }

        Cliente cliente = clienteOpt.get();

        if (!passwordEncoder.matches(password, cliente.getPassword())) {
            throw new Exception("Correo electrónico o contraseña incorrectos");
        }

        return cliente;
    }

    @Override
    public Cliente buscarClientePorId(String id) throws Exception {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);

        if (clienteOpt.isEmpty()) {
            throw new Exception("Cliente no encontrado");
        }

        return clienteOpt.get();
    }
}