package co.uniquindio.edu.mi_moneda.config;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.sesionIniciada.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private ClienteRepository clienteRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permitir acceso a páginas públicas y recursos estáticos
                        .requestMatchers("/", "/login-page", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                        // Permitir el endpoint de prueba
                        .requestMatchers("/test-endpoint", "/dashboard-test").permitAll()
                        // Requerir autenticación para todo lo demás
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login-page")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler) // Usar el handler personalizado
                        .failureUrl("/login-page?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login-page?logout")
                        .permitAll()
                )
                // Agrega esto para registrar explícitamente authenticationProvider
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Usa directamente una implementación de UserDetailsService
        authProvider.setUserDetailsService(username -> {
            System.out.println("Autenticando usuario: " + username);

            try {
                Cliente cliente = clienteRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + username));

                System.out.println("Usuario encontrado: " + cliente.getNombre());
                System.out.println("Contraseña cifrada: " + cliente.getPassword());

                return User.builder()
                        .username(cliente.getEmail())
                        .password(cliente.getPassword())
                        .roles("USER")
                        .build();
            } catch (Exception e) {
                System.out.println("Error al autenticar: " + e.getMessage());
                throw e;
            }
        });

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Para eliminar la advertencia
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/css/**");
    }
}