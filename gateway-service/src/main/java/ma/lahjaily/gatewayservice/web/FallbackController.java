package ma.lahjaily.gatewayservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fallback controller for Circuit Breaker.
 * Provides graceful degradation responses when downstream services are unavailable.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    /**
     * Fallback for inventory-service.
     * Returns an empty Spring Data REST compatible response.
     */
    @GetMapping(value = "/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> inventoryFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put("products", List.of());
        response.put("_embedded", embedded);
        response.put("_fallback", true);
        response.put("message", "Inventory service is temporarily unavailable. Please try again later.");
        response.put("timestamp", Instant.now().toString());
        
        return Mono.just(response);
    }

    /**
     * Fallback for customer-service.
     * Returns an empty Spring Data REST compatible response.
     */
    @GetMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> customersFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put("customers", List.of());
        response.put("_embedded", embedded);
        response.put("_fallback", true);
        response.put("message", "Customer service is temporarily unavailable. Please try again later.");
        response.put("timestamp", Instant.now().toString());
        
        return Mono.just(response);
    }

    /**
     * Fallback for billing-service.
     * Returns an empty response for bills.
     */
    @GetMapping(value = "/billing", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> billingFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> embedded = new HashMap<>();
        embedded.put("bills", List.of());
        response.put("_embedded", embedded);
        response.put("_fallback", true);
        response.put("message", "Billing service is temporarily unavailable. Please try again later.");
        response.put("timestamp", Instant.now().toString());
        
        return Mono.just(response);
    }

    /**
     * Generic fallback for any unhandled service.
     */
    @GetMapping(value = "/generic", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> genericFallback(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        
        Map<String, Object> response = new HashMap<>();
        response.put("_fallback", true);
        response.put("message", "Service is temporarily unavailable. Please try again later.");
        response.put("timestamp", Instant.now().toString());
        
        return Mono.just(response);
    }
}
