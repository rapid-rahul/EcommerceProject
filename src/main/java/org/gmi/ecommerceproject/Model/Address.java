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
    @Size(min = 2, max = 50,message = "Name must be at least 2 characters")
    private String name;

    @Column(length = 10, nullable = false)
    private String mobileNumber;
    @Column(length = 10, nullable = false)
    private String alternatePhone;

    @Column(length = 6, nullable = false)
    private String pincode;
    @Column(length = 10, nullable = false)
    private String locality;

    @NotBlank
    @Size(min = 2, max = 50,message = " area or street name must be at least 2 characters")
    private String areaAndStreet;
    @NotBlank
    @Size(min = 2, max = 50,message = "city name must be at least 2 characters")
    private String city;
    @NotBlank
    @Size(min = 2, max = 50,message = "State name must be at least 2 characters")
    private String state;
    private String landmark;
    @NotBlank
    @Size(min= 2,message = "country name must be at least 6 characters")
    private String country;

    @Enumerated(EnumType.STRING)
    private AddressType addressType;  // HOME / WORK

    // Many addresses can belong to one User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Address(String name, String mobileNumber, String alternatePhone, String pincode, String locality, String areaAndStreet, String city, String state, String landmark, String country, AddressType addressType) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.alternatePhone = alternatePhone;
        this.pincode = pincode;
        this.locality = locality;
        this.areaAndStreet = areaAndStreet;
        this.city = city;
        this.state = state;
        this.landmark = landmark;
        this.country = country;
        this.addressType = addressType;
    }

}
