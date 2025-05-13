package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleNode;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.NodeQueue;
import co.uniquindio.edu.mi_moneda.listasPropias.OwnMap;
import co.uniquindio.edu.mi_moneda.model.*;
import co.uniquindio.edu.mi_moneda.model.enums.CategoriaGasto;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.repository.*;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import co.uniquindio.edu.mi_moneda.services.interfaces.PuntosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class DashboardController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private MonederoRepository monederoRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private TransaccionProgramadaRepository transaccionProgramadaRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private PuntosRepository puntosRepository;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Autowired
    private PatronGastoRepository patronGastoRepository;

    @Autowired
    private PuntosService puntosService;

    /**
     * Muestra el dashboard del cliente autenticado con toda su información
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            // Obtener información del cliente
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            // Calcular el balance total sumando saldos de todos los monederos
            double balanceTotal = calcularBalanceTotal(cliente);
            model.addAttribute("balanceTotal", balanceTotal);

            // Número de monederos activos
            model.addAttribute("cantidadMonederos", obtenerCantidadMonederos(cliente));

            // Buscar las transacciones más recientes del cliente
            List<Transaccion> transaccionesRecientes = obtenerTransaccionesRecientes(cliente);
            model.addAttribute("transaccionesRecientes", transaccionesRecientes);

            // Obtener transacciones programadas próximas
            List<TransaccionProgramada> transaccionesProgramadas = obtenerTransaccionesProgramadas(cliente);
            model.addAttribute("transaccionesProgramadas", transaccionesProgramadas);

            // Obtener las notificaciones no leídas
            List<Notificacion> notificacionesNoLeidas = obtenerNotificacionesNoLeidas(cliente);
            model.addAttribute("notificacionesNoLeidas", notificacionesNoLeidas);
            model.addAttribute("cantidadNotificaciones", notificacionesNoLeidas.size());

            // Obtener información de puntos del cliente
            Puntos puntos = cliente.getPuntos();
            if (puntos == null) {
                puntos = new Puntos();
                puntos.setPuntosAcumulados(0);
                puntos.setHistorialPuntos(new SimpleList<>());
            }
            model.addAttribute("puntos", puntos);

            // Calcular puntos necesarios para el siguiente rango
            Map<String, Object> rangosInfo = calcularInfoRangos(cliente, puntos);
            model.addAttribute("rangosInfo", rangosInfo);

            // Obtener beneficios disponibles
            List<Beneficio> beneficiosDisponibles = beneficioRepository.findByCostePuntosLessThanEqual(
                    puntos != null ? puntos.getPuntosAcumulados() : 0);
            model.addAttribute("beneficiosDisponibles", beneficiosDisponibles);

            // Obtener distribución de gastos para el gráfico
            Map<String, Double> distribucionGastos = obtenerDistribucionGastos(cliente);
            model.addAttribute("distribucionGastos", distribucionGastos);

            // Formateador para fechas
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
            model.addAttribute("formatter", formatter);

            return "dashboard";
        } catch (Exception e) {
            session.setAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "redirect:/login-page";
        }
    }

    /**
     * LLeva hacia la ventana de wallets
     */
    @GetMapping("/wallets")
    public String showWallets(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        String clienteId = (String) session.getAttribute("clienteId");

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            return "wallets";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login-page";
        }
    }

    /**
     * LLeva hacia la ventana de transacciones
     */
    @GetMapping("/transactions")
    public String showTransactions(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        String clienteId = (String) session.getAttribute("clienteId");

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            return "transactions";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login-page";
        }
    }

    /**
     * LLeva hacia la ventana de programadas
     */
    @GetMapping("/scheduled")
    public String showProgramadas(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        String clienteId = (String) session.getAttribute("clienteId");

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            return "programadas";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login-page";
        }
    }

    /**
     * LLeva hacia la ventana de puntos
     */
    @GetMapping("/points")
    public String showPuntos(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        String clienteId = (String) session.getAttribute("clienteId");

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            return "puntos";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login-page";
        }
    }

    /**
     * LLeva hacia la ventana de analisis
     */
    @GetMapping("/analytics")
    public String showAnalisis(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        String clienteId = (String) session.getAttribute("clienteId");

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            return "analisis";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login-page";
        }
    }

    /**
     * LLeva hacia la ventana de mi perfil
     */
    @GetMapping("/profile")
    public String showPerfil(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        String clienteId = (String) session.getAttribute("clienteId");

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            return "perfil";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login-page";
        }
    }

    /**
     * Calcula el balance total de todos los monederos
     */
    private double calcularBalanceTotal(Cliente cliente) {
        double total = 0.0;
        SimpleList<Monedero> monederos = obtenerMonederos(cliente);

        // Si no hay monederos, devuelve 0
        if (monederos == null || monederos.isEmpty()) {
            return 0.0;
        }

        // Recorrer la lista de monederos usando nuestras estructuras personalizadas
        Node<Monedero> nodoActual = monederos.getFirstNode();
        while (nodoActual != null) {
            total += nodoActual.getValue().getSaldo();
            nodoActual = nodoActual.getNextNodo();
        }

        return total;
    }

    /**
     * Obtiene la lista de monederos del cliente
     */
    private SimpleList<Monedero> obtenerMonederos(Cliente cliente) {
        // Si el cliente ya tiene una lista de monederos, la usamos
        if (cliente.getMonederos() != null) {
            return cliente.getMonederos();
        }

        // En caso contrario, buscamos los monederos en la base de datos
        SimpleList<Monedero> monederos = new SimpleList<>();
        List<Monedero> monederosEncontrados = monederoRepository.findAll().stream()
                .filter(m -> m.getPropietario() != null &&
                        m.getPropietario().getId().equals(cliente.getId()))
                .toList();

        // Añadimos a nuestra estructura personalizada SimpleList
        for (Monedero m : monederosEncontrados) {
            monederos.add(m);
        }

        return monederos;
    }

    /**
     * Obtiene la cantidad de monederos del cliente
     */
    private int obtenerCantidadMonederos(Cliente cliente) {
        SimpleList<Monedero> monederos = obtenerMonederos(cliente);
        return monederos != null ? monederos.getSize() : 0;
    }

    /**
     * Obtiene las transacciones más recientes del cliente usando DoubleList
     */
    private List<Transaccion> obtenerTransaccionesRecientes(Cliente cliente) {
        List<Transaccion> resultado = new ArrayList<>();

        // Obtener el historial de transacciones del cliente
        DoubleList<Transaccion> historial = cliente.getHistorialTransacciones();

        // Si no hay historial, devolver lista vacía
        if (historial == null || historial.isEmpty()) {
            return resultado;
        }

        // Recorrer la lista doblemente enlazada desde el final (más recientes primero)
        DoubleNode<Transaccion> nodoActual = historial.getLastNode();
        int count = 0;

        while (nodoActual != null && count < 5) {
            resultado.add(nodoActual.getValue());
            nodoActual = nodoActual.getPreviousNodo();
            count++;
        }

        return resultado;
    }

    /**
     * Obtiene las transacciones programadas próximas usando QueueTransactionProgramed
     */
    private List<TransaccionProgramada> obtenerTransaccionesProgramadas(Cliente cliente) {
        List<TransaccionProgramada> resultado = new ArrayList<>();

        // Obtener cola de transacciones programadas
        QueueTransactionProgramed cola = cliente.getTransaccionesProgramadas();

        // Si no hay cola, devolver lista vacía
        if (cola == null || cola.isEmpty()) {
            return resultado;
        }

        // La cola ya debe estar ordenada por prioridad, así que tomamos las primeras 3
        NodeQueue nodoActual = cola.getFirstNode();
        int count = 0;

        while (nodoActual != null && count < 3) {
            resultado.add(nodoActual.getTransaccion());
            nodoActual = nodoActual.getNextNode();
            count++;
        }

        return resultado;
    }

    /**
     * Obtiene las notificaciones no leídas del cliente usando SimpleList
     */
    private List<Notificacion> obtenerNotificacionesNoLeidas(Cliente cliente) {
        List<Notificacion> noLeidas = new ArrayList<>();

        // Obtener lista de notificaciones
        SimpleList<Notificacion> notificaciones = cliente.getNotificaciones();

        // Si no hay notificaciones, devolver lista vacía
        if (notificaciones == null || notificaciones.isEmpty()) {
            return noLeidas;
        }

        // Recorrer la lista y filtrar las no leídas
        Node<Notificacion> nodoActual = notificaciones.getFirstNode();

        while (nodoActual != null) {
            if (!nodoActual.getValue().isLeida()) {
                noLeidas.add(nodoActual.getValue());
            }
            nodoActual = nodoActual.getNextNodo();
        }

        return noLeidas;
    }

    /**
     * Calcula información sobre los rangos para el cliente
     */
    private Map<String, Object> calcularInfoRangos(Cliente cliente, Puntos puntos) {
        Map<String, Object> info = new HashMap<>();

        int puntosActuales = puntos != null ? puntos.getPuntosAcumulados() : 0;
        String rangoActual = cliente.getRango();

        // Determinar el siguiente rango y puntos necesarios
        String siguienteRango = "";
        int puntosNecesarios = 0;
        double porcentaje = 0.0;

        if ("BRONCE".equals(rangoActual)) {
            siguienteRango = "PLATA";
            puntosNecesarios = 1000 - puntosActuales;
            porcentaje = (puntosActuales / 1000.0) * 100;
        } else if ("PLATA".equals(rangoActual)) {
            siguienteRango = "ORO";
            puntosNecesarios = 5000 - puntosActuales;
            porcentaje = ((puntosActuales - 1000) / 4000.0) * 100;
        } else if ("ORO".equals(rangoActual)) {
            siguienteRango = "PLATINO";
            puntosNecesarios = 15000 - puntosActuales;
            porcentaje = ((puntosActuales - 5000) / 10000.0) * 100;
        } else {
            siguienteRango = "MÁXIMO NIVEL";
            puntosNecesarios = 0;
            porcentaje = 100;
        }

        info.put("rangoActual", rangoActual);
        info.put("siguienteRango", siguienteRango);
        info.put("puntosNecesarios", puntosNecesarios);
        info.put("porcentajeAvance", porcentaje);

        return info;
    }

    /**
     * Obtiene la distribución de gastos para el gráfico usando OwnMap
     */
    private Map<String, Double> obtenerDistribucionGastos(Cliente cliente) {
        Map<String, Double> resultado = new HashMap<>();

        // Intentamos obtener el patrón de gasto del cliente
        PatronGasto patronGasto = null;
        List<PatronGasto> patrones = patronGastoRepository.findAll();

        for (PatronGasto p : patrones) {
            if (p.getCliente() != null && p.getCliente().getId().equals(cliente.getId())) {
                patronGasto = p;
                break;
            }
        }

        // Si no hay patrón de gasto registrado, creamos datos de ejemplo
        if (patronGasto == null || patronGasto.getDistribucionGastos() == null) {
            resultado.put("Alimentación", 30.0);
            resultado.put("Transporte", 20.0);
            resultado.put("Entretenimiento", 15.0);
            resultado.put("Servicios", 25.0);
            resultado.put("Otros", 10.0);
            return resultado;
        }

        // Si hay patrón de gasto, lo procesamos usando OwnMap
        OwnMap distribucion = patronGasto.getDistribucionGastos();

        // Recorrer las categorías de gasto
        for (CategoriaGasto categoria : CategoriaGasto.values()) {
            double monto = distribucion.getAmountCategory(categoria);
            if (monto > 0) {
                resultado.put(categoria.name(), monto);
            }
        }

        return resultado;
    }
}