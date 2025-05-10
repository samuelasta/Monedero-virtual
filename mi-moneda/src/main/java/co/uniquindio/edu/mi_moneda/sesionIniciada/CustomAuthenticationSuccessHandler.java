package co.uniquindio.edu.mi_moneda.sesionIniciada;

import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email = authentication.getName();
        System.out.println("Login exitoso para: " + email);

        // Buscar el cliente y guardar su ID en la sesión
        clienteRepository.findByEmail(email).ifPresent(cliente -> {
            HttpSession session = request.getSession();
            session.setAttribute("clienteId", cliente.getId());
            session.setAttribute("clienteNombre", cliente.getNombre());
            session.setAttribute("clienteEmail", cliente.getEmail());
            System.out.println("Guardado clienteId en sesión: " + cliente.getId());
        });

        // Redirigir al dashboard
        response.sendRedirect("/dashboard");
    }
}
