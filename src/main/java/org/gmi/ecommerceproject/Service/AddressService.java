package org.gmi.ecommerceproject.Service;

import jakarta.validation.Valid;
import org.gmi.ecommerceproject.Model.User;
import org.gmi.ecommerceproject.Payload.AddressDTO;

import java.util.List;

public interface AddressService {
    List<AddressDTO> getAddresses();

    AddressDTO createAddress(AddressDTO addressDTO, User user);

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddress(Long addressId, @Valid AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
