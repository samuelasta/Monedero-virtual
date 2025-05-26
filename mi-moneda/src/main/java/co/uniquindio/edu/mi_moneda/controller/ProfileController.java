package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;

@Controller
public class ProfileController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Muestra la página de perfil del cliente con su información
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            // Obtener información del cliente
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Convertir a DTO para la vista
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setEmail(cliente.getEmail());
            clienteDTO.setRango(cliente.getRango());

            // Formatear fechas si es necesario
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
            model.addAttribute("formatter", formatter);

            // Añadir cliente al modelo
            model.addAttribute("cliente", clienteDTO);

            return "perfil";
        } catch (Exception e) {
            session.setAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    /**
     * Actualiza la información del perfil del cliente
     */
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "telefono", required = false) String telefono,
            @RequestParam(value = "fechaNacimiento", required = false) String fechaNacimiento,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam(value = "ciudad", required = false) String ciudad,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestParam(value = "codigoPostal", required = false) String codigoPostal,
            @RequestParam(value = "pais", required = false) String pais,
            @RequestParam(value = "idioma", required = false) String idioma,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            // Obtener información del cliente
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Actualizar campos del cliente
            cliente.setNombre(nombre);

            // Guardar los cambios
            clienteService.actualizarPerfil(cliente);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage", "Perfil actualizado correctamente");

            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    /**
     * Cambia la contraseña del cliente
     */
    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // Verificar si el usuario está autenticado
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            // Validar que las contraseñas coincidan
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Las contraseñas no coinciden");
                return "redirect:/profile";
            }

            // Obtener información del cliente
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Verificar que la contraseña actual sea correcta
            if (!passwordEncoder.matches(currentPassword, cliente.getPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta");
                return "redirect:/profile";
            }

            // Verificar requisitos mínimos de la contraseña
            if (newPassword.length() < 8) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "La nueva contraseña debe tener al menos 8 caracteres");
                return "redirect:/profile";
            }

            // Actualizar la contraseña
            cliente.setPassword(passwordEncoder.encode(newPassword));
            clienteService.actualizarPerfil(cliente);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage", "Contraseña actualizada correctamente");

            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cambiar la contraseña: " + e.getMessage());
            return "redirect:/profile";
        }
    }
}