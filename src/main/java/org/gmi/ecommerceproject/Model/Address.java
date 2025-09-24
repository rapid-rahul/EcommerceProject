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
@Table(name = "addresses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 2, max = 50,message = "building name must be  at least 2 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 2, max = 50,message = "street name must be at least 2 characters")
    private String street;

    @NotBlank
    @Size(min = 2, max = 50,message = "city name must be at least 2 characters")
    private String city;
    @NotBlank
    @Size(min = 2, max = 50,message = "State name must be at least 2 characters")
    private String state;
    @NotBlank
    @Size(min = 6, max = 50,message = "PinCode Must be at least 6 Characters")
    private String zip;

    @NotBlank
    @Size(min= 6,message = "country name must be at least 6 characters")
    private String country;

    @ToString.Exclude
    @ManyToMany(mappedBy = "addresses")
    private List<User> users = new ArrayList<>();

    public Address(String buildingName, String street, String state, String city, String zip, String country) {
        this.buildingName = buildingName;
        this.street = street;
        this.state = state;
        this.city = city;
        this.zip = zip;
        this.country = country;
    }
}
