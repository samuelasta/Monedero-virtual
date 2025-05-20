package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MonederosController {

    @Autowired
    private ClienteService clienteService;

    /**
     * LLeva hacia la ventana de transacciones
     */
    @GetMapping("/wallets/dropdown-menu dropdown-menu-end")
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
}
