package org.gmi.ecommerceproject.Repository;

import org.gmi.ecommerceproject.Model.AppRole;
import org.gmi.ecommerceproject.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
