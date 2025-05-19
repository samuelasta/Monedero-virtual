package co.uniquindio.edu.mi_moneda.services.implementation;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Puntos;
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
        cliente.setId(id != null && !id.trim().isEmpty() ? id : UUID.randomUUID().toString());
        cliente.setNombre(nombre);
        cliente.setEmail(email);
        cliente.setPassword(passwordEncoder.encode(password));
        cliente.setRango("BRONCE");
        cliente.setMonederos(new SimpleList<>());
        cliente.setHistorialTransacciones(new DoubleList<>());
        cliente.setTransaccionesProgramadas(new QueueTransactionProgramed());
        cliente.setNotificaciones(new SimpleList<>());

        // Creamos y asociamos los puntos
        Puntos puntos = new Puntos();
        puntos.setId(UUID.randomUUID().toString());
        puntos.setCliente(cliente);
        puntos.setPuntosAcumulados(0);
        puntos.setHistorialPuntos(new SimpleList<>());
        cliente.setPuntos(puntos);

        // Guardamos el cliente y puntos en la base de datos
        puntosRepository.save(puntos);
        Cliente clienteGuardado = clienteRepository.save(cliente);

        // Creamos un monedero principal con número de cuenta único
        String numeroCuenta = generarNumeroCuentaUnico();
        Monedero monederoPrincipal = Monedero.builder()
                .id(numeroCuenta)
                .nombre("Monedero Principal")
                .propietario(clienteGuardado)
                .tipoMonedero(TipoMonedero.CORRIENTE)
                .saldo(0.0)
                .numeroCuenta(numeroCuenta)
                .fechaCreacion(LocalDateTime.now())
                .activo(true)
                .historialTransacciones(new DoubleList<>())
                .build();

        // Le creamos un monedero de ahorros
        String numeroAhorros = generarNumeroCuentaUnico();
        Monedero monederoAhorros = Monedero.builder()
                .id(numeroAhorros)
                .nombre("Monedero Ahorros")
                .propietario(clienteGuardado)
                .tipoMonedero(TipoMonedero.AHORRO)
                .saldo(0.0)
                .numeroCuenta(numeroCuenta)
                .fechaCreacion(LocalDateTime.now())
                .activo(true)
                .historialTransacciones(new DoubleList<>())
                .build();

        // Creamos un monedero principal con número de cuenta único
        String numeroNomina = generarNumeroCuentaUnico();
        Monedero monederoNomina = Monedero.builder()
                .id(numeroNomina)
                .nombre("Monedero Nomina")
                .propietario(clienteGuardado)
                .tipoMonedero(TipoMonedero.NOMINA)
                .saldo(0.0)
                .numeroCuenta(numeroCuenta)
                .fechaCreacion(LocalDateTime.now())
                .activo(true)
                .historialTransacciones(new DoubleList<>())
                .build();

        emailService.sendWelcomeEmail(email, nombre, monederoPrincipal.getNumeroCuentaFormateado(), monederoAhorros.getNumeroCuentaFormateado(), monederoNomina.getNumeroCuentaFormateado());
        monederoRepository.save(monederoPrincipal);
        monederoRepository.save(monederoAhorros);
        monederoRepository.save(monederoNomina);

        // Añadimos el monedero a la lista del cliente
        SimpleList<Monedero> monederos = cliente.getMonederos();
        monederos.add(monederoPrincipal);
        monederos.add(monederoAhorros);
        monederos.add(monederoNomina);
        cliente.setMonederos(monederos);
        clienteRepository.save(cliente);
        System.out.println("cliente guardado en la base de datos");


        return clienteGuardado;
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
     * Genera un número de cuenta único de 16 dígitos
     * @return Número de cuenta único
     */
    private String generarNumeroCuentaUnico() {
        Random random = new Random();
        String numeroCuenta;
        boolean esUnico = false;

        do {
            // Generar número de 16 dígitos
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