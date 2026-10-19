/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.config;

/**
 *
 * @author fer_c
 */
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

// Configuració de seguretat del servidor
@Configuration
@EnableWebSecurity
public class SeguretatConfig {

    // Clau secreta per signar els tokens (es llegeix d'application.properties)
    @Value("${padelgest.jwt.secret}")
    private String secretJwt;

    // Xifrador de contrasenyes amb PBKDF2 (HMAC-SHA256 i salt aleatori)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    // Crea i signa els tokens JWT
    @Bean
    public JwtEncoder jwtEncoder() {
        return NimbusJwtEncoder.withSecretKey(clauJwt()).build();
    }

    // Comprova la signatura i la caducitat dels tokens que arriben
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(clauJwt())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    // Regles d'accés als endpoints
    @Bean
    public SecurityFilterChain filtreSeguretat(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessio -> sessio.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(peticions -> peticions
                        // Obert a tothom: registre i login
                        .requestMatchers("/api/auth/registre", "/api/auth/login").permitAll()
                        // Només ADMIN: gestió d'usuaris
                        .requestMatchers("/api/usuaris/**").hasRole("ADMIN")
                        // Només ADMIN: crear, modificar i desactivar pistes
                        .requestMatchers(HttpMethod.POST, "/api/pistes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pistes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pistes/**").hasRole("ADMIN")
                        // Només ADMIN: veure les reserves de tothom
                        .requestMatchers(HttpMethod.GET, "/api/reserves", "/api/reserves/usuari/**",
                                "/api/reserves/pista/**").hasRole("ADMIN")
                        // La resta: qualsevol usuari amb un token vàlid
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(convertidorRols())));
        return http.build();
    }

    // Llegeix el claim "rol" del token i el converteix en ROLE_ADMIN o ROLE_USER
    private JwtAuthenticationConverter convertidorRols() {
        JwtGrantedAuthoritiesConverter rols = new JwtGrantedAuthoritiesConverter();
        rols.setAuthoritiesClaimName("rol");
        rols.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(rols);
        return convertidor;
    }

    // Converteix el text secret en una clau HMAC-SHA256
    private SecretKey clauJwt() {
        return new SecretKeySpec(secretJwt.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}