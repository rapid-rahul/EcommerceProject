package org.gmi.ecommerceproject.Security.Services;

import jakarta.transaction.Transactional;
import org.gmi.ecommerceproject.Model.User;
import org.gmi.ecommerceproject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(()->new UsernameNotFoundException("User Not found  with username"+username));
        return UserDetailsImpl.build(user);
    }
}
