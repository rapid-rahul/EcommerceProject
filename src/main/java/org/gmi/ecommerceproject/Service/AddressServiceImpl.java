package org.gmi.ecommerceproject.Service;

import org.gmi.ecommerceproject.Exception.ResourceNotFoundException;
import org.gmi.ecommerceproject.Model.Address;
import org.gmi.ecommerceproject.Model.AddressType;
import org.gmi.ecommerceproject.Model.User;
import org.gmi.ecommerceproject.Payload.AddressDTO;
import org.gmi.ecommerceproject.Repository.AddressRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public List<AddressDTO> getAddresses() {
        List<Address> addresses = addressRepository.findAll();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()-> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressDTO.class);

    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        address.setName(addressDTO.getName());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setAddressType(AddressType.valueOf(addressDTO.getAddressType()));
        address.setAlternatePhone(addressDTO.getAlternatePhone());
        address.setPincode(addressDTO.getPincode());
        address.setLandmark(addressDTO.getLandmark());
        address.setCountry(addressDTO.getCountry());
        address.setLocality(addressDTO.getLocality());
        address.setAreaAndStreet(addressDTO.getAreaAndStreet());
        address.setMobileNumber(addressDTO.getMobileNumber());
        Address updatedAddress = addressRepository.save(address);

        return modelMapper.map(updatedAddress, AddressDTO.class);

    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        addressRepository.delete(address);
        return "Address has been deleted Successfully of addressId "+addressId;

    }
}
