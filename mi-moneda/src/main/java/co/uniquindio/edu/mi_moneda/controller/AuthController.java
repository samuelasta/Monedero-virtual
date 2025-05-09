package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Muestra la página de login/registro
     */
    @GetMapping({"/", "/login-page"})
    public String showLoginPage() {
        return "login";
    }

    /**
     * Procesa la solicitud de inicio de sesión
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               Model model) {
        try {
            // Intentar autenticar al cliente
            Cliente cliente = clienteService.autenticarCliente(email, password);

            // Si la autenticación es exitosa, guardar el cliente en la sesión
            session.setAttribute("clienteId", cliente.getId());
            session.setAttribute("clienteNombre", cliente.getNombre());
            session.setAttribute("clienteEmail", cliente.getEmail());
            session.setAttribute("clienteRango", cliente.getRango());

            // Redirigir al dashboard
            return "redirect:/dashboard";
        } catch (Exception e) {
            // Si hay un error, mostrar mensaje en la página de login
            model.addAttribute("loginError", e.getMessage());
            return "login";
        }
    }

    /**
     * Procesa la solicitud de registro de un nuevo cliente
     */
    @PostMapping("/register")
    public String processRegistration(@RequestParam(required = false) String clienteId,
                                      @RequestParam String fullName,
                                      @RequestParam String email,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      Model model) {
        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            model.addAttribute("registerError", "Las contraseñas no coinciden");
            return "login";
        }

        try {
            // Determinar el ID a utilizar (proporcionado o generado)
            String id = (clienteId != null && !clienteId.trim().isEmpty())
                    ? clienteId
                    : UUID.randomUUID().toString();

            // Registrar el cliente
            clienteService.registrarCliente(id, fullName, email, password);

            // Mostrar mensaje de éxito y redirigir a la pestaña de login
            model.addAttribute("registrationSuccess", "Registro exitoso. Por favor inicia sesión.");
            return "login";
        } catch (Exception e) {
            // Si hay un error, mostrar mensaje en la página de registro
            model.addAttribute("registerError", e.getMessage());
            return "login";
        }
    }

    /**
     * Muestra el dashboard del cliente autenticado
     */
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        // Obtener información del cliente de la sesión
        String clienteId = (String) session.getAttribute("clienteId");

        try {
            // Obtener el cliente de la base de datos
            Cliente cliente = clienteService.buscarClientePorId(clienteId);
            model.addAttribute("cliente", cliente);

            // Aquí podrías agregar más atributos al modelo según lo que necesites mostrar en el dashboard
            // Por ejemplo, los monederos del cliente, transacciones recientes, etc.

            return "dashboard";
        } catch (Exception e) {
            // Si hay un error (por ejemplo, si el cliente ya no existe), cerrar sesión
            session.invalidate();
            return "redirect:/login-page";
        }
    }

    /**
     * Procesa la solicitud de cierre de sesión
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalidar la sesión
        session.invalidate();

        // Agregar mensaje de éxito para mostrar en la página de login
        redirectAttributes.addFlashAttribute("logoutSuccess", "Has cerrado sesión correctamente");

        return "redirect:/login-page";
    }
}