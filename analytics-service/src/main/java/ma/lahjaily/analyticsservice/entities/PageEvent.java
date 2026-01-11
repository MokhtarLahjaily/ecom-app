package ma.lahjaily.analyticsservice.entities;

import java.util.Date;

/**
 * Record representing a page event for analytics tracking
 */
public record PageEvent(
        String name,      // Page name (e.g., "HomePage", "ProductPage")
        String user,      // User identifier
        Date date,        // Event timestamp
        long duration     // Duration in milliseconds
) {
}
