package co.uniquindio.edu.mi_moneda.services.implementation;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//Esta es la clase que conectará Spring Security con tus entidades Cliente:
@Service
public class ClienteUserDetailsService implements UserDetailsService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el cliente por email (que actuará como nombre de usuario)
        Cliente cliente = clienteRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró usuario con email: " + username));

        // Construye un objeto UserDetails para Spring Security
        return User.builder()
                .username(cliente.getEmail())
                .password(cliente.getPassword()) // Asume que ya está encriptado
                .roles("USER") // Todos los clientes tendrán rol "USER"
                .build();
    }
}
