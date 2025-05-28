package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.dto.ClienteDTO;
import co.uniquindio.edu.mi_moneda.dto.MonederoDTO;
import co.uniquindio.edu.mi_moneda.dto.TransaccionDTO;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleNode;
import co.uniquindio.edu.mi_moneda.listasPropias.Node;
import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.enums.TipoMonedero;
import co.uniquindio.edu.mi_moneda.model.enums.TipoTransaccion;
import co.uniquindio.edu.mi_moneda.repository.ClienteRepository;
import co.uniquindio.edu.mi_moneda.repository.MonederoRepository;
import co.uniquindio.edu.mi_moneda.repository.TransaccionRepository;
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
public class MonederosController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private MonederoService monederoService;

    @Autowired
    private MonederoRepository monederoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * LLeva hacia la ventana de wallets
     */
    @GetMapping("/wallets")
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

            // AQUÍ ESTÁ EL PROBLEMA: Buscar monederos directamente en la base de datos
            // En lugar de confiar solo en la lista del cliente
            List<Monedero> monederosDB = monederoRepository.findByPropietario(cliente);

            System.out.println("=== DEBUG WALLETS ===");
            System.out.println("Cliente ID: " + clienteId);
            System.out.println("Cliente nombre: " + cliente.getNombre());
            System.out.println("Monederos encontrados en DB: " + monederosDB.size());

            // Convertir a DTOs
            List<MonederoDTO> monederosDTO = new ArrayList<>();
            for (Monedero monedero : monederosDB) {
                System.out.println("Monedero encontrado: " + monedero.getNombre() + " - Saldo: " + monedero.getSaldo());
                monederosDTO.add(MonederoDTO.fromEntity(monedero));
            }

            model.addAttribute("clienteMonederos", monederosDTO);

            // Buscar transacciones recientes directamente en la base de datos
            List<TransaccionDTO> transaccionesDTO = obtenerTransaccionesRecientesFromDB(cliente);
            model.addAttribute("transaccionesRecientes", transaccionesDTO);

            System.out.println("Transacciones encontradas: " + transaccionesDTO.size());
            System.out.println("=====================");

            return "wallets";
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error en showWallets: " + e.getMessage());
            session.invalidate();
            return "redirect:/login-page?error=session";
        }
    }

    /**
     * Obtiene las transacciones recientes directamente de la base de datos
     */
    private List<TransaccionDTO> obtenerTransaccionesRecientesFromDB(Cliente cliente) {
        List<TransaccionDTO> resultado = new ArrayList<>();

        try {
            // Buscar todas las transacciones donde el cliente sea origen o destino
            List<Transaccion> todasLasTransacciones = transaccionRepository.findAll();

            // Filtrar transacciones del cliente
            List<Transaccion> transaccionesCliente = todasLasTransacciones.stream()
                    .filter(t -> {
                        boolean esOrigen = t.getOrigen() != null &&
                                t.getOrigen().getPropietario() != null &&
                                t.getOrigen().getPropietario().getId().equals(cliente.getId());

                        boolean esDestino = t.getDestino() != null &&
                                t.getDestino().getPropietario() != null &&
                                t.getDestino().getPropietario().getId().equals(cliente.getId());

                        return esOrigen || esDestino;
                    })
                    .sorted((t1, t2) -> t2.getFecha().compareTo(t1.getFecha())) // Más recientes primero
                    .limit(20) // Limitamos a 20 transacciones recientes
                    .collect(Collectors.toList());

            // Convertir a DTOs
            for (Transaccion transaccion : transaccionesCliente) {
                resultado.add(TransaccionDTO.fromEntity(transaccion));
            }

        } catch (Exception e) {
            System.err.println("Error al obtener transacciones: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Obtiene todas las transacciones del cliente para filtrado por monedero
     */
    private List<TransaccionDTO> obtenerTodasLasTransacciones(Cliente cliente) {
        List<TransaccionDTO> resultado = new ArrayList<>();

        // Obtener el historial completo de transacciones del cliente
        DoubleList<Transaccion> historial = cliente.getHistorialTransacciones();

        if (historial != null && !historial.isEmpty()) {
            // Recorrer la lista doblemente enlazada desde el final (más recientes primero)
            DoubleNode<Transaccion> nodoActual = historial.getLastNode();

            while (nodoActual != null) {
                TransaccionDTO dto = TransaccionDTO.fromEntity(nodoActual.getValue());
                resultado.add(dto);
                nodoActual = nodoActual.getPreviousNodo();
            }
        }

        // Si no hay suficientes transacciones en el historial del cliente,
        // también buscar en los historiales de los monederos
        SimpleList<Monedero> monederos = cliente.getMonederos();
        if (monederos != null && !monederos.isEmpty()) {
            Node<Monedero> nodoMonedero = monederos.getFirstNode();

            while (nodoMonedero != null) {
                Monedero monedero = nodoMonedero.getValue();

                if (monedero.getHistorialTransacciones() != null && !monedero.getHistorialTransacciones().isEmpty()) {
                    DoubleNode<Transaccion> nodoTransaccion = monedero.getHistorialTransacciones().getLastNode();

                    while (nodoTransaccion != null) {
                        TransaccionDTO dto = TransaccionDTO.fromEntity(nodoTransaccion.getValue());

                        // Evitar duplicados
                        boolean yaExiste = resultado.stream()
                                .anyMatch(t -> t.getId().equals(dto.getId()));

                        if (!yaExiste) {
                            resultado.add(dto);
                        }

                        nodoTransaccion = nodoTransaccion.getPreviousNodo();
                    }
                }

                nodoMonedero = nodoMonedero.getNextNodo();
            }
        }

        // Ordenar por fecha (más recientes primero) y limitar a las últimas 50
        return resultado.stream()
                .sorted((t1, t2) -> t2.getFecha().compareTo(t1.getFecha()))
                .limit(50)
                .collect(Collectors.toList());
    }

    @PostMapping("/wallets/new")
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

            System.out.println("=== DEBUG CREAR MONEDERO ===");
            System.out.println("Cliente: " + cliente.getNombre());
            System.out.println("Nombre monedero: " + nombre);
            System.out.println("Saldo inicial: " + saldoInicial);

            // Crear el nuevo monedero
            Monedero nuevoMonedero = new Monedero();
            nuevoMonedero.setId(UUID.randomUUID().toString());
            nuevoMonedero.setNombre(nombre);
            nuevoMonedero.setPropietario(cliente);
            nuevoMonedero.setTipoMonedero(tipoMonedero);
            nuevoMonedero.setSaldo(saldoInicial);
            nuevoMonedero.setNumeroCuenta(generarNumeroCuentaUnico());
            nuevoMonedero.setFechaCreacion(LocalDateTime.now());
            nuevoMonedero.setActivo(true);
            nuevoMonedero.setHistorialTransacciones(new DoubleList<>());

            // Guardar el monedero en la base de datos
            Monedero monederoGuardado = monederoRepository.save(nuevoMonedero);

            // CRÍTICO: Actualizar la lista enlazada del cliente ANTES de guardar el cliente
            SimpleList<Monedero> monederos = cliente.getMonederos();
            if (monederos == null) {
                monederos = new SimpleList<>();
            }

            // Añadir el nuevo monedero a la lista enlazada
            monederos.add(monederoGuardado);
            cliente.setMonederos(monederos);

            // Guardar el cliente con la lista actualizada ANTES de crear la transacción
            Cliente clienteActualizado = clienteRepository.save(cliente);

            // Crear transacción inicial si hay saldo inicial
            if (saldoInicial > 0) {
                Transaccion transaccionInicial = new Transaccion();
                transaccionInicial.setId(UUID.randomUUID().toString());
                transaccionInicial.setTipo(TipoTransaccion.DEPOSITO);
                transaccionInicial.setMonto(saldoInicial);
                transaccionInicial.setFecha(LocalDateTime.now());
                transaccionInicial.setDescripcion("Depósito inicial al crear monedero: " + nombre);
                transaccionInicial.setDestino(monederoGuardado);
                transaccionInicial.setActiva(true);

                // Guardar la transacción
                transaccionRepository.save(transaccionInicial);

                // Actualizar historial del cliente usando su lista enlazada
                DoubleList<Transaccion> historial = clienteActualizado.getHistorialTransacciones();
                if (historial == null) {
                    historial = new DoubleList<>();
                }
                historial.add(transaccionInicial);
                clienteActualizado.setHistorialTransacciones(historial);

                // Guardar cliente con historial actualizado
                clienteRepository.save(clienteActualizado);

                System.out.println("Transacción inicial creada: " + transaccionInicial.getId());
            }

            // FORZAR ACTUALIZACIÓN EN SESIÓN para que el dashboard se recargue
            session.setAttribute("clienteActualizado", true);

            redirectAttributes.addFlashAttribute("mostrarMensaje", true);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Monedero '" + nombre + "' creado exitosamente con saldo inicial de $" +
                            String.format("%,.2f", saldoInicial));

            System.out.println("Monedero creado y cliente actualizado correctamente");
            System.out.println("================================");
            return "redirect:/dashboard"; // CAMBIO: Redirigir al dashboard para ver el cambio inmediatamente

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mostrarError", true);
            redirectAttributes.addFlashAttribute("error", "Error al crear el monedero: " + e.getMessage());
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