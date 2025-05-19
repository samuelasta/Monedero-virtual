package co.uniquindio.edu.mi_moneda;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.model.Beneficio;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Puntos;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoBeneficio;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.repository.PuntosRepository;
import co.uniquindio.edu.mi_moneda.services.implementation.PuntosServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PuntosServiceTest {

    @InjectMocks
    private PuntosServiceImpl puntosService;

    @Mock
    private PuntosRepository puntosRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private co.uniquindio.edu.mi_moneda.repository.TransaccionPuntosRepository transaccionPuntosRepository;

    private Cliente cliente;
    private Transaccion transaccion;
    private Beneficio beneficio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Configurar cliente de prueba
        cliente = new Cliente();
        cliente.setId(UUID.randomUUID().toString());
        cliente.setNombre("Usuario Prueba");
        cliente.setEmail("test@ejemplo.com");

        Puntos puntos = new Puntos();
        puntos.setId(UUID.randomUUID().toString());
        puntos.setCliente(cliente);
        puntos.setPuntosAcumulados(1000);
        puntos.setHistorialPuntos(new SimpleList<>());

        cliente.setPuntos(puntos);
        cliente.setRango("BRONCE");

        // Configurar transacción de prueba
        transaccion = new Transaccion();
        transaccion.setId(UUID.randomUUID().toString());
        transaccion.setTipo(TipoTransaccion.DEPOSITO);
        transaccion.setMonto(1000.0);
        transaccion.setFecha(LocalDateTime.now());
        transaccion.setActiva(true);

        // Configurar beneficio de prueba
        beneficio = new Beneficio();
        beneficio.setId(UUID.randomUUID().toString());
        beneficio.setNombre("Descuento en comisión");
        beneficio.setDescripcion("Descuento del 50% en comisiones");
        beneficio.setCostePuntos(500);
        beneficio.setTipo(TipoBeneficio.DESCUENTO_COMISION);
        beneficio.setValor(0.5); // 50% de descuento

        // Configurar comportamiento de los mocks
        when(puntosRepository.save(any(Puntos.class))).thenAnswer(i -> i.getArguments()[0]);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transaccionPuntosRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void calcularPuntosPorTransaccion_Deposito() {
        // Ejecutar
        double puntos = puntosService.calcularPuntosPorTransaccion(transaccion);

        // Verificar
        assertEquals(10, puntos); // 1% de 1000
    }

    @Test
    void calcularPuntosPorTransaccion_Retiro() {
        // Configurar
        transaccion.setTipo(TipoTransaccion.RETIRO);

        // Ejecutar
        double puntos = puntosService.calcularPuntosPorTransaccion(transaccion);

        // Verificar
        assertEquals(0, puntos); // Los retiros no generan puntos
    }

    @Test
    void acumularPuntosCliente() {
        // Ejecutar
        boolean resultado = puntosService.acumularPuntosCliente(cliente, 100, "Prueba de acumulación");

        // Verificar
        assertTrue(resultado);
        assertEquals(1100, cliente.getPuntos().getPuntosAcumulados());
    }

    @Test
    void canjearPuntos_SuficientesPuntos() {
        // Ejecutar
        boolean resultado = puntosService.canjearPuntos(cliente, beneficio);

        // Verificar
        assertTrue(resultado);
        assertEquals(500, cliente.getPuntos().getPuntosAcumulados()); // 1000 - 500
    }

    @Test
    void canjearPuntos_InsuficientesPuntos() {
        // Configurar
        cliente.getPuntos().setPuntosAcumulados(300); // Menos de los 500 requeridos

        // Ejecutar
        boolean resultado = puntosService.canjearPuntos(cliente, beneficio);

        // Verificar
        assertFalse(resultado);
        assertEquals(300, cliente.getPuntos().getPuntosAcumulados()); // No debería cambiar
    }

    @Test
    void actualizarRangoCliente() {
        // Caso 1: Bronce
        cliente.getPuntos().setPuntosAcumulados(500);
        String rango1 = puntosService.actualizarRangoCliente(cliente);
        assertEquals("BRONCE", rango1);

        // Caso 2: Plata
        cliente.getPuntos().setPuntosAcumulados(2000);
        String rango2 = puntosService.actualizarRangoCliente(cliente);
        assertEquals("PLATA", rango2);

        // Caso 3: Oro
        cliente.getPuntos().setPuntosAcumulados(10000);
        String rango3 = puntosService.actualizarRangoCliente(cliente);
        assertEquals("ORO", rango3);

        // Caso 4: Platino
        cliente.getPuntos().setPuntosAcumulados(20000);
        String rango4 = puntosService.actualizarRangoCliente(cliente);
        assertEquals("PLATINO", rango4);
    }
}