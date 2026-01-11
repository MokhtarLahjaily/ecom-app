package ma.lahjaily.inventoryservice.entities;

import java.util.Date;

/**
 * Record représentant un événement de visite de page pour l'analytics.
 * Envoyé au topic Kafka "visite-topic" lors de la consultation d'un produit.
 * 
 * @param name     Nom de la page/action (ex: "product-view")
 * @param user     Identifiant de l'utilisateur
 * @param date     Timestamp de l'événement
 * @param duration Durée de la visite en millisecondes
 */
public record PageEvent(
        String name,
        String user,
        Date date,
        long duration
) {
}
