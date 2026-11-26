package cl.lowcost.ufs.Config;

import cl.lowcost.ufs.Models.User;
import cl.lowcost.ufs.Repos.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, UserRepository users) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/figures", "/api/figures/*").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/figures").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/figures/*").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(adminAwareConverter(users))));
        return http.build();
    }

    // El rol ADMIN sale de la columna "admin" de la tabla users, nunca del frontend.
    private Converter<Jwt, AbstractAuthenticationToken> adminAwareConverter(UserRepository users) {
        return jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (jwt.getSubject() != null) {
                try {
                    UUID id = UUID.fromString(jwt.getSubject());
                    if (users.findById(id).map(User::isAdmin).orElse(false)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    }
                } catch (IllegalArgumentException ignored) {
                    // subject que no es UUID: queda sin roles
                }
            }
            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${supabase.jwt.jwks-url:}") String jwksUrl,
                          @Value("${supabase.jwt.secret:}") String secret) {
        if (!jwksUrl.isBlank()) {
            return NimbusJwtDecoder.withJwkSetUri(jwksUrl)
                    .jwsAlgorithms(algs -> {
                        algs.add(SignatureAlgorithm.ES256);
                        algs.add(SignatureAlgorithm.RS256);
                    })
                    .build();
        }
        if (!secret.isBlank()) {
            SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        }
        throw new IllegalStateException("Falta configurar SUPABASE_JWKS_URL o SUPABASE_JWT_SECRET");
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins:*}") List<String> origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
