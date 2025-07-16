package rs.fon.demo.filters;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import rs.fon.demo.services.TokenBlacklistService;
import rs.fon.demo.services.UserService;
import rs.fon.demo.utils.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//@Component
public class JwtFilter extends org.springframework.web.filter.OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Autowired
    public JwtFilter(UserService userService, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                // Proverava da li je token blacklistovan
                if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                Claims claims = jwtUtil.extractAllClaims(jwt);

                List<String> roles = claims.get("authorities", List.class);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
