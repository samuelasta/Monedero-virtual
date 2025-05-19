package co.uniquindio.edu.mi_moneda.services.implementation;

import co.uniquindio.edu.mi_moneda.model.*;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccionPuntos;
import co.uniquindio.edu.mi_moneda.repository.*;
import co.uniquindio.edu.mi_moneda.services.interfaces.PuntosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PuntosServiceImpl implements PuntosService {

    @Autowired
    private TransaccionPuntosRepository transaccionPuntosRepository;
    @Autowired
    private BeneficioRepository beneficioRepository;
    @Autowired
    private TransaccionRepository transaccionRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PuntosRepository puntosRepository;

    @Override
    public double calcularPuntosPorTransaccion(Transaccion transaccion) {
        if(transaccion == null || !transaccion.isActiva()){
            return 0;
        }
        double monto = transaccion.getMonto();
        if(transaccion.getTipo() == TipoTransaccion.DEPOSITO){
            return (monto /100 ); // 1 punto por cada 100 unidades de moneda depositadas
        }
        else if(transaccion.getTipo() == TipoTransaccion.RETIRO){
            return ((monto / 100) * 2); // 2 puntos por cada 100 unidades retiradas
        }
        return ((monto / 100) * 3); // 3 puntos por cada 100 unidades transferidas
    }

    @Override
    public boolean acumularPuntosCliente(Cliente cliente, double puntos, String motivo) {
        if (cliente == null || puntos <= 0) {
            return false;
        }

        // Obtener objeto de puntos del cliente
        Puntos puntosCliente = cliente.getPuntos();

        if (puntosCliente == null) {
            // Si el cliente no tiene puntos, crear nuevo objeto
            puntosCliente = Puntos.builder()
                    .id(UUID.randomUUID().toString())
                    .cliente(cliente)
                    .puntosAcumulados(0)
                    .build();
            cliente.setPuntos(puntosCliente);
        }

        // Incrementar puntos
        puntosCliente.setPuntosAcumulados(puntosCliente.getPuntosAcumulados() + puntos);

        // Registrar transacción de puntos
        TransaccionPuntos transaccionPuntos = TransaccionPuntos.builder()
                .id(UUID.randomUUID().toString())
                .tipo(TipoTransaccionPuntos.ACUMULACION)
                .cantidad(puntos)
                .fecha(LocalDateTime.now())
                .motivo(motivo)
                .build();

        // Guardar la transacción en el repositorio
        transaccionPuntos = transaccionPuntosRepository.save(transaccionPuntos);

        //añadimos la transaccion a la lista simple de historial de transaccion de puntos
        puntosCliente.getHistorialPuntos().add(transaccionPuntos);

        // Guardar en repositorios
        puntosRepository.save(puntosCliente);
        clienteRepository.save(cliente);

        // Actualizar rango del cliente después de acumular puntos
        actualizarRangoCliente(cliente);

        return true;
    }

    @Override
    public boolean canjearPuntos(Cliente cliente, Beneficio beneficio) {
        if (cliente == null || beneficio == null) {
            return false;
        }

        Puntos puntosCliente = cliente.getPuntos();

        if (puntosCliente == null || puntosCliente.getPuntosAcumulados() < beneficio.getCostePuntos()) {
            return false; // No tiene suficientes puntos
        }

        // Restar puntos al cliente
        puntosCliente.setPuntosAcumulados(puntosCliente.getPuntosAcumulados() - beneficio.getCostePuntos());

        // Registrar transacción de canje
        TransaccionPuntos transaccionPuntos = TransaccionPuntos.builder()
                .id(UUID.randomUUID().toString())
                .tipo(TipoTransaccionPuntos.CANJE)
                .cantidad(beneficio.getCostePuntos())
                .fecha(LocalDateTime.now())
                .motivo("Canje por: " + beneficio.getNombre())
                .build();

        // Guardar la transacción en el repositorio
        transaccionPuntos = transaccionPuntosRepository.save(transaccionPuntos);

        //añadimos la transaccion a la lista simple de historial de transaccion de puntos
        puntosCliente.getHistorialPuntos().add(transaccionPuntos);

        // Guardar en repositorios
        puntosRepository.save(puntosCliente);
        clienteRepository.save(cliente);

        // Actualizar rango después del canje
        actualizarRangoCliente(cliente);

        return true;
    }

    @Override
    public String actualizarRangoCliente(Cliente cliente) {
        if (cliente == null || cliente.getPuntos() == null) {
            return "BRONCE"; // Rango por defecto
        }

        double puntosTotales = cliente.getPuntos().getPuntosAcumulados();
        String nuevoRango;

        // Lógica para determinar el rango según los puntos
        if (puntosTotales <= 500) {
            nuevoRango = "BRONCE";
        } else if (puntosTotales > 500 && puntosTotales <= 1000) {
            nuevoRango = "PLATA";
        } else if (puntosTotales > 1000 && puntosTotales <= 5000) {
            nuevoRango = "ORO";
        } else {
            nuevoRango = "PLATINO";
        }

        // Actualizar rango solo si ha cambiado
        if (!nuevoRango.equals(cliente.getRango())) {
            cliente.setRango(nuevoRango);
            clienteRepository.save(cliente);
        }

        return nuevoRango;
    }
}
