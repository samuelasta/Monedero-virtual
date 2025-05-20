package co.uniquindio.edu.mi_moneda.config;

import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import co.uniquindio.edu.mi_moneda.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        // Agregar convertidores existentes
        converters.add(new SimpleListToListConverter());
        converters.add(new ListToSimpleListConverter());
        converters.add(new DoubleListToListConverter());
        converters.add(new ListToDoubleListConverter());
        converters.add(new QueueToListConverter());
        converters.add(new ListToQueueConverter());

        // Agregar convertidores para manejar referencias circulares
        converters.add(new ClienteReferenceConverter());
        converters.add(new MonederoReferenceConverter());
        converters.add(new PuntosReferenceConverter());

        return new MongoCustomConversions(converters);
    }

    /**
     * Configuración personalizada del MappingMongoConverter para evitar
     * referencias circulares y mejorar el manejo de relaciones
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory databaseFactory,
            MongoMappingContext mappingContext,
            BeanFactory beanFactory) {

        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(databaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mappingContext);

        // Desactivar _class para reducir tamaño de documentos
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        // Registrar conversores personalizados
        converter.setCustomConversions(customConversions());

        return converter;
    }

    /**
     * Listener para verificar entidades antes de convertirlas
     * Ayuda a detectar problemas de referencia circular temprano
     */
    @Bean
    public ApplicationListener<BeforeConvertEvent<?>> beforeConvertListener() {
        return event -> {
            Object source = event.getSource();

            // Verificar que referencias críticas no sean circulares
            if (source instanceof Cliente) {
                Cliente cliente = (Cliente) source;
                // Si es necesario, quitar temporalmente referencias para evitar ciclos
            } else if (source instanceof Monedero) {
                Monedero monedero = (Monedero) source;
                // Evitar que el propietario contenga una referencia al monedero
                if (monedero.getPropietario() != null) {
                    Cliente propietario = monedero.getPropietario();
                    // Limpiar temporalmente la referencia si es necesario
                }
            }
        };
    }

    // Convertidor para manejar referencias de Cliente
    private static class ClienteReferenceConverter implements Converter<Cliente, Cliente> {
        @Override
        public Cliente convert(Cliente source) {
            if (source == null) return null;

            // Crear versión "ligera" del cliente para evitar referencias circulares
            Cliente clienteRef = new Cliente();
            clienteRef.setId(source.getId());
            clienteRef.setNombre(source.getNombre());
            clienteRef.setEmail(source.getEmail());
            clienteRef.setRango(source.getRango());
            // No incluir monederos, transacciones, etc. para evitar ciclos

            return clienteRef;
        }
    }

    // Convertidor para manejar referencias de Monedero
    private static class MonederoReferenceConverter implements Converter<Monedero, Monedero> {
        @Override
        public Monedero convert(Monedero source) {
            if (source == null) return null;

            // Crear versión "ligera" del monedero
            Monedero monederoRef = new Monedero();
            monederoRef.setId(source.getId());
            monederoRef.setNombre(source.getNombre());
            monederoRef.setTipoMonedero(source.getTipoMonedero());
            monederoRef.setSaldo(source.getSaldo());
            monederoRef.setNumeroCuenta(source.getNumeroCuenta());
            monederoRef.setFechaCreacion(source.getFechaCreacion());
            monederoRef.setActivo(source.isActivo());
            // No incluir propietario ni historial para evitar ciclos

            return monederoRef;
        }
    }

    // Convertidor para manejar referencias de Puntos
    private static class PuntosReferenceConverter implements Converter<Puntos, Puntos> {
        @Override
        public Puntos convert(Puntos source) {
            if (source == null) return null;

            // Crear versión "ligera" de puntos
            Puntos puntosRef = new Puntos();
            puntosRef.setId(source.getId());
            puntosRef.setPuntosAcumulados(source.getPuntosAcumulados());
            // No incluir cliente ni historial para evitar ciclos

            return puntosRef;
        }
    }

    // Convertidor de SimpleList a List (para guardar en MongoDB)
    private static class SimpleListToListConverter<T extends identificable> implements Converter<SimpleList<T>, List<T>> {
        @Override
        public List<T> convert(SimpleList<T> source) {
            List<T> result = new ArrayList<>();

            if (source != null && !source.isEmpty()) {
                // Recorrer la lista enlazada y añadir cada elemento a ArrayList
                co.uniquindio.edu.mi_moneda.listasPropias.Node<T> current = source.getFirstNode();
                while (current != null) {
                    result.add(current.getValue());
                    current = current.getNextNodo();
                }
            }

            return result;
        }
    }

    // Convertidor de List a SimpleList (para cargar desde MongoDB)
    private static class ListToSimpleListConverter<T extends identificable> implements Converter<List<T>, SimpleList<T>> {
        @Override
        public SimpleList<T> convert(List<T> source) {
            SimpleList<T> result = new SimpleList<>();

            if (source != null) {
                for (T item : source) {
                    if (item instanceof co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable) {
                        result.add(item);
                    }
                }
            }

            return result;
        }
    }

    // Convertidor de DoubleList a List (para guardar en MongoDB)
    private static class DoubleListToListConverter<T extends identificable> implements Converter<DoubleList<T>, List<T>> {
        @Override
        public List<T> convert(DoubleList<T> source) {
            List<T> result = new ArrayList<>();

            if (source != null && !source.isEmpty()) {
                // Recorrer la lista doblemente enlazada
                co.uniquindio.edu.mi_moneda.listasPropias.DoubleNode<T> current = source.getFirstNode();
                while (current != null) {
                    result.add(current.getValue());
                    current = current.getNextNodo();
                }
            }

            return result;
        }
    }

    // Convertidor de List a DoubleList (para cargar desde MongoDB)
    private static class ListToDoubleListConverter<T extends identificable> implements Converter<List<T>, DoubleList<T>> {
        @Override
        public DoubleList<T> convert(List<T> source) {
            DoubleList<T> result = new DoubleList<>();

            if (source != null) {
                for (T item : source) {
                    if (item instanceof co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable) {
                        result.add(item);
                    }
                }
            }

            return result;
        }
    }

    // Convertidor de Queue a List (para guardar en MongoDB)
    private static class QueueToListConverter implements Converter<QueueTransactionProgramed, List<TransaccionProgramada>> {
        @Override
        public List<TransaccionProgramada> convert(QueueTransactionProgramed source) {
            List<TransaccionProgramada> result = new ArrayList<>();

            if (source != null && !source.isEmpty()) {
                // Implementar lógica para recorrer la cola
                co.uniquindio.edu.mi_moneda.listasPropias.NodeQueue current = source.getFirstNode();
                while (current != null) {
                    result.add(current.getTransaccion());
                    current = current.getNextNode();
                }
            }

            return result;
        }
    }

    // Convertidor de List a Queue (para cargar desde MongoDB)
    private static class ListToQueueConverter implements Converter<List<TransaccionProgramada>, QueueTransactionProgramed> {
        @Override
        public QueueTransactionProgramed convert(List<TransaccionProgramada> source) {
            QueueTransactionProgramed result = new QueueTransactionProgramed();

            if (source != null) {
                for (TransaccionProgramada tp : source) {
                    result.enqueue(tp);
                }
            }

            return result;
        }
    }
}