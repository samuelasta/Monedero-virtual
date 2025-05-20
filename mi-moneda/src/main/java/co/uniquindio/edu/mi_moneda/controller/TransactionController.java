package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.dto.MonederoDTO;
import co.uniquindio.edu.mi_moneda.dto.TransaccionDTO;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import co.uniquindio.edu.mi_moneda.services.interfaces.MonederoService;
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
import java.util.List;
import java.util.UUID;

@Controller
public class TransactionController {

    @Autowired
    private MonederoService monederoService;

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra la página de transacciones con los datos del cliente
     */
    @GetMapping("/transactions-view")
    public String showTransactions(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            String clienteId = (String) session.getAttribute("clienteId");

            // Obtener el cliente como DTO
            ClienteDTO clienteDTO = clienteService.buscarClienteDTOPorId(clienteId);
            model.addAttribute("cliente", clienteDTO);

            // Obtener la entidad cliente para acceso a datos más específicos
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Convertir la lista de monederos para el selector de la interfaz
            List<MonederoDTO> monederosDTO = new ArrayList<>();
            if (cliente.getMonederos() != null && !cliente.getMonederos().isEmpty()) {
                Node<Monedero> current = cliente.getMonederos().getFirstNode();
                while (current != null) {
                    monederosDTO.add(MonederoDTO.fromEntity(current.getValue()));
                    current = current.getNextNodo();
                }
            }
            model.addAttribute("monederos", monederosDTO);

            // Verificar si hay mensajes de éxito o error
            if (model.containsAttribute("successMessage")) {
                model.addAttribute("showSuccess", true);
            }

            if (model.containsAttribute("errorMessage")) {
                model.addAttribute("showError", true);
            }

            return "transactions";
        } catch (Exception e) {
            session.invalidate();
            return "redirect:/login-page";
        }
    }

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

    /**
     * Procesa la solicitud de retiro
     */
    @PostMapping("/transactions/process-withdraw")
    public String processWithdraw(
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

            // Realizamos el retiro
            monederoService.realizarRetiro(idMonedero, monto, cliente);

            // mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage",
                    "Retiro realizado con éxito por $" + String.format("%,.2f", monto));

            // Redirigir al dashboard o a la página de transacciones
            return "redirect:/dashboard";

        } catch (Exception e) {
            // En caso de error, agregar mensaje de error
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al realizar el retiro: " + e.getMessage());

            return "redirect:/transactions";
        }
    }

    /**
     * Procesa la solicitud de transferencia
     */
    @PostMapping("/transactions/process-transfer")
    public String processTransfer(
            @RequestParam("idMonederoOrigen") String idMonederoOrigen,
            @RequestParam("numeroCuentaDestino") String numeroCuentaDestino,
            @RequestParam("monto") double monto,
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

            // Realizamos la transferencia
            monederoService.realizarTransferencia(idMonederoOrigen, numeroCuentaDestino, monto, cliente);

            // mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transferencia realizada con éxito por $" + String.format("%,.2f", monto));

            // Redirigir al dashboard o a la página de transacciones
            return "redirect:/dashboard";

        } catch (Exception e) {
            // En caso de error, agregar mensaje de error
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al realizar la transferencia: " + e.getMessage());

            return "redirect:/transactions";
        }
    }
}