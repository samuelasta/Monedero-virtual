package co.uniquindio.edu.mi_moneda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MongoDBConnectionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testMongoDBConnection() {
        // Si la conexión funciona, el MongoTemplate será inyectado correctamente
        assertNotNull(mongoTemplate);

        // Imprimir el nombre de la base de datos para verificar
        String dbName = mongoTemplate.getDb().getName();
        System.out.println("Conexión establecida con la base de datos: " + dbName);

        // Listar las colecciones disponibles
        System.out.println("Colecciones disponibles:");
        mongoTemplate.getCollectionNames().forEach(System.out::println);
    }
}
