package ma.lahjaily.analyticsservice.controllers;

import ma.lahjaily.analyticsservice.entities.PageEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * REST Controller for Analytics API endpoints
 * 
 * Endpoints:
 * - GET /publish : Publier un événement sur un topic Kafka
 * - GET /analytics : Flux SSE des statistiques temps réel
 * - GET /api/analytics/stream : Alias pour le flux SSE
 * - GET /api/analytics/snapshot : Snapshot des statistiques
 * 
 * @author LAHJAILY
 * @version 1.0
 */
@RestController
public class AnalyticsController {

    private final StreamBridge streamBridge;

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    public AnalyticsController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * Publier un événement de visite sur un topic Kafka
     * 
     * Exemple d'utilisation:
     * - curl "http://localhost:8084/publish?name=P1&topic=visite-topic"
     * - curl "http://localhost:8084/publish?name=HomePage&topic=visite-topic"
     * 
     * @param name Nom de la page visitée
     * @param topic Topic Kafka destination
     * @return L'événement PageEvent créé
     */
    @GetMapping("/publish")
    public PageEvent publish(
            @RequestParam(name = "name", defaultValue = "P1") String name,
            @RequestParam(name = "topic", defaultValue = "visite-topic") String topic) {
        
        PageEvent pageEvent = new PageEvent(
                name,
                Math.random() > 0.5 ? "U1" : "U2",
                new Date(),
                new Random().nextInt(1000)
        );
        
        streamBridge.send(topic, pageEvent);
        System.out.println("📤 [PRODUCER] Published to " + topic + ": " + pageEvent);
        return pageEvent;
    }

    /**
     * Stream SSE des analytics en temps réel (endpoint principal)
     * 
     * Interroge le State Store "count-store" chaque seconde
     * et renvoie les comptages par page sur les 5 dernières minutes
     * 
     * Utilisation Frontend: EventSource("/analytics")
     * 
     * @return Flux SSE de Map<String, Long> (pageName → count)
     */
    @GetMapping(path = "/analytics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> analytics() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                    Map<String, Long> stringLongMap = new HashMap<>();
                    try {
                        ReadOnlyWindowStore<String, Long> windowStore = 
                                interactiveQueryService.getQueryableStore(
                                        "count-store", 
                                        QueryableStoreTypes.windowStore()
                                );
                        
                        Instant now = Instant.now();
                        Instant from = now.minusSeconds(300);  // 5 minutes
                        
                        KeyValueIterator<Windowed<String>, Long> fetchAll = 
                                windowStore.fetchAll(from, now);
                        
                        while (fetchAll.hasNext()) {
                            KeyValue<Windowed<String>, Long> next = fetchAll.next();
                            stringLongMap.put(next.key.key(), next.value);
                        }
                        fetchAll.close();
                    } catch (Exception e) {
                        // State store not yet initialized
                        System.err.println("⚠️ Store not ready: " + e.getMessage());
                    }
                    return stringLongMap;
                });
    }

    /**
     * Alias pour le stream analytics (API RESTful)
     */
    @GetMapping(path = "/api/analytics/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> streamAnalytics() {
        return analytics();
    }

    /**
     * Snapshot des statistiques actuelles
     * 
     * Retourne le comptage agrégé sur les 10 dernières minutes
     * 
     * @return Map de pageName → totalCount
     */
    @GetMapping("/api/analytics/snapshot")
    public Map<String, Long> getAnalyticsSnapshot() {
        Map<String, Long> pageCountMap = new HashMap<>();
        try {
            ReadOnlyWindowStore<String, Long> windowStore = 
                    interactiveQueryService.getQueryableStore(
                            "count-store", 
                            QueryableStoreTypes.windowStore()
                    );
            
            Instant now = Instant.now();
            Instant from = now.minusSeconds(600);  // 10 minutes
            
            KeyValueIterator<Windowed<String>, Long> iterator = 
                    windowStore.fetchAll(from, now);
            
            while (iterator.hasNext()) {
                KeyValue<Windowed<String>, Long> next = iterator.next();
                pageCountMap.merge(next.key.key(), next.value, Long::sum);
            }
            iterator.close();
        } catch (Exception e) {
            System.err.println("⚠️ Store not ready: " + e.getMessage());
        }
        return pageCountMap;
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/api/analytics/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "analytics-service");
        status.put("storeReady", isStoreReady() ? "true" : "false");
        return status;
    }
    
    private boolean isStoreReady() {
        try {
            interactiveQueryService.getQueryableStore("count-store", QueryableStoreTypes.windowStore());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
