package org.gmi.ecommerceproject.Payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {

    private Long addressId;
    private String name;
    private String mobileNumber;
    private String alternatePhone;
    private String pincode;
    private String locality;
    private String areaAndStreet;
    private String city;
    private String state;
    private String landmark;
    private String addressType;
    private String country;
}
