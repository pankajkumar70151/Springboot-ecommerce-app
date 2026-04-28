package com.ecommerce.app.service;

import com.ecommerce.app.exceptions.ResourceNotFoundException;
import com.ecommerce.app.modal.Address;
import com.ecommerce.app.modal.User;
import com.ecommerce.app.payload.AddressDTO;
import com.ecommerce.app.repositories.AddressRepository;
import com.ecommerce.app.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AuthUtil authUtil;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {

        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);

    }

    @Override
    public List<AddressDTO> getAllAddress() {

        List<Address> addressList = addressRepository.findAll();
        return addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {

        Address address = addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException("Address", "addressId", addressId));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddress(User user) {

        List<Address> addressList = user.getAddresses();

        return addressList.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

    }

    @Override
    public AddressDTO updateAddressByAddressId(AddressDTO addressDTO, Long addressId) {

        Address address = addressRepository.findById(addressId).orElseThrow(()->
                new ResourceNotFoundException("Address", "addressId", addressId));

        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        address.setStreet(addressDTO.getStreet());
        address.setZipCode(addressDTO.getZipCode());
        address.setBuildingName(addressDTO.getBuildingName());

        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {

        Address savedAddress = addressRepository.findById(addressId).orElseThrow(()->
                new ResourceNotFoundException("Address", "addressId", addressId));

        addressRepository.delete(savedAddress);
        return "Address deleted successfully";
    }
}
