package org.gmi.ecommerceproject.Repository;

import org.gmi.ecommerceproject.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
