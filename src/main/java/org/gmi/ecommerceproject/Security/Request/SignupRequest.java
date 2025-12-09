package org.gmi.ecommerceproject.Security.Request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    @NotBlank
    @Email
    @Size(min = 3, max = 50)
    private String email;
    private Set<String> role;
    private String password;

}
