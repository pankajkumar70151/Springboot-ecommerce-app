package com.ecommerce.app.service;

import com.ecommerce.app.modal.User;
import com.ecommerce.app.payload.AddressDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddress();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddress(User user);

    AddressDTO updateAddressByAddressId(AddressDTO addressDTO, Long addressId);

    String deleteAddress(Long addressId);
}
