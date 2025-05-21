package co.uniquindio.edu.mi_moneda.services.interfaces;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import org.springframework.stereotype.Service;

@Service
public interface TransaccionProgramadaService {

    void transaccionProgramada(Cliente cliente);
}
