package co.uniquindio.edu.mi_moneda.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DbTestController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/test-db")
    public Map<String, Object> testDb() {
        Map<String, Object> result = new HashMap<>();

        // Agregar información de la base de datos
        result.put("databaseName", mongoTemplate.getDb().getName());

        // Listar todas las colecciones
        List<String> collections = new ArrayList<>();
        mongoTemplate.getCollectionNames().forEach(collections::add);
        result.put("collections", collections);

        // Verificar usuarios en la colección Cliente/Clientes
        List<Map> clientes = new ArrayList<>();
        for (String collectionName : Arrays.asList("Cliente", "Clientes")) {
            if (collections.contains(collectionName)) {
                mongoTemplate.getCollection(collectionName).find().limit(10)
                        .forEach(doc -> clientes.add(doc));
            }
        }
        result.put("clientes", clientes);

        return result;
    }
}
