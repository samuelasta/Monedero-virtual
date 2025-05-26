package co.uniquindio.edu.mi_moneda.services.implementation;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.repository.MonederoRepository;
import co.uniquindio.edu.mi_moneda.repository.TransaccionRepository;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import co.uniquindio.edu.mi_moneda.services.interfaces.EmailService;
import co.uniquindio.edu.mi_moneda.services.interfaces.MonederoService;
import co.uniquindio.edu.mi_moneda.services.interfaces.PuntosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonederoServiceImpl implements MonederoService {

    @Autowired
    private MonederoRepository monederoRepository;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PuntosService puntosService;

    @Autowired
    private EmailService emailService;

    /**
     * Realizamos un deposito a un monedero, creamos el registro de la transacción
     *
     * @param idMonedero
     * @param monto
     * @param cliente
     */
    @Override
    public void realizarDeposito(String idMonedero, double monto, Cliente cliente) {
        // Primero buscamos el monedero específico por su ID
        Optional<Monedero> monederoOptional = monederoRepository.findById(idMonedero);

        if (monederoOptional.isPresent()) {
            Monedero monedero = monederoOptional.get();

            // Verificamos que el monedero pertenezca al cliente
            if (monedero.getPropietario() != null &&
                    monedero.getPropietario().getId().equals(cliente.getId())) {

                // Actualizamos el saldo
                monedero.setSaldo(monedero.getSaldo() + monto);

                // Guardamos el monedero actualizado
                monederoRepository.save(monedero);

                // Creamos un registro de transacción
                Transaccion transaccion = new Transaccion();
                transaccion.setId(cliente.getId());
                transaccion.setTipo(TipoTransaccion.DEPOSITO);
                transaccion.setMonto(monto);
                transaccion.setFecha(LocalDateTime.now());
                transaccion.setDescripcion("Depósito en monedero: " + monedero.getNombre());
                transaccion.setDestino(monedero);
                transaccion.setActiva(true);

                // Guardamos la transacción
                transaccionRepository.save(transaccion);

                double puntosCliente = puntosService.calcularPuntosPorTransaccion(transaccion);
                String motivo = "Deposito realizado";
                puntosService.acumularPuntosCliente(cliente, puntosCliente, motivo );

                // Agregamos la transacción al historial del monedero
                if (monedero.getHistorialTransacciones() == null) {
                    monedero.setHistorialTransacciones(new DoubleList<>());
                }
                monedero.getHistorialTransacciones().add(transaccion);

                // También agregamos al historial del cliente
                if (cliente.getHistorialTransacciones() == null) {
                    cliente.setHistorialTransacciones(new DoubleList<>());
                }
                cliente.getHistorialTransacciones().add(transaccion);

                // Actualizamos el cliente
                clienteRepository.save(cliente);

                LocalDateTime fechaTransferencia = LocalDateTime.now();
                emailService.sendMovementEmail(cliente.getEmail(), monedero, monto, cliente, fechaTransferencia, motivo);

            } else {
                throw new RuntimeException("El monedero no pertenece al cliente");
            }
        } else {
            throw new RuntimeException("Monedero no encontrado con ID: " + idMonedero);
        }
    }


    /**
     * Realizamos un retiro, creamos el registro de la transacción, loa gregamos
     *
     * @param idMonedero
     * @param monto
     * @param cliente
     */
    @Override
    public void realizarRetiro(String idMonedero, double monto, Cliente cliente) {
        // Primero buscamos el monedero específico por su ID
        Optional<Monedero> monederoOptional = monederoRepository.findById(idMonedero);

        if (monederoOptional.isPresent()) {
            Monedero monedero = monederoOptional.get();

            // Verificamos que el monedero pertenezca al cliente y que tenga saldo suficiente
            if (monedero.getPropietario() != null &&
                    monedero.getPropietario().getId().equals(cliente.getId()) && saldoSuficiente(monedero, monto)) {

                // Actualizamos el saldo
                monedero.setSaldo(monedero.getSaldo() - monto);

                // Guardamos el monedero actualizado
                monederoRepository.save(monedero);


                // Creamos un registro de transacción
                Transaccion transaccion = new Transaccion();
                transaccion.setId(cliente.getId());
                transaccion.setTipo(TipoTransaccion.RETIRO);
                transaccion.setMonto(monto);
                transaccion.setFecha(LocalDateTime.now());
                transaccion.setDescripcion("Retiro en monedero: " + monedero.getNombre());
                transaccion.setDestino(monedero);
                transaccion.setActiva(true);

                // Guardamos la transacción
                transaccionRepository.save(transaccion);
                double puntosCliente = puntosService.calcularPuntosPorTransaccion(transaccion);
                String motivo = "Retiro realizado";
                puntosService.acumularPuntosCliente(cliente, puntosCliente, motivo );

                // Agregamos la transacción al historial del monedero
                if (monedero.getHistorialTransacciones() == null) {
                    monedero.setHistorialTransacciones(new DoubleList<>());
                }
                monedero.getHistorialTransacciones().add(transaccion);

                // También agregamos al historial del cliente
                if (cliente.getHistorialTransacciones() == null) {
                    cliente.setHistorialTransacciones(new DoubleList<>());
                }
                cliente.getHistorialTransacciones().add(transaccion);

                // Actualizamos el cliente
                clienteRepository.save(cliente);

                LocalDateTime fechaTransferencia = LocalDateTime.now();
                emailService.sendMovementEmail(cliente.getEmail(), monedero, monto, cliente, fechaTransferencia, motivo);

            } else {
                throw new RuntimeException("El monedero no pertenece al cliente");
            }
        } else {
            throw new RuntimeException("Monedero no encontrado con ID: " + idMonedero);
        }
    }

    @Override
    public void realizarTransferencia(String idMonedero, String numeroCuenta, double monto, Cliente cliente) {
        // Primero buscamos el monedero específico por su ID
        Optional<Monedero> monederoOptional = monederoRepository.findById(idMonedero);
        // Buscamos el monedero destino por numero de cuenta
        Optional<Monedero> monederoDestinoOptional = monederoRepository.findByNumeroCuenta(numeroCuenta);

        if (monederoOptional.isPresent() && monederoDestinoOptional.isPresent()) {
            Monedero monedero = monederoOptional.get();
            Monedero monederoDestino = monederoDestinoOptional.get();

            // Verificamos que el monedero pertenezca al cliente y que tenga saldo suficiente
            if (monedero.getPropietario() != null &&
                    monedero.getPropietario().getId().equals(cliente.getId()) && saldoSuficiente(monedero, monto)) {

                // Actualizamos el saldo de la persona que transfirio
                monedero.setSaldo(monedero.getSaldo() - monto);

                // Actualizamos el saldo de la persona que la recibe
                monederoDestino.setSaldo(monederoDestino.getSaldo() + monto);



                // Guardamos el monedero actualizado
                monederoRepository.save(monedero);
                monederoRepository.save(monederoDestino);

                // Creamos un registro de transacción para el que la transfirió
                Transaccion transaccion = new Transaccion();
                transaccion.setId(cliente.getId());
                transaccion.setTipo(TipoTransaccion.TRANSFERENCIA);
                transaccion.setMonto(monto);
                transaccion.setFecha(LocalDateTime.now());
                transaccion.setDescripcion("Transferencia en monedero: " + monedero.getNombre());
                transaccion.setDestino(monederoDestino);
                transaccion.setActiva(true);

                // Guardamos la transacción
                transaccionRepository.save(transaccion);

                // Creamos un registro de transacción para el que la recibe

                Transaccion transaccionDestino = new Transaccion();
                transaccion.setId(monederoDestino.getPropietario().getId());
                transaccion.setTipo(TipoTransaccion.TRANSFERENCIA);
                transaccion.setMonto(monto);
                transaccion.setFecha(LocalDateTime.now());
                transaccion.setDescripcion("Transferencia recibida en monedero: " + monedero.getNombre());
                transaccion.setDestino(monederoDestino);
                transaccion.setActiva(true);

                // Guardamos la transacción
                transaccionRepository.save(transaccionDestino);

                double puntosCliente = puntosService.calcularPuntosPorTransaccion(transaccion);
                String motivo = "Transacción realizada";
                puntosService.acumularPuntosCliente(cliente, puntosCliente, motivo );

                // Agregamos la transacción al historial del monedero
                if (monedero.getHistorialTransacciones() == null) {
                    monedero.setHistorialTransacciones(new DoubleList<>());
                }
                monedero.getHistorialTransacciones().add(transaccion);
                monedero.getHistorialTransacciones().add(transaccionDestino);

                // También agregamos al historial del cliente
                if (cliente.getHistorialTransacciones() == null) {
                    cliente.setHistorialTransacciones(new DoubleList<>());
                }
                cliente.getHistorialTransacciones().add(transaccion);
                monederoDestino.getPropietario().getHistorialTransacciones().add(transaccionDestino);

                // Actualizamos el cliente
                clienteRepository.save(cliente);
                clienteRepository.save(monederoDestino.getPropietario());

                //el mensaje para el que salió el dinero
                LocalDateTime fechaTransferencia = LocalDateTime.now();
                emailService.sendMovementEmail(cliente.getEmail(), monedero, monto, cliente, fechaTransferencia, motivo);

                String emailDestinatario = monederoDestino.getPropietario().getEmail();
                Cliente clienteDestino = monederoDestino.getPropietario();
                emailService.sendMovementEmailTransfer(emailDestinatario, monederoDestino, monto, cliente, clienteDestino, fechaTransferencia, motivo);




            } else {
                throw new RuntimeException("El monedero no pertenece al cliente");
            }
        } else {
            throw new RuntimeException("Monedero no encontrado con ID: " + idMonedero);
        }

        }

    @Override
    public boolean saldoSuficiente(Monedero monedero, double monto) {
        boolean salfo = false;
        if(monedero.getSaldo() >= monto){
            return true;
        }
        return false;
    }
}

