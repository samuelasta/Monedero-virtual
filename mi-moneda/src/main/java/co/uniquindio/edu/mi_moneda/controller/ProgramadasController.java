package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.dto.MonederoDTO;
import co.uniquindio.edu.mi_moneda.dto.TransaccionDTO;
import co.uniquindio.edu.mi_moneda.dto.TransaccionProgramadaDTO;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.listasPropias.NodeQueue;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.TransaccionProgramada;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ProgramadasController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra la página de transacciones programadas
     */
    @GetMapping("/scheduled")
    public String showScheduled(HttpSession session, Model model) {
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

            // Obtener los monederos del cliente para el selector de formulario
            List<MonederoDTO> monederosDTO = new ArrayList<>();
            if (cliente.getMonederos() != null && !cliente.getMonederos().isEmpty()) {
                Node<Monedero> current = cliente.getMonederos().getFirstNode();
                while (current != null) {
                    monederosDTO.add(MonederoDTO.fromEntity(current.getValue()));
                    current = current.getNextNodo();
                }
            }
            model.addAttribute("clienteMonederos", monederosDTO);

            // Obtener las transacciones programadas pendientes
            List<TransaccionProgramada> transaccionesProgramadas = new ArrayList<>();
            if (cliente.getTransaccionesProgramadas() != null && !cliente.getTransaccionesProgramadas().isEmpty()) {
                NodeQueue current = cliente.getTransaccionesProgramadas().getFirstNode();
                while (current != null) {
                    transaccionesProgramadas.add(current.getTransaccion());
                    current = current.getNextNode();
                }
            }

            // Convertir a DTOs
            List<TransaccionProgramadaDTO> transaccionesProgramadasDTO = transaccionesProgramadas.stream()
                    .map(TransaccionProgramadaDTO::fromEntity)
                    .collect(Collectors.toList());
            model.addAttribute("transaccionesProgramadas", transaccionesProgramadasDTO);

            // Simular transacciones completadas para mostrar
            List<TransaccionProgramadaDTO> transaccionesCompletadasDTO = new ArrayList<>();
            model.addAttribute("transaccionesCompletadas", transaccionesCompletadasDTO);

            // Crear eventos para el calendario
            List<Map<String, Object>> calendarEvents = new ArrayList<>();
            for (TransaccionProgramadaDTO trans : transaccionesProgramadasDTO) {
                Map<String, Object> event = new HashMap<>();
                event.put("title", trans.getDescripcion());
                event.put("start", trans.getFechaProgramada().toString());

                // Determinar color según tipo
                String color;
                String className;
                if (trans.getTipo().toString().equals("DEPOSITO")) {
                    color = "#198754"; // verde
                    className = "fc-event-deposit";
                } else if (trans.getTipo().toString().equals("RETIRO")) {
                    color = "#dc3545"; // rojo
                    className = "fc-event-withdraw";
                } else {
                    color = "#0d6efd"; // azul
                    className = "fc-event-transfer";
                }

                event.put("backgroundColor", color);
                event.put("borderColor", color);
                event.put("className", className);
                event.put("transId", trans.getId());

                calendarEvents.add(event);
            }
            model.addAttribute("calendarEvents", calendarEvents);

            return "programadas";
        } catch (Exception e) {
            e.printStackTrace();
            session.invalidate();
            return "redirect:/login-page";
        }
    }
}