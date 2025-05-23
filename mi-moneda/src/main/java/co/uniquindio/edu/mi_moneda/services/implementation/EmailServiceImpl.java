package co.uniquindio.edu.mi_moneda.services.implementation;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.services.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendWelcomeEmail(String to, String name, String numeroCuentaFormateado, String numeroCuentaCorriente, String numeroCuentaAhorro) {
        String subject = "Bienvenido a Monedero Virtual";
        String content = "<html><body>" +
                "<h2>Bienvenido a Monedero Virtual, " + name + "!</h2>" +
                "<p>Gracias por registrarte en nuestra plataforma. Ahora puedes disfrutar de todos los beneficios de gestionar tus finanzas con Monedero Virtual.</p>" +
                "<p>Este es el numero de cuenta de tu monedero principal (corriente): </p>"+ numeroCuentaFormateado +
                "<p>Este es el numero de cuenta de tu monedero de ahorros: </p>"+ numeroCuentaCorriente +
                "<p>Este es el numero de cuenta de tu monedero de nomina: </p>"+ numeroCuentaCorriente +

                "<p>Con tu cuenta puedes:</p>" +
                "<ul>" +
                "<li>Crear múltiples monederos virtuales</li>" +
                "<li>Realizar transacciones seguras</li>" +
                "<li>Programar pagos futuros</li>" +
                "<li>Acumular puntos y obtener beneficios</li>" +
                "<li>Analizar tus patrones de gasto</li>" +
                "</ul>" +
                "<p>Para comenzar, simplemente inicia sesión en tu cuenta con el correo electrónico y la contraseña que proporcionaste durante el registro.</p>" +
                "<p>Si tienes alguna pregunta o necesitas ayuda, no dudes en contactar a nuestro equipo de soporte.</p>" +
                "<p>¡Esperamos que disfrutes usando Monedero Virtual!</p>" +
                "<p>Atentamente,<br>El equipo de Monedero Virtual</p>" +
                "</body></html>";

        sendEmail(to, subject, content);
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true indica que el contenido es HTML

            // mi correo
            helper.setFrom("virtualwalletbilleteravirtual@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            // Log el error y manejo de excepciones
            System.err.println("Error al enviar correo electrónico: " + e.getMessage());
        }
    }

    @Override
    public void sendMovementEmail(String to, Monedero monedero, double monto, Cliente cliente, LocalDateTime fechaTransaccion, String motivo) {
        String tipoMovimiento = monto >= 0 ? "depósito" : "retiro";
        String valorFormateado = String.format("%,.2f", Math.abs(monto));
        String subject = "Notificación de " + tipoMovimiento + " - MonederoApp";

        String content = "<html><body>" +
                "<h2>Se ha registrado un " + tipoMovimiento + " en tu monedero</h2>" +

                "<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<p><strong>Detalles de la transacción:</strong></p>" +
                "<p>Monedero: " + monedero.getNombre() + " (" + monedero.getNumeroCuentaFormateado() + ")</p>" +
                "<p>Monto: $" + valorFormateado + "</p>" +
                "<p>Fecha: " + fechaTransaccion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")) + "</p>" +
                "<p>Motivo: " + (motivo != null ? motivo : "No especificado") + "</p>" +
                "<p>Saldo actual: $" + String.format("%,.2f", monedero.getSaldo()) + "</p>" +
                "</div>" +

                "<p>Si no reconoces esta transacción, por favor contacta a nuestro servicio al cliente inmediatamente.</p>" +

                "<p>Recuerda que con MonederoApp puedes:</p>" +
                "<ul>" +
                "<li>Monitorear todas tus transacciones en tiempo real</li>" +
                "<li>Programar pagos futuros</li>" +
                "<li>Analizar tus patrones de gasto</li>" +
                "<li>Acumular puntos y obtener beneficios exclusivos</li>" +
                "</ul>" +

                "<p style='color: #777; font-size: 0.9em;'>Este es un mensaje automático, por favor no responda a este correo.</p>" +
                "<p>Atentamente,<br>El equipo de MonederoApp</p>" +
                "</body></html>";

        sendEmail(to, subject, content);
    }

    @Override
    public void sendMovementEmailTransfer(String to, Monedero monedero, double monto, Cliente remitente, Cliente Destinatario, LocalDateTime fechaTransferencia, String motivo) {
        String tipoMovimiento = monto >= 0 ? "depósito" : "retiro";
        String valorFormateado = String.format("%,.2f", Math.abs(monto));
        String subject = "Notificación de " + tipoMovimiento + " - MonederoApp";

        String content = "<html><body>" +
                "<h2>Se ha registrado un " + tipoMovimiento + " en tu monedero</h2>" +
                "<h2>Remitente: " + remitente.getNombre() + ", ¿lo conoces?</h2>" +

                "<div style='background-color: #f5f5f5; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                "<p><strong>Detalles de la transacción:</strong></p>" +
                "<p>Monedero: " + monedero.getNombre() + " (" + monedero.getNumeroCuentaFormateado() + ")</p>" +
                "<p>Monto: $" + valorFormateado + "</p>" +
                "<p>Fecha: " + fechaTransferencia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")) + "</p>" +
                "<p>Motivo: " + (motivo != null ? motivo : "No especificado") + "</p>" +
                "<p>Saldo actual: $" + String.format("%,.2f", monedero.getSaldo()) + "</p>" +
                "</div>" +

                "<p>Si no reconoces esta transacción, por favor contacta a nuestro servicio al cliente inmediatamente.</p>" +

                "<p>Recuerda que con MonederoApp puedes:</p>" +
                "<ul>" +
                "<li>Monitorear todas tus transacciones en tiempo real</li>" +
                "<li>Programar pagos futuros</li>" +
                "<li>Analizar tus patrones de gasto</li>" +
                "<li>Acumular puntos y obtener beneficios exclusivos</li>" +
                "</ul>" +

                "<p style='color: #777; font-size: 0.9em;'>Este es un mensaje automático, por favor no responda a este correo.</p>" +
                "<p>Atentamente,<br>El equipo de MonederoApp</p>" +
                "</body></html>";

        sendEmail(to, subject, content);
    }



}