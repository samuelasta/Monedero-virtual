package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class AnalisisController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra la página de análisis de gastos con los datos predefinidos en la plantilla
     */
    @GetMapping("/analytics")
    public String showAnalytics(HttpSession session, Model model) {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            // Obtener información básica del cliente para mostrar en el encabezado
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Convertir cliente a DTO para la vista
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setEmail(cliente.getEmail());
            clienteDTO.setRango(cliente.getRango());

            model.addAttribute("cliente", clienteDTO);

            // La plantilla HTML ya tiene todos los datos necesarios predefinidos,
            // por lo que no es necesario añadir más atributos al modelo

            return "analisis";
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar el análisis: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }
}