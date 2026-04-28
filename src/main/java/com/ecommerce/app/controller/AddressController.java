package com.ecommerce.app.controller;

import com.ecommerce.app.modal.User;
import com.ecommerce.app.payload.AddressDTO;
import com.ecommerce.app.service.AddressService;
import com.ecommerce.app.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class AddressController {

    @Autowired
    AddressService addressService;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/address")
    public ResponseEntity<AddressDTO> addAddress(@Valid @RequestBody AddressDTO addressDTO) {

        User user = authUtil.loggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO,HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddress() {

        List<AddressDTO> addressDTOList = addressService.getAllAddress();
        return new ResponseEntity<>(addressDTOList,HttpStatus.CREATED);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {

        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO,HttpStatus.CREATED);
    }

    @GetMapping("/user/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddress() {

        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOList = addressService.getUserAddress(user);
        return new ResponseEntity<>(addressDTOList,HttpStatus.CREATED);
    }


    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Long addressId) {
        AddressDTO savedAddressDTO = addressService.updateAddressByAddressId(addressDTO, addressId);
        return new ResponseEntity<>(savedAddressDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("/address/{addressId}")
    public String deleteAddress(@PathVariable Long addressId) {

        return addressService.deleteAddress(addressId);

    }

}
