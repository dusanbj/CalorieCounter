package rs.fon.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.fon.demo.filters.JwtFilter;
import rs.fon.demo.services.TokenBlacklistService;
import rs.fon.demo.services.UserService;
import rs.fon.demo.utils.JwtUtil;

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public SpringSecurityConfig(UserService userService,
                                JwtUtil jwtUtil,
                                TokenBlacklistService tokenBlacklistService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userService);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors()
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/auth/register", "/auth/login", "/auth/refresh").permitAll()
                .antMatchers("/food/createFoodItem").hasRole("ADMIN")
                .antMatchers("/food/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(userService, jwtUtil, tokenBlacklistService);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManager();
    }
}