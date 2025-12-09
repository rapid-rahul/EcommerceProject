package org.gmi.ecommerceproject.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductDTO {
    private Long productId;
    private String productName;
    private String image;
    private String description;
    private Double price;
    private Integer quantity;
    private double discount;
    private double specialPrice;
}
