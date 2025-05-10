package co.uniquindio.edu.mi_moneda.config;

import co.uniquindio.edu.mi_moneda.listasPropias.SimpleList;
import co.uniquindio.edu.mi_moneda.listasPropias.DoubleList;
import co.uniquindio.edu.mi_moneda.listasPropias.QueueTransactionProgramed;
import co.uniquindio.edu.mi_moneda.listasPropias.identificable.identificable;
import co.uniquindio.edu.mi_moneda.model.Monedero;
import co.uniquindio.edu.mi_moneda.model.Notificacion;
import co.uniquindio.edu.mi_moneda.model.Transaccion;
import co.uniquindio.edu.mi_moneda.model.TransaccionProgramada;
import co.uniquindio.edu.mi_moneda.model.TransaccionPuntos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        // Agregar convertidores
        converters.add(new SimpleListToListConverter());
        converters.add(new ListToSimpleListConverter());
        converters.add(new DoubleListToListConverter());
        converters.add(new ListToDoubleListConverter());
        converters.add(new QueueToListConverter());
        converters.add(new ListToQueueConverter());

        return new MongoCustomConversions(converters);
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