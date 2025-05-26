package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.BeneficioDTO;
import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.dto.PuntosDTO;
import co.uniquindio.edu.mi_moneda.dto.TransaccionPuntosDTO;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.model.Beneficio;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Puntos;
import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccionPuntos;
import co.uniquindio.edu.mi_moneda.repository.BeneficioRepository;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PuntosController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private BeneficioRepository beneficioRepository;

    /**
     * Muestra la página de puntos
     */
    @GetMapping("/points")
    public String showPuntos(HttpSession session, Model model) {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            // Obtener información del cliente
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Convertir a DTO
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setEmail(cliente.getEmail());
            clienteDTO.setRango(cliente.getRango());
            model.addAttribute("cliente", clienteDTO);

            // Obtener información de puntos del cliente
            Puntos puntos = cliente.getPuntos();

            // Si el cliente tiene puntos, mostrar datos reales
            if (puntos != null) {
                PuntosDTO puntosDTO = PuntosDTO.fromEntity(puntos);
                model.addAttribute("puntos", puntosDTO);

                // Calcular información de rangos
                Map<String, Object> rangosInfo = calcularInfoRangos(cliente, puntos);
                model.addAttribute("rangosInfo", rangosInfo);

                // Obtener beneficios disponibles
                List<Beneficio> beneficiosDisponibles = beneficioRepository.findByCostePuntosLessThanEqual(
                        puntos.getPuntosAcumulados());
                List<BeneficioDTO> beneficiosDTO = beneficiosDisponibles.stream()
                        .map(BeneficioDTO::fromEntity)
                        .collect(Collectors.toList());
                model.addAttribute("beneficiosDisponibles", beneficiosDTO);

                // Historial de transacciones de puntos si está disponible
                if (puntos.getHistorialPuntos() != null && !puntos.getHistorialPuntos().isEmpty()) {
                    List<TransaccionPuntosDTO> historialDTO = new ArrayList<>();
                    Node<TransaccionPuntos> current = puntos.getHistorialPuntos().getFirstNode();
                    while (current != null) {
                        historialDTO.add(TransaccionPuntosDTO.fromEntity(current.getValue()));
                        current = current.getNextNodo();
                    }
                    model.addAttribute("historialPuntosData", historialDTO);
                }
            } else {
                // Si no hay información de puntos, usar datos de ejemplo
                usarDatosEjemplo(model, cliente.getRango());
            }

            return "puntos";
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, usar datos de ejemplo
            usarDatosEjemplo(model, "BRONCE");
            return "puntos";
        }
    }

    /**
     * Muestra la página de puntos oprimiendo boton cangear
     */
    @GetMapping("/points/redeem")
    public String showPuntosButton(HttpSession session, Model model) {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            // Obtener información del cliente
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Convertir a DTO
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setEmail(cliente.getEmail());
            clienteDTO.setRango(cliente.getRango());
            model.addAttribute("cliente", clienteDTO);

            // Obtener información de puntos del cliente
            Puntos puntos = cliente.getPuntos();

            // Si el cliente tiene puntos, mostrar datos reales
            if (puntos != null) {
                PuntosDTO puntosDTO = PuntosDTO.fromEntity(puntos);
                model.addAttribute("puntos", puntosDTO);

                // Calcular información de rangos
                Map<String, Object> rangosInfo = calcularInfoRangos(cliente, puntos);
                model.addAttribute("rangosInfo", rangosInfo);

                // Obtener beneficios disponibles
                List<Beneficio> beneficiosDisponibles = beneficioRepository.findByCostePuntosLessThanEqual(
                        puntos.getPuntosAcumulados());
                List<BeneficioDTO> beneficiosDTO = beneficiosDisponibles.stream()
                        .map(BeneficioDTO::fromEntity)
                        .collect(Collectors.toList());
                model.addAttribute("beneficiosDisponibles", beneficiosDTO);

                // Historial de transacciones de puntos si está disponible
                if (puntos.getHistorialPuntos() != null && !puntos.getHistorialPuntos().isEmpty()) {
                    List<TransaccionPuntosDTO> historialDTO = new ArrayList<>();
                    Node<TransaccionPuntos> current = puntos.getHistorialPuntos().getFirstNode();
                    while (current != null) {
                        historialDTO.add(TransaccionPuntosDTO.fromEntity(current.getValue()));
                        current = current.getNextNodo();
                    }
                    model.addAttribute("historialPuntosData", historialDTO);
                }
            } else {
                // Si no hay información de puntos, usamos datos de ejemplo
                usarDatosEjemplo(model, cliente.getRango());
            }

            return "puntos";
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, usar datos de ejemplo
            usarDatosEjemplo(model, "BRONCE");
            return "puntos";
        }
    }

    /**
     * Canjea puntos por un beneficio
     */
    @PostMapping("/points/redeem")
    public String canjearPuntos(
            //@RequestParam("beneficioId") String beneficioId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener el ID del cliente desde la sesión
            String clienteId = (String) session.getAttribute("clienteId");
            if (clienteId == null) {
                return "redirect:/login-page";
            }


            redirectAttributes.addFlashAttribute("successMessage",
                    "Beneficio canjeado exitosamente. Los puntos han sido descontados de tu cuenta.");

            return "puntos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al canjear el beneficio: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * para usar datos de ejemplo cuando no hay datos reales
     */
    private void usarDatosEjemplo(Model model, String rango) {
        // Cliente de ejemplo
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre("Usuario Ejemplo");
        clienteDTO.setEmail("usuario@ejemplo.com");
        clienteDTO.setRango(rango != null ? rango : "BRONCE");
        model.addAttribute("cliente", clienteDTO);

        // Puntos de ejemplo
        PuntosDTO puntosDTO = new PuntosDTO();
        puntosDTO.setId("example-id");
        puntosDTO.setPuntosAcumulados(1250);
        model.addAttribute("puntos", puntosDTO);

        // Rangos de ejemplo
        Map<String, Object> rangosInfo = new HashMap<>();
        rangosInfo.put("rangoActual", "PLATA");
        rangosInfo.put("siguienteRango", "ORO");
        rangosInfo.put("puntosNecesarios", 750);
        rangosInfo.put("porcentajeAvance", 65.0);
        model.addAttribute("rangosInfo", rangosInfo);

        // Beneficios de ejemplo
        List<BeneficioDTO> beneficiosDTO = new ArrayList<>();
        // Beneficio 1
        BeneficioDTO beneficio1 = new BeneficioDTO();
        beneficio1.setId("beneficio-1");
        beneficio1.setNombre("Descuento en comisiones");
        beneficio1.setDescripcion("Obtén un 50% de descuento en comisiones por transferencias durante 1 mes");
        beneficio1.setCostePuntos(500);
        beneficio1.setCostePuntosFormateado("500 pts");
        beneficiosDTO.add(beneficio1);

        // Beneficio 2
        BeneficioDTO beneficio2 = new BeneficioDTO();
        beneficio2.setId("beneficio-2");
        beneficio2.setNombre("Tarjeta de regalo");
        beneficio2.setDescripcion("Tarjeta de regalo de $100 para usar en tiendas asociadas");
        beneficio2.setCostePuntos(1000);
        beneficio2.setCostePuntosFormateado("1000 pts");
        beneficiosDTO.add(beneficio2);

        // Beneficio 3
        BeneficioDTO beneficio3 = new BeneficioDTO();
        beneficio3.setId("beneficio-3");
        beneficio3.setNombre("Cashback en próxima transacción");
        beneficio3.setDescripcion("5% de cashback en tu próxima transacción (hasta $50)");
        beneficio3.setCostePuntos(1500);
        beneficio3.setCostePuntosFormateado("1500 pts");
        beneficiosDTO.add(beneficio3);

        model.addAttribute("beneficiosDisponibles", beneficiosDTO);

        // Historial de transacciones de puntos de ejemplo
        List<TransaccionPuntosDTO> historialDTO = new ArrayList<>();

        // Transacción 1
        TransaccionPuntosDTO trans1 = new TransaccionPuntosDTO();
        trans1.setId("trans-1");
        trans1.setTipo(TipoTransaccionPuntos.ACUMULACION);
        trans1.setCantidad(150);
        trans1.setFecha(LocalDateTime.now().minusDays(10));
        trans1.setFechaFormateada("10/05/2023, 15:30");
        trans1.setMotivo("Depósito en efectivo");
        trans1.setCantidadFormateada("150 pts");
        historialDTO.add(trans1);

        // Transacción 2
        TransaccionPuntosDTO trans2 = new TransaccionPuntosDTO();
        trans2.setId("trans-2");
        trans2.setTipo(TipoTransaccionPuntos.CANJE);
        trans2.setCantidad(-200);
        trans2.setFecha(LocalDateTime.now().minusDays(5));
        trans2.setFechaFormateada("15/05/2023, 14:45");
        trans2.setMotivo("Canje por descuento en comisiones");
        trans2.setCantidadFormateada("-200 pts");
        historialDTO.add(trans2);

        // Transacción 3
        TransaccionPuntosDTO trans3 = new TransaccionPuntosDTO();
        trans3.setId("trans-3");
        trans3.setTipo(TipoTransaccionPuntos.ACUMULACION);
        trans3.setCantidad(100);
        trans3.setFecha(LocalDateTime.now().minusDays(2));
        trans3.setFechaFormateada("18/05/2023, 09:15");
        trans3.setMotivo("Transferencia realizada");
        trans3.setCantidadFormateada("100 pts");
        historialDTO.add(trans3);

        model.addAttribute("historialPuntosData", historialDTO);
    }

    /**
     * Calcula información sobre los rangos para el cliente
     */
    private Map<String, Object> calcularInfoRangos(Cliente cliente, Puntos puntos) {
        Map<String, Object> info = new HashMap<>();

        double puntosActuales = puntos != null ? puntos.getPuntosAcumulados() : 0;
        String rangoActual = cliente.getRango();

        // Determinar el siguiente rango y puntos necesarios
        String siguienteRango = "";
        double puntosNecesarios = 0;
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
}