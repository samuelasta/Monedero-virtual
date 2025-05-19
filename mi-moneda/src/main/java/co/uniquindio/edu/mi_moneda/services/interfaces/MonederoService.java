package co.uniquindio.edu.mi_moneda.services.interfaces;


import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import org.springframework.beans.factory.annotation.Autowired;

public interface MonederoService {


    void realizarDeposito(String tipoMonedero, double monto, Cliente cliente);

    void realizarRetiro(String idMonedero, double monto, Cliente cliente);

    void realizarTransferencia(String idMonedero, String numeroCuenta, double monto, Cliente cliente);

    boolean saldoSuficiente(Monedero monedero, double monto);


}
