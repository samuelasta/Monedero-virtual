package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.dto.MonederoDTO;
import co.uniquindio.edu.mi_moneda.dto.TransaccionDTO;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleNode;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.repository.MonederoRepository;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import co.uniquindio.edu.mi_moneda.services.interfaces.MonederoService;
import co.uniquindio.edu.mi_moneda.services.interfaces.PuntosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TransactionController {

    @Autowired
    private MonederoService monederoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PuntosService puntosService;

    @Autowired
    private MonederoRepository monederoRepository;

    /**
     * Muestra la página principal de transacciones
     */
    @GetMapping("/transactions")
    public String showTransactions(HttpSession session, Model model,
                                   @RequestParam(required = false) String walletId) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Convertir cliente a DTO
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setEmail(cliente.getEmail());
            clienteDTO.setRango(cliente.getRango());
            model.addAttribute("cliente", clienteDTO);

            // Buscar monederos directamente en la base de datos
            List<Monedero> monederosDB = monederoRepository.findByPropietario(cliente);
            List<MonederoDTO> monederosDTO = monederosDB.stream()
                    .map(MonederoDTO::fromEntity)
                    .collect(Collectors.toList());

            // Añadir esto justo antes de model.addAttribute("monederos", monederosDTO);
            System.out.println("Número de monederos encontrados: " + monederosDTO.size());
            for (MonederoDTO m : monederosDTO) {
                System.out.println("Monedero: " + m.getNombre() + ", ID: " + m.getId());
            }
            model.addAttribute("monederos", monederosDTO);


            // Si se especificó un monedero específico, preseleccionarlo en el formulario
            if (walletId != null && !walletId.isEmpty()) {
                model.addAttribute("selectedWalletId", walletId);
            }

            // Obtener el historial de transacciones del cliente
            List<TransaccionDTO> transaccionesDTO = new ArrayList<>();
            if (cliente.getHistorialTransacciones() != null && !cliente.getHistorialTransacciones().isEmpty()) {
                DoubleNode<Transaccion> currentTrans = cliente.getHistorialTransacciones().getFirstNode();
                while (currentTrans != null) {
                    transaccionesDTO.add(TransaccionDTO.fromEntity(currentTrans.getValue()));
                    currentTrans = currentTrans.getNextNodo();
                }
            }
            model.addAttribute("transacciones", transaccionesDTO);

            return "transactions";
        } catch (Exception e) {
            e.printStackTrace();
            session.invalidate();
            return "redirect:/login-page";
        }
    }


    /**
     * Muestra el formulario de depósito preseleccionando un monedero específico
     */
    @GetMapping("/transactions/deposit")
    public String showDepositForm(HttpSession session, Model model,
                                  @RequestParam(required = false) String walletId) {

        return showTransactions(session, model, walletId);
    }

    /**
     * Muestra el formulario de retiro preseleccionando un monedero específico
     */
    @GetMapping("/transactions/withdraw")
    public String showWithdrawForm(HttpSession session, Model model,
                                   @RequestParam(required = false) String walletId) {
        return showTransactions(session, model, walletId);
    }

    /**
     * Muestra el formulario de transferencia preseleccionando un monedero específico
     */
    @GetMapping("/transactions/transfer")
    public String showTransferForm(HttpSession session, Model model,
                                   @RequestParam(required = false) String walletId) {
        return showTransactions(session, model, walletId);
    }

    /**
     * Procesa el formulario de depósito
     */
    @PostMapping("/transactions/process-deposit")
    public String processDeposit(
            @RequestParam("idMonedero") String idMonedero,
            @RequestParam("monto") double monto,
            @RequestParam("metodo") String metodo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            String clienteId = (String) session.getAttribute("clienteId");
            if (clienteId == null) {
                return "redirect:/login-page";
            }

            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Realizar el depósito utilizando el servicio
            monederoService.realizarDeposito(idMonedero, monto, cliente);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage",
                    "Depósito realizado con éxito por $" + String.format("%,.2f", monto));

            return "redirect:/transactions";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al realizar el depósito: " + e.getMessage());
            return "redirect:/transactions";
        }
    }

    /**
     * Procesa el formulario de retiro
     */
    @PostMapping("/transactions/process-withdraw")
    public String processWithdraw(
            @RequestParam("idMonedero") String idMonedero,
            @RequestParam("monto") double monto,
            @RequestParam("metodo") String metodo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            String clienteId = (String) session.getAttribute("clienteId");
            if (clienteId == null) {
                return "redirect:/login-page";
            }

            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Realizar el retiro utilizando el servicio
            monederoService.realizarRetiro(idMonedero, monto, cliente);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage",
                    "Retiro realizado con éxito por $" + String.format("%,.2f", monto));

            return "redirect:/transactions";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al realizar el retiro: " + e.getMessage());
            return "redirect:/transactions";
        }
    }

    /**
     * Procesa el formulario de transferencia
     */
    @PostMapping("/transactions/process-transfer")
    public String processTransfer(
            @RequestParam("idMonederoOrigen") String idMonederoOrigen,
            @RequestParam(value = "idMonederoDestino", required = false) String idMonederoDestino,
            @RequestParam(value = "numeroCuentaDestino", required = false) String numeroCuentaDestino,
            @RequestParam("monto") double monto,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            String clienteId = (String) session.getAttribute("clienteId");
            if (clienteId == null) {
                return "redirect:/login-page";
            }

            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Validar que se haya proporcionado un destino
            if ((idMonederoDestino == null || idMonederoDestino.isEmpty()) &&
                    (numeroCuentaDestino == null || numeroCuentaDestino.isEmpty())) {
                throw new Exception("Debe especificar un monedero destino");
            }

            // Determinar el destino de la transferencia
            String destinoFinal = idMonederoDestino != null && !idMonederoDestino.isEmpty() ?
                    idMonederoDestino : numeroCuentaDestino;

            // Realizar la transferencia utilizando el servicio
            monederoService.realizarTransferencia(idMonederoOrigen, destinoFinal, monto, cliente);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage",
                    "Transferencia realizada con éxito por $" + String.format("%,.2f", monto));

            return "redirect:/transactions";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al realizar la transferencia: " + e.getMessage());
            return "redirect:/transactions";
        }
    }

    /**
     * Obtiene los detalles de una transacción para mostrarlos en el modal
     */
    @GetMapping("/transactions/details/{id}")
    @ResponseBody
    public Map<String, Object> getTransactionDetails(@PathVariable("id") String id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String clienteId = (String) session.getAttribute("clienteId");
            if (clienteId == null) {
                response.put("error", "Sesión no válida");
                return response;
            }

            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Buscar la transacción en el historial del cliente
            Transaccion transaccion = null;
            if (cliente.getHistorialTransacciones() != null && !cliente.getHistorialTransacciones().isEmpty()) {
                DoubleNode<Transaccion> current = cliente.getHistorialTransacciones().getFirstNode();
                while (current != null) {
                    if (current.getValue().getId().equals(id)) {
                        transaccion = current.getValue();
                        break;
                    }
                    current = current.getNextNodo();
                }
            }

            if (transaccion != null) {
                // Convertir a DTO para la respuesta
                TransaccionDTO dto = TransaccionDTO.fromEntity(transaccion);

                // Calcular los puntos ganados por esta transacción
                double puntos = puntosService.calcularPuntosPorTransaccion(transaccion);

                // Añadir toda la información al mapa de respuesta
                response.put("transaccion", dto);
                response.put("puntosGanados", puntos);
                response.put("success", true);
            } else {
                response.put("error", "Transacción no encontrada");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Error al obtener detalles: " + e.getMessage());
        }

        return response;
    }

    /**
     * Programa una transacción futura
     */
    @PostMapping("/transactions/schedule")
    public String scheduleTransaction(
            @RequestParam("tipo") TipoTransaccion tipo,
            @RequestParam("origenId") String origenId,
            @RequestParam(value = "destinoId", required = false) String destinoId,
            @RequestParam("monto") double monto,
            @RequestParam("fechaProgramada") String fechaProgramada,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "periodoRecurrencia", required = false) String periodoRecurrencia,
            @RequestParam(value = "recurrente", required = false) boolean recurrente,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // TODO: Implementar la programación de transacciones


            redirectAttributes.addFlashAttribute("successMessage",
                    "Transacción programada con éxito para " + fechaProgramada);

            return "redirect:/transactions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al programar la transacción: " + e.getMessage());
            return "redirect:/transactions";
        }
    }
}