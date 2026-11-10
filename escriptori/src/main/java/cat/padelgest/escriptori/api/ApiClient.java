/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori.api;

/**
 *
 * @author fer_c
 */
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

// Client de l'API REST: totes les peticions al servidor passen per aquí
public class ApiClient {

    // Atributs 
    // Adreça del servidor
    private static final String URL_BASE = "https://padelgest.onrender.com/api";

    // Objecte de Java que envia les peticions HTTP
    private static final HttpClient client = HttpClient.newHttpClient();

    // Jackson: passa d'objectes Java a JSON i al revés
    private static final JsonMapper json = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    // Token que ens dona el servidor en fer login (null si no hi ha sessió)
    private static String token;

    // Getters i setters 
    public static void setToken(String nouToken) {
        token = nouToken;
    }

    // Peticions 
    // Fa una petició GET i converteix la resposta JSON en un objecte.
    public static <T> T get(String ruta, Class<T> tipus) {
        HttpRequest.Builder peticio = HttpRequest.newBuilder(URI.create(URL_BASE + ruta))
                .GET();
        return enviar(peticio, tipus);
    }

    // Fa una petició POST enviant un objecte com a JSON.
    public static <T> T post(String ruta, Object cos, Class<T> tipus) {
        HttpRequest.Builder peticio = HttpRequest.newBuilder(URI.create(URL_BASE + ruta))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(cos)));
        return enviar(peticio, tipus);
    }

    // Fa una petició PUT (modificar) enviant un objecte com a JSON.
    public static <T> T put(String ruta, Object cos, Class<T> tipus) {
        HttpRequest.Builder peticio = HttpRequest.newBuilder(URI.create(URL_BASE + ruta))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(cos)));
        return enviar(peticio, tipus);
    }

    // Fa una petició DELETE. No esperem cap resposta (204 No Content).
    public static void delete(String ruta) {
        HttpRequest.Builder peticio = HttpRequest.newBuilder(URI.create(URL_BASE + ruta))
                .DELETE();
        enviar(peticio, Void.class);
    }

    // Metodes auxiliars 
    // Afegeix el token, envia la petició i llegeix la resposta
    private static <T> T enviar(HttpRequest.Builder peticio, Class<T> tipus) {
        if (token != null) {
            peticio.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> resposta;
        try {
            resposta = client.send(peticio.build(), HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ApiException("No es pot connectar amb el servidor");
        }

        int codi = resposta.statusCode();
        String cos = resposta.body();

        // Codis 400 o més: el servidor diu que alguna cosa ha anat malament
        if (codi >= 400) {
            throw new ApiException(missatgeError(codi, cos));
        }

        // Respostes sense contingut (per exemple, 204)
        if (cos == null || cos.isBlank()) {
            return null;
        }
        return json.readValue(cos, tipus);
    }

    // El nostre servidor envia el missatge d'error com a text; si no n'hi ha, en posem un
    private static String missatgeError(int codi, String cos) {
        if (cos != null && !cos.isBlank()) {
            return cos;
        }
        if (codi == 401) {
            return "Has d'iniciar sessió";
        }
        if (codi == 403) {
            return "No tens permís per fer això";
        }
        return "Error del servidor (" + codi + ")";
    }
}
