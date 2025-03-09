package com.animal.java.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.POST, "/api/v1/animals/addAnimal").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/v1/animals/addCompleteAnimal").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/user").authenticated()
                    .anyRequest().permitAll()
            )
            .oauth2Login(withDefaults());
        return http.build();
    }
}
