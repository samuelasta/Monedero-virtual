package co.uniquindio.edu.mi_moneda.services.interfaces;

public interface EmailService {
    void sendWelcomeEmail(String to, String name, String numeroCuentaFormateado, String numeroCuentaCorriente, String numeroCuentaAhorro);
    void sendEmail(String to, String subject, String content);
}