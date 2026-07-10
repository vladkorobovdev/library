package github.vladkorobovdev.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        JwtAuthFilter jwtAuthFilter,
                        AuthenticationProvider authProvider) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/auth/**").permitAll()

                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")

                                                .requestMatchers(HttpMethod.GET, "/api/orders").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/api/orders/*/status")
                                                .hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/orders/*")
                                                .hasRole("ADMIN")

                                                .requestMatchers("/api/payments/webhook", "/api/payments/success",
                                                                "/api/payments/cancel")
                                                .permitAll()
                                                .requestMatchers("/error").permitAll()

                                                .requestMatchers(
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
