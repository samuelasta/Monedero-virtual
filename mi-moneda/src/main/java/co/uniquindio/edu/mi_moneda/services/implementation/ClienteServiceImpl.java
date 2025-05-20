package co.uniquindio.edu.mi_moneda.services.implementation;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.dto.MonederoDTO;
import co.uniquindio.edu.mi_moneda.dto.PuntosDTO;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Puntos;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.repository.MonederoRepository;
import co.uniquindio.edu.mi_moneda.repository.PuntosRepository;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import co.uniquindio.edu.mi_moneda.services.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PuntosRepository puntosRepository;

    @Autowired
    private MonederoRepository monederoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public void registrarCliente(String id, String nombre, String email, String password) {
        try {
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
            cliente.setId(id != null && !id.trim().isEmpty() ? id : UUID.randomUUID().toString());
            cliente.setNombre(nombre);
            cliente.setEmail(email);
            cliente.setPassword(passwordEncoder.encode(password));
            cliente.setRango("BRONCE");
            cliente.setMonederos(new SimpleList<>());
            cliente.setHistorialTransacciones(new DoubleList<>());
            cliente.setTransaccionesProgramadas(new QueueTransactionProgramed());
            cliente.setNotificaciones(new SimpleList<>());

            // Guardamos primero el cliente
            Cliente clienteGuardado = clienteRepository.save(cliente);

            // Creamos y asociamos los puntos
            Puntos puntos = new Puntos();
            puntos.setId(UUID.randomUUID().toString());
            puntos.setCliente(clienteGuardado);
            puntos.setPuntosAcumulados(0);
            puntos.setHistorialPuntos(new SimpleList<>());

            // Guardamos los puntos
            puntosRepository.save(puntos);

            // Actualizamos el cliente con la referencia a puntos
            clienteGuardado.setPuntos(puntos);
            clienteGuardado = clienteRepository.save(clienteGuardado);

            // Creamos los monederos con referencias al cliente guardado
            String numeroCuenta = generarNumeroCuentaUnico();
            Monedero monederoPrincipal = Monedero.builder()
                    .id(UUID.randomUUID().toString())
                    .nombre("Monedero Principal")
                    .propietario(clienteGuardado)
                    .tipoMonedero(TipoMonedero.CORRIENTE)
                    .saldo(0.0)
                    .numeroCuenta(numeroCuenta)
                    .fechaCreacion(LocalDateTime.now())
                    .activo(true)
                    .historialTransacciones(new DoubleList<>())
                    .build();

            String numeroAhorros = generarNumeroCuentaUnico();
            Monedero monederoAhorros = Monedero.builder()
                    .id(UUID.randomUUID().toString())
                    .nombre("Monedero Ahorros")
                    .propietario(clienteGuardado)
                    .tipoMonedero(TipoMonedero.AHORRO)
                    .saldo(0.0)
                    .numeroCuenta(numeroAhorros)
                    .fechaCreacion(LocalDateTime.now())
                    .activo(true)
                    .historialTransacciones(new DoubleList<>())
                    .build();

            String numeroNomina = generarNumeroCuentaUnico();
            Monedero monederoNomina = Monedero.builder()
                    .id(UUID.randomUUID().toString())
                    .nombre("Monedero Nomina")
                    .propietario(clienteGuardado)
                    .tipoMonedero(TipoMonedero.NOMINA)
                    .saldo(0.0)
                    .numeroCuenta(numeroNomina)
                    .fechaCreacion(LocalDateTime.now())
                    .activo(true)
                    .historialTransacciones(new DoubleList<>())
                    .build();

            // Guardamos los monederos
            monederoPrincipal = monederoRepository.save(monederoPrincipal);
            monederoAhorros = monederoRepository.save(monederoAhorros);
            monederoNomina = monederoRepository.save(monederoNomina);

            // Añadimos los monederos a la lista del cliente
            SimpleList<Monedero> monederos = new SimpleList<>();
            monederos.add(monederoPrincipal);
            monederos.add(monederoAhorros);
            monederos.add(monederoNomina);
            clienteGuardado.setMonederos(monederos);

            // Guardamos el cliente actualizado
            clienteRepository.save(clienteGuardado);

            // Enviamos el email con manejo de excepciones
            try {
                String formattedPrincipal = monederoPrincipal.getNumeroCuentaFormateado() != null ?
                        monederoPrincipal.getNumeroCuentaFormateado() : numeroCuenta;
                String formattedAhorros = monederoAhorros.getNumeroCuentaFormateado() != null ?
                        monederoAhorros.getNumeroCuentaFormateado() : numeroAhorros;
                String formattedNomina = monederoNomina.getNumeroCuentaFormateado() != null ?
                        monederoNomina.getNumeroCuentaFormateado() : numeroNomina;

                emailService.sendWelcomeEmail(email, nombre, formattedPrincipal,
                        formattedAhorros, formattedNomina);
            } catch (Exception e) {
                // Log el error pero permitir que el registro continúe
                System.err.println("Error al enviar email de bienvenida: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Error durante el registro del cliente: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-lanzar la excepción para que sea manejada por el controlador
        }
    }

    @Override
    public Cliente autenticarCliente(String email, String password) throws Exception {
        System.out.println("Buscando cliente con email: " + email);
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(email);
        System.out.println("¿Cliente encontrado? " + clienteOpt.isPresent());

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

    @Override
    public ClienteDTO buscarClienteDTOPorId(String id) throws Exception {
        Cliente cliente = buscarClientePorId(id);

        // Convertir a DTO con información básica
        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        dto.setRango(cliente.getRango());

        // Convertir Puntos a DTO
        if (cliente.getPuntos() != null) {
            dto.setPuntos(PuntosDTO.fromEntity(cliente.getPuntos()));
        }

        // Convertir Monederos a lista de DTOs
        List<MonederoDTO> monederosDTO = new ArrayList<>();
        SimpleList<Monedero> monederosList = cliente.getMonederos();
        if (monederosList != null && !monederosList.isEmpty()) {
            co.uniquindio.edu.mi_moneda.listasPropias.Node<Monedero> current = monederosList.getFirstNode();
            while (current != null) {
                monederosDTO.add(MonederoDTO.fromEntity(current.getValue()));
                current = current.getNextNodo();
            }
        }
        dto.setMonederos(monederosDTO);

        return dto;
    }

    @Override
    public Cliente buscarClientePorEmail(String email) throws Exception {
        return clienteRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Cliente no encontrado con email: " + email));
    }

    @Override
    public Cliente buscarClientePorNumeroCuenta(String numeroCuenta) throws Exception {
        // Buscar el monedero con ese número de cuenta
        Monedero monedero = monederoRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new Exception("No se encontró ningún monedero con el número de cuenta: " + numeroCuenta));

        // Devolver el propietario del monedero
        return monedero.getPropietario();
    }

    /**
     * Genera un número de cuenta único de 10 dígitos
     * @return Número de cuenta único
     */
    private String generarNumeroCuentaUnico() {
        Random random = new Random();
        String numeroCuenta;
        boolean esUnico = false;

        do {
            // Generar número de 10 dígitos
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            numeroCuenta = sb.toString();

            // Verificar si ya existe un monedero con ese número
            esUnico = !monederoRepository.existsByNumeroCuenta(numeroCuenta);

        } while (!esUnico);

        return numeroCuenta;
    }
}