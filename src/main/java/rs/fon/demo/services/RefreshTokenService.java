package rs.fon.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.demo.model.RefreshToken;
import rs.fon.demo.model.User;
import rs.fon.demo.repositories.RefreshTokenRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh.expiration.ms}")
    private Long refreshTokenDurationMs;

    public Map<String, Object> createRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = passwordEncoder.encode(rawToken);

        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(hashedToken)
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        refreshTokenRepository.save(token);

        Map<String, Object> response = new HashMap<>();
        response.put("token", rawToken); // šalješ klijentu originalni token
        response.put("expiryDate", token.getExpiryDate());

        return response;
    }

    public Optional<RefreshToken> findMatchingToken(String rawToken) {
        return refreshTokenRepository.findAll().stream()
                .filter(storedToken -> passwordEncoder.matches(rawToken, storedToken.getToken()))
                .findFirst();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
