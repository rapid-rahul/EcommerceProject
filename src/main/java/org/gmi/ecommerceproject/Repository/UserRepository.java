package org.gmi.ecommerceproject.Repository;

import org.gmi.ecommerceproject.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);

    boolean existsByEmail(String email);

    boolean existsByUserName(String username);
}
