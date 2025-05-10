package co.uniquindio.edu.mi_moneda.controller;

import co.uniquindio.edu.mi_moneda.model.Cliente;
import co.uniquindio.edu.mi_moneda.model.Puntos;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {

    @GetMapping("/dashboard-test")
    public String dashboardTest(Model model) {
        System.out.println("Accediendo a /dashboard-test");

        // Crear datos de prueba
        Cliente cliente = new Cliente();
        cliente.setId("test-id");
        cliente.setNombre("Usuario de Prueba");
        cliente.setEmail("test@example.com");
        cliente.setRango("BRONCE");

        model.addAttribute("cliente", cliente);
        model.addAttribute("balanceTotal", 1000.0);
        model.addAttribute("cantidadMonederos", 2);
        model.addAttribute("transaccionesRecientes", new ArrayList<>());
        model.addAttribute("transaccionesProgramadas", new ArrayList<>());
        model.addAttribute("notificacionesNoLeidas", new ArrayList<>());
        model.addAttribute("cantidadNotificaciones", 0);

        Puntos puntos = new Puntos();
        puntos.setPuntosAcumulados(500);
        model.addAttribute("puntos", puntos);

        Map<String, Object> rangosInfo = new HashMap<>();
        rangosInfo.put("rangoActual", "BRONCE");
        rangosInfo.put("siguienteRango", "PLATA");
        rangosInfo.put("puntosNecesarios", 500);
        rangosInfo.put("porcentajeAvance", 50.0);
        model.addAttribute("rangosInfo", rangosInfo);

        model.addAttribute("beneficiosDisponibles", new ArrayList<>());

        Map<String, Double> distribucionGastos = new HashMap<>();
        distribucionGastos.put("Alimentación", 30.0);
        distribucionGastos.put("Transporte", 20.0);
        distribucionGastos.put("Entretenimiento", 15.0);
        distribucionGastos.put("Servicios", 25.0);
        distribucionGastos.put("Otros", 10.0);
        model.addAttribute("distribucionGastos", distribucionGastos);

        System.out.println("Listo para renderizar dashboard");
        return "dashboard";
    }
}
