package ma.lahjaily.billingservice.entities;

import java.util.Date;

/**
 * Record représentant un événement de création de facture pour l'analytics.
 * Envoyé au topic Kafka "facture-topic" lors de la création d'une facture.
 * 
 * @param name     Nom de l'action (ex: "bill-created")
 * @param user     Identifiant du client
 * @param date     Timestamp de l'événement
 * @param duration Durée de traitement en millisecondes
 */
public record PageEvent(
        String name,
        String user,
        Date date,
        long duration
) {
}
