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
import org.springframework.security.config.Customizer;
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
                        .requestMatchers("/api/auth/jo").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()));
        return http.build();
    }

    // Converteix el text secret en una clau HMAC-SHA256
    private SecretKey clauJwt() {
        return new SecretKeySpec(secretJwt.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}