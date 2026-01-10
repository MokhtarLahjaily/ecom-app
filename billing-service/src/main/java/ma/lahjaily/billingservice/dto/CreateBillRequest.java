package ma.lahjaily.billingservice.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO pour la création d'une nouvelle facture.
 * Contient la liste des produits commandés.
 */
@Data
public class CreateBillRequest {
    private List<ProductItemRequest> productItems;

    @Data
    public static class ProductItemRequest {
        private String productId;
        private int quantity;
        private double unitPrice;
    }
}
