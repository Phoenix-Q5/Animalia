package com.animal.java.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter(
            @Value("${animalia.security.keycloak.client-id:animalia-dev}") String clientId) {
        return new KeycloakJwtAuthenticationConverter(clientId);
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http,
                                         KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.POST, "/api/v1/animals/addAnimal").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/v1/animals/addCompleteAnimal").hasRole("ADMIN")
                    .requestMatchers("/api/v1/animals/admin/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/user").authenticated()
                    .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter))
            );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
