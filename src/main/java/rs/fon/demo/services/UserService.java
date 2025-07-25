package rs.fon.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.fon.demo.dto.responses.UserResponse;
import rs.fon.demo.model.Role;
import rs.fon.demo.model.User;
import rs.fon.demo.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = this.userRepository.findByUsername(username);
        if (myUser == null) {
            throw new UsernameNotFoundException("User name " + username + " not found");
        }

        var authorities = List.of(new SimpleGrantedAuthority(myUser.getRole().name()));

        return new org.springframework.security.core.userdetails.User(
                myUser.getUsername(),
                myUser.getPassword(),
                authorities
        );
    }

    public User registerUser(String username, String password, Role role) {
        if(userRepository.findByUsername(username) != null) {
            throw new UsernameNotFoundException("User name " + username + " already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    public List<UserResponse> readAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
    }
}