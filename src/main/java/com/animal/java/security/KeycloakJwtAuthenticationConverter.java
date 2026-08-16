package com.animal.java.security;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String ROLE_PREFIX = "ROLE_";

    private final String clientId;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        return new JwtAuthenticationToken(jwt, extractAuthorities(jwt));
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        addRoles(authorities, realmAccess != null ? realmAccess.get("roles") : null);

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.get(clientId) instanceof Map<?, ?> clientAccess) {
            addRoles(authorities, clientAccess.get("roles"));
        }
        return authorities;
    }

    private void addRoles(Set<GrantedAuthority> authorities, Object rolesClaim) {
        if (!(rolesClaim instanceof Collection<?> roles)) {
            return;
        }
        for (Object role : roles) {
            if (role == null) {
                continue;
            }
            String value = role.toString();
            if (value.isBlank()) {
                continue;
            }
            String normalized = value.startsWith(ROLE_PREFIX)
                    ? value.toUpperCase(Locale.ROOT)
                    : ROLE_PREFIX + value.toUpperCase(Locale.ROOT);
            authorities.add(new SimpleGrantedAuthority(normalized));
        }
    }
}
