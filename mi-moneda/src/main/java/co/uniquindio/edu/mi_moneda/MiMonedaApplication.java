package co.uniquindio.edu.mi_moneda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
public class MiMonedaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiMonedaApplication.class, args);
	}
}
