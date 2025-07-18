package rs.fon.demo.dto.requests;

import lombok.Data;
import rs.fon.demo.model.Role;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role; // ROLE_USER ili ROLE_ADMIN
}