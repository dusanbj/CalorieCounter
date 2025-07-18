package rs.fon.demo.dto.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import rs.fon.demo.model.Role;

@Setter
@Getter
public class UserResponse {
    public String username;
    public Role role;

    public UserResponse(String username, Role role) {
        this.username = username;
        this.role = role;
    }
}
