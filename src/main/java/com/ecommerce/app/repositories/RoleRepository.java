package com.ecommerce.app.repositories;

import com.ecommerce.app.modal.AppRole;
import com.ecommerce.app.modal.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(AppRole roleName);
}
