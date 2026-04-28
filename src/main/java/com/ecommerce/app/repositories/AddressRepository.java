package com.ecommerce.app.repositories;

import com.ecommerce.app.modal.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository  extends JpaRepository<Address, Long> {

}
