package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import co.uniquindio.edu.mi_moneda.services.interfaces.MonederoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class TransactionController {

    @Autowired
    private MonederoService monederoService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Procesa la solicitud de depósito
     */
    @PostMapping("/transactions/process-deposit")
    public String processDeposit(
            @RequestParam("idMonedero") String idMonedero,
            @RequestParam("monto") double monto,
            @RequestParam("metodo") String metodo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Obtener el ID del cliente desde la sesión
            String clienteId = (String) session.getAttribute("clienteId");
            if (clienteId == null) {
                return "redirect:/login-page";
            }

            // Obtenemos el cliente usando el servicio
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Realizamos el depósito
            monederoService.realizarDeposito(idMonedero, monto, cliente);

            // mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage",
                    "Depósito realizado con éxito por $" + String.format("%,.2f", monto));

            // Redirigir al dashboard o a la página de transacciones
            return "redirect:/dashboard";

        } catch (Exception e) {
            // En caso de error, agregar mensaje de error
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al realizar el depósito: " + e.getMessage());

            return "redirect:/transactions";
        }
    }
}