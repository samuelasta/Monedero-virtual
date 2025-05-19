package co.uniquindio.edu.mi_moneda.services.interfaces;

import co.uniquindio.edu.mi_moneda.model.Beneficio;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Transaccion;

public interface PuntosService {

    /**
     * calcula los puntos por transacción
     * @param transaccion la transaccion que se está realizando
     * @return la cantidad de puntos generados
     */
    double calcularPuntosPorTransaccion(Transaccion transaccion);

    /**
     * Acumula puntos para un cliente específico
     * @param cliente El cliente al que se le acumularán los puntos
     * @param puntos La cantidad de puntos a acumular
     * @param motivo El motivo por el que se acumulan los puntos
     * @return true si la operación fue exitosa, false en caso contrario
     */
    boolean acumularPuntosCliente(Cliente cliente, double puntos, String motivo);

    /**
     * Canjea puntos para un cliente en especifico
     * @param cliente El cliente que realiza el canje
     * @param beneficio El beneficio por el que canjea los puntos
     * @return true si fue exitoso el canje, false si no
     */
    boolean canjearPuntos(Cliente cliente, Beneficio beneficio);

    /**
     * Actualiza el rango del cliente segun su cantidad de puntos
     * (bronce, plata, oro, platino)
     * @param cliente El cliente al que se le actualiza el rango
     * @return el rango del cliente
     */
    String actualizarRangoCliente(Cliente cliente);


}
