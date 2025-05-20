package co.uniquindio.edu.mi_moneda.dto;

import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleNode;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.listasPropias.NodeQueue;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Notificacion;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.TransaccionProgramada;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String id;
    private String nombre;
    private String email;
    private String rango;
    private PuntosDTO puntos;
    private List<MonederoDTO> monederos;
    private List<TransaccionDTO> transaccionesRecientes;
    private List<TransaccionProgramadaDTO> transaccionesProgramadas;
    private List<NotificacionDTO> notificacionesNoLeidas;
    private int cantidadMonederos;
    private int cantidadNotificaciones;
    private double balanceTotal;

    /**
     * Crea un DTO a partir de la entidad Cliente y otros datos adicionales
     */
    public static ClienteDTO fromEntity(Cliente cliente, double balanceTotal,
                                        List<Transaccion> transaccionesRecientes,
                                        List<TransaccionProgramada> transaccionesProgramadas,
                                        List<Notificacion> notificacionesNoLeidas) {
        if (cliente == null) {
            return null;
        }

        ClienteDTO dto = new ClienteDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        dto.setRango(cliente.getRango());

        // Puntos
        if (cliente.getPuntos() != null) {
            dto.setPuntos(PuntosDTO.fromEntity(cliente.getPuntos()));
        }

        // Monederos
        List<MonederoDTO> monederosDto = new ArrayList<>();
        if (cliente.getMonederos() != null && !cliente.getMonederos().isEmpty()) {
            Node<Monedero> current = cliente.getMonederos().getFirstNode();
            while (current != null) {
                monederosDto.add(MonederoDTO.fromEntity(current.getValue()));
                current = current.getNextNodo();
            }
        }
        dto.setMonederos(monederosDto);
        dto.setCantidadMonederos(monederosDto.size());

        // Transacciones recientes (ya recibidas como parámetro)
        List<TransaccionDTO> transaccionesDto = new ArrayList<>();
        for (Transaccion transaccion : transaccionesRecientes) {
            transaccionesDto.add(TransaccionDTO.fromEntity(transaccion));
        }
        dto.setTransaccionesRecientes(transaccionesDto);

        // Transacciones programadas (ya recibidas como parámetro)
        List<TransaccionProgramadaDTO> programadasDto = new ArrayList<>();
        for (TransaccionProgramada programada : transaccionesProgramadas) {
            programadasDto.add(TransaccionProgramadaDTO.fromEntity(programada));
        }
        dto.setTransaccionesProgramadas(programadasDto);

        // Notificaciones no leídas (ya recibidas como parámetro)
        List<NotificacionDTO> notificacionesDto = new ArrayList<>();
        for (Notificacion notificacion : notificacionesNoLeidas) {
            notificacionesDto.add(NotificacionDTO.fromEntity(notificacion));
        }
        dto.setNotificacionesNoLeidas(notificacionesDto);
        dto.setCantidadNotificaciones(notificacionesNoLeidas.size());

        // Balance total
        dto.setBalanceTotal(balanceTotal);

        return dto;
    }
}