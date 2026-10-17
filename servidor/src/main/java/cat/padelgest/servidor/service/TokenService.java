/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.service;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.model.Usuari;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

// Servei que genera els tokens JWT
@Service
public class TokenService {

    // Dependències
    private final JwtEncoder jwtEncoder;
    private final long minutsValidesa;

    // Constructor
    public TokenService(JwtEncoder jwtEncoder, @Value("${padelgest.jwt.minuts}") long minutsValidesa) {
        this.jwtEncoder = jwtEncoder;
        this.minutsValidesa = minutsValidesa;
    }

    // Genera un token signat amb l'email, l'id i el rol de l'usuari
    public String generarToken(Usuari usuari) {
        Instant ara = Instant.now();
        JwtClaimsSet dades = JwtClaimsSet.builder()
                .issuer("padelgest")
                .subject(usuari.getEmail())
                .issuedAt(ara)
                .expiresAt(ara.plus(minutsValidesa, ChronoUnit.MINUTES))
                .claim("id", usuari.getId())
                .claim("rol", usuari.getRol())
                .build();
        JwsHeader capcalera = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(capcalera, dades)).getTokenValue();
    }
}
