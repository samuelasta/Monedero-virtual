package co.uniquindio.edu.mi_moneda.repository;

import co.uniquindio.edu.mi_moneda.model.Beneficio;
import co.uniquindio.edu.mi_moneda.model.enums.TipoBeneficio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeneficioRepository extends MongoRepository<Beneficio, String> {

    /**
     * Busca beneficios existentes segun el tipo
     * @param tipo El tipo de beneficio a buscar
     * @return lista de beneficios
     */
    List<Beneficio> findByTipo(TipoBeneficio tipo);

    /**
     * Busca beneficios cuyo costo en puntos sea menor o igual a un valor específico
     * @param puntos La cantidad máxima de puntos
     * @return Lista de beneficios disponibles con el límite de puntos especificado
     */
    List<Beneficio> findByCostePuntosLessThanEqual(double puntos);

    /**
     * Busca beneficios por nombre (ignorando mayúsculas/minúsculas)
     * @param nombre El nombre o parte del nombre a buscar
     * @return Lista de beneficios que coinciden con el nombre
     */
    List<Beneficio> findByNombreContainingIgnoreCase(String nombre);
}
