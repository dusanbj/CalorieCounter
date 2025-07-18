package rs.fon.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rs.fon.demo.dto.responses.UserResponse;
import rs.fon.demo.model.RefreshToken;
import rs.fon.demo.model.Role;
import rs.fon.demo.model.User;
import rs.fon.demo.repositories.UserRepository;
import rs.fon.demo.dto.requests.LoginRequest;
import rs.fon.demo.dto.requests.RegisterRequest;
import rs.fon.demo.services.RefreshTokenService;
import rs.fon.demo.services.TokenBlacklistService;
import rs.fon.demo.services.UserService;
import rs.fon.demo.utils.JwtUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil,
                          RefreshTokenService refreshTokenService,
                          UserRepository userRepository,
                          TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already taken");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.ROLE_USER;

        userService.registerUser(
                request.getUsername(),
                request.getPassword(),
                role
        );
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails);

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(loginRequest.getUsername()));
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        Map<String, Object> refreshTokenData = refreshTokenService.createRefreshToken(userOptional.get());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshTokenData.get("token"));
        response.put("refreshTokenExpiry", refreshTokenData.get("expiryDate"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String rawToken = body.get("refreshToken");

        Optional<RefreshToken> tokenOptional = refreshTokenService.findMatchingToken(rawToken);
        if (!tokenOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        RefreshToken token = tokenOptional.get();
        if (refreshTokenService.isExpired(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        }

        String newAccessToken = jwtUtil.generateToken(token.getUser());
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        long expirationMillis = jwtUtil.extractExpiration(token).getTime() - System.currentTimeMillis();
        tokenBlacklistService.blacklistToken(token, expirationMillis);

        refreshTokenService.deleteByUser(userOptional.get());

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/readUsers")
    public ResponseEntity<List<UserResponse>> readUsers() {
        List<UserResponse> users = userService.readAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}