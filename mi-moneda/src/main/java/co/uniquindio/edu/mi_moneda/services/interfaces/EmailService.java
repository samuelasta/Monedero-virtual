package co.uniquindio.edu.mi_moneda.services.interfaces;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;

import java.time.LocalDateTime;

public interface EmailService {

    void sendWelcomeEmail(String to, String name, String numeroCuentaFormateado, String numeroCuentaCorriente, String numeroCuentaAhorro);

    void sendEmail(String to, String subject, String content);

    void sendMovementEmail(String to, Monedero monedero, double monto, Cliente cliente, LocalDateTime fechaTransferencia, String motivo);

    void sendMovementEmailTransfer(String to, Monedero monedero, double monto, Cliente remitente, Cliente Destinatario, LocalDateTime fechaTransferencia, String motivo);

}