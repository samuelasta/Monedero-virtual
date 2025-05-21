package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.dto.MonederoDTO;
import co.uniquindio.edu.mi_moneda.dto.TransaccionDTO;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.repository.MonederoRepository;
import co.uniquindio.edu.mi_moneda.services.interfaces.ClienteService;
import co.uniquindio.edu.mi_moneda.services.interfaces.MonederoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/wallets")
public class MonederosController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private MonederoService monederoService;

    @Autowired
    private MonederoRepository monederoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Muestra la página principal de monederos
     */
    @GetMapping("")
    public String showWallets(HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        String clienteId = (String) session.getAttribute("clienteId");

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Convertir cliente a DTO
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setEmail(cliente.getEmail());
            clienteDTO.setRango(cliente.getRango());

            model.addAttribute("cliente", clienteDTO);

            // Convertir la SimpleList a una List de DTOs
            List<MonederoDTO> monederosDTO = new ArrayList<>();
            SimpleList<Monedero> monederosList = cliente.getMonederos();
            if (monederosList != null && !monederosList.isEmpty()) {
                Node<Monedero> current = monederosList.getFirstNode();
                while (current != null) {
                    monederosDTO.add(MonederoDTO.fromEntity(current.getValue()));
                    current = current.getNextNodo();
                }
            }
            model.addAttribute("clienteMonederos", monederosDTO);

            // Convertir transacciones recientes a DTOs
            List<TransaccionDTO> transaccionesDTO = obtenerTransaccionesRecientes(cliente).stream()
                    .map(TransaccionDTO::fromEntity)
                    .collect(Collectors.toList());
            model.addAttribute("transaccionesRecientes", transaccionesDTO);

            return "wallets";
        } catch (Exception e) {
            e.printStackTrace();
            session.invalidate();
            return "redirect:/login-page?error=session";
        }
    }

    /**
     * Crear un nuevo monedero
     */
    @PostMapping("/new")
    public String crearNuevoMonedero(
            @RequestParam String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam double saldoInicial,
            @RequestParam TipoMonedero tipoMonedero,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String clienteId = (String) session.getAttribute("clienteId");
        if (clienteId == null) {
            return "redirect:/login-page";
        }

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Creamos un nuevo monedero
            Monedero nuevoMonedero = new Monedero();
            nuevoMonedero.setId(UUID.randomUUID().toString());
            nuevoMonedero.setNombre(nombre);
            nuevoMonedero.setPropietario(cliente);
            nuevoMonedero.setTipoMonedero(tipoMonedero);
            nuevoMonedero.setSaldo(saldoInicial);
            nuevoMonedero.setNumeroCuenta(generarNumeroCuentaUnico());
            nuevoMonedero.setFechaCreacion(LocalDateTime.now());
            nuevoMonedero.setActivo(true);

            // Guardar el monedero en la base de datos
            Monedero monederoGuardado = monederoRepository.save(nuevoMonedero);

            // Agregar el monedero a la lista de monederos del cliente
            SimpleList<Monedero> monederos = cliente.getMonederos();
            if (monederos == null) {
                monederos = new SimpleList<>();
            }
            monederos.add(monederoGuardado);
            cliente.setMonederos(monederos);

            // Guardar el cliente actualizado
            clienteRepository.save(cliente);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Monedero '" + nombre + "' creado exitosamente con saldo inicial de $" + saldoInicial);

            return "redirect:/wallets";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al crear el monedero: " + e.getMessage());
            return "redirect:/wallets";
        }
    }

    /**
     * Eliminar un monedero
     */
    @PostMapping("/delete")
    public String eliminarMonedero(
            @RequestParam String id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String clienteId = (String) session.getAttribute("clienteId");
        if (clienteId == null) {
            return "redirect:/login-page";
        }

        try {
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            // Verificar que el monedero existe y pertenece al cliente
            Optional<Monedero> monederoOpt = monederoRepository.findById(id);
            if (monederoOpt.isEmpty()) {
                throw new Exception("El monedero no existe");
            }

            Monedero monedero = monederoOpt.get();
            if (!monedero.getPropietario().getId().equals(cliente.getId())) {
                throw new Exception("No tienes permiso para eliminar este monedero");
            }

            // Verificar que el monedero no tenga saldo
            if (monedero.getSaldo() > 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "No se puede eliminar el monedero porque tiene saldo. Por favor, transfiere el saldo a otro monedero primero.");
                return "redirect:/wallets";
            }

            // Eliminar el monedero de la lista del cliente
            SimpleList<Monedero> monederos = cliente.getMonederos();
            monederos.remove(id);
            cliente.setMonederos(monederos);
            clienteRepository.save(cliente);

            // Desactivar el monedero (o eliminarlo)
            monedero.setActivo(false);
            monederoRepository.save(monedero);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Monedero '" + monedero.getNombre() + "' eliminado exitosamente");

            return "redirect:/wallets";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el monedero: " + e.getMessage());
            return "redirect:/wallets";
        }
    }

    /**
     * Obtener detalles de un monedero (para AJAX)
     */
    @GetMapping("/details/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getWalletDetails(@PathVariable String id, HttpSession session) {
        String clienteId = (String) session.getAttribute("clienteId");
        if (clienteId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "No autorizado"));
        }

        try {
            Optional<Monedero> monederoOpt = monederoRepository.findById(id);
            if (monederoOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Monedero no encontrado"));
            }

            Monedero monedero = monederoOpt.get();
            if (!monedero.getPropietario().getId().equals(clienteId)) {
                return ResponseEntity.status(403).body(Map.of("error", "No tienes permiso para ver este monedero"));
            }

            // Convertir a DTO con información adicional
            MonederoDTO dto = MonederoDTO.fromEntity(monedero);

            // Crear mapa con los datos del monedero
            Map<String, Object> response = new HashMap<>();
            response.put("id", dto.getId());
            response.put("nombre", dto.getNombre());
            response.put("saldo", dto.getSaldo());
            response.put("tipoMonedero", dto.getTipoMonedero().name());
            response.put("numeroCuenta", dto.getNumeroCuenta());
            response.put("numeroCuentaFormateado", dto.getNumeroCuentaFormateado());
            response.put("fechaCreacion", dto.getFechaCreacion() != null ?
                    dto.getFechaCreacion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null);
            response.put("activo", dto.isActivo());

            // Obtener transacciones recientes del monedero (se implementaría en un entorno real)

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Editar un monedero
     */
    @GetMapping("/edit/{id}")
    public String showEditWalletForm(@PathVariable String id, HttpSession session, Model model) {
        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            String clienteId = (String) session.getAttribute("clienteId");
            Cliente cliente = clienteService.buscarClientePorId(clienteId);

            Optional<Monedero> monederoOpt = monederoRepository.findById(id);
            if (monederoOpt.isEmpty()) {
                throw new Exception("Monedero no encontrado");
            }

            Monedero monedero = monederoOpt.get();
            if (!monedero.getPropietario().getId().equals(clienteId)) {
                throw new Exception("No tienes permiso para editar este monedero");
            }

            // Agregar información al modelo
            model.addAttribute("cliente", cliente);
            model.addAttribute("monedero", MonederoDTO.fromEntity(monedero));

            return "editWallet"; // Esta vista deberías crearla
        } catch (Exception e) {
            return "redirect:/wallets?error=" + e.getMessage();
        }
    }

    @PostMapping("/edit")
    public String updateWallet(
            @RequestParam String id,
            @RequestParam String nombre,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (session.getAttribute("clienteId") == null) {
            return "redirect:/login-page";
        }

        try {
            String clienteId = (String) session.getAttribute("clienteId");

            Optional<Monedero> monederoOpt = monederoRepository.findById(id);
            if (monederoOpt.isEmpty()) {
                throw new Exception("Monedero no encontrado");
            }

            Monedero monedero = monederoOpt.get();
            if (!monedero.getPropietario().getId().equals(clienteId)) {
                throw new Exception("No tienes permiso para editar este monedero");
            }

            // Actualizar monedero
            monedero.setNombre(nombre);
            monederoRepository.save(monedero);

            redirectAttributes.addFlashAttribute("successMessage", "Monedero actualizado correctamente");
            return "redirect:/wallets";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el monedero: " + e.getMessage());
            return "redirect:/wallets";
        }
    }

    /**
     * Obtiene las transacciones más recientes del cliente
     */
    private List<Transaccion> obtenerTransaccionesRecientes(Cliente cliente) {
        // En un entorno real, podríamos buscar en la base de datos directamente
        // Aquí usamos la estructura de datos del cliente
        List<Transaccion> resultado = new ArrayList<>();

        if (cliente.getHistorialTransacciones() != null && !cliente.getHistorialTransacciones().isEmpty()) {
            co.uniquindio.edu.mi_moneda.listasPropias.DoubleNode<Transaccion> nodoActual =
                    cliente.getHistorialTransacciones().getLastNode();
            int count = 0;

            while (nodoActual != null && count < 5) {
                resultado.add(nodoActual.getValue());
                nodoActual = nodoActual.getPreviousNodo();
                count++;
            }
        }

        return resultado;
    }

    /**
     * Genera un número de cuenta único
     */
    private String generarNumeroCuentaUnico() {
        java.util.Random random = new java.util.Random();
        String numeroCuenta;
        boolean esUnico = false;

        do {
            // Generar número de 10 dígitos
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            numeroCuenta = sb.toString();

            // Verificar si ya existe un monedero con ese número
            esUnico = !monederoRepository.existsByNumeroCuenta(numeroCuenta);

        } while (!esUnico);

        return numeroCuenta;
    }
}