package co.uniquindio.edu.mi_moneda.controller;

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

    @GetMapping("/terms-and-conditions")
    public String showTermsAndConditions() {
        return "terms";
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