package ma.lahjaily.analyticsservice.handlers;

import ma.lahjaily.analyticsservice.entities.PageEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Handler for Kafka page events with Consumer, Supplier, and KStream patterns
 * 
 * Ce composant implémente l'API fonctionnelle de Spring Cloud Stream :
 * - Consumer : Consomme les événements du topic visite-topic
 * - Supplier : Génère des événements toutes les 200ms vers visite-topic  
 * - Function (KStream) : Traite et agrège les événements vers facture-topic
 * 
 * @author LAHJAILY
 * @version 1.0
 */
@Component
public class PageEventHandler {

    /**
     * Consumer : Consomme et affiche les événements de visite
     * 
     * Binding: pageEventConsumer-in-0 → visite-topic
     * Logs chaque PageEvent reçu dans la console
     * 
     * @return Consumer fonctionnel pour PageEvent
     */
    @Bean
    public Consumer<PageEvent> pageEventConsumer() {
        return (input) -> {
            System.out.println("*************************************");
            System.out.println("📥 [CONSUMER] Received PageEvent:");
            System.out.println("   → Page: " + input.name());
            System.out.println("   → User: " + input.user());
            System.out.println("   → Date: " + input.date());
            System.out.println("   → Duration: " + input.duration() + "ms");
            System.out.println("*************************************");
        };
    }

    /**
     * Supplier : Génère des événements de visite automatiquement
     * 
     * Binding: pageEventSupplier-out-0 → visite-topic
     * Génère un PageEvent toutes les 200ms (configurable)
     * Simule des visites sur différentes pages par différents utilisateurs
     * 
     * @return Supplier fonctionnel pour PageEvent
     */
    @Bean
    public Supplier<PageEvent> pageEventSupplier() {
        return () -> new PageEvent(
                Math.random() > 0.5 ? "P1" : "P2",  // Pages: P1 ou P2
                Math.random() > 0.5 ? "U1" : "U2",  // Users: U1 ou U2
                new Date(),
                10 + new Random().nextInt(10000)    // Duration: 10-10010ms
        );
    }

    /**
     * KStream : Traitement en temps réel avec agrégation fenêtrée
     * 
     * Pipeline de traitement:
     * 1. Filter: Garde uniquement les visites avec duration > 100ms
     * 2. Map: Extrait (pageName, duration) de chaque événement
     * 3. GroupByKey: Groupe par nom de page
     * 4. WindowedBy: Fenêtre temporelle glissante de 5 minutes
     * 5. Count: Compte le nombre de visites par fenêtre
     * 6. Materialize: Stocke dans "count-store" (Interactive Queries)
     * 
     * Binding: kStream-in-0 (visite-topic) → kStream-out-0 (facture-topic)
     * 
     * @return Function KStream pour le traitement de flux
     */
    @Bean
    public Function<KStream<String, PageEvent>, KStream<String, Long>> kStream() {
        return (input) -> input
                .filter((key, value) -> value.duration() > 100)
                .map((key, value) -> new KeyValue<>(value.name(), value.duration()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .count(Materialized.as("count-store"))
                .toStream()
                .map((windowedKey, count) -> new KeyValue<>(windowedKey.key(), count));
    }
}
