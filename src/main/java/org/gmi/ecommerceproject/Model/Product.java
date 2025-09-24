package org.gmi.ecommerceproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="products")
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @NotBlank
    @Size(min = 3, max = 100,message = "Product Name must Contain at least 3 Characters")
    private String productName;
    private String image;
    @NotBlank
    @Size(min = 6, max = 100,message = "Description must contain at least 6 characters")
    private String description;
    private Double price;
    private Integer quantity;
    private double discount;
    private double specialPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE},fetch = FetchType.EAGER,orphanRemoval = true)
    private List<CartItem> products = new ArrayList<>();

}
