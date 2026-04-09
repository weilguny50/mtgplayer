package jfx.httpClient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * HTTP-Client mit reinem JDK (java.net.http.HttpClient, Java 11+).
 *
 * Bietet zwei Operationen:
 *   download(id, targetPath)   → GET  /image/{id} → Datei speichern
 *   upload(id, sourcePath)     → POST /image/{id} → Datei hochladen
 */
public class ImageClient {

    private static final Logger LOG = Logger.getLogger(ImageClient.class.getName());

    private final String baseUrl;
    private final HttpClient httpClient;

    public ImageClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    // ── Download: GET /image/{id} ────────────────────────────────────

    /**
     * Lädt ein Bild vom Server und speichert es lokal.
     *
     * @param id         Bild-ID (z.B. "foto-42")
     * @param targetPath Lokaler Speicherpfad (z.B. Path.of("download.png"))
     * @return           Anzahl heruntergeladener Bytes
     */
    public long download(String id, Path targetPath) throws IOException, InterruptedException {
        String idCoded = id.replaceAll(" ","*");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/image/" + idCoded))
                .GET()
                .build();

        LOG.info("GET %s/image/%s → %s".formatted(baseUrl, id, targetPath));

        HttpResponse<Path> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofFile(targetPath.resolve(id)));// resolve heißt, geh in den Ordner rein und mach dort was mit file()

        if (response.statusCode() == 404) {
            Files.deleteIfExists(targetPath.resolve(id));
        }

        long size = Files.size(targetPath);
        LOG.info("Download OK: %s (%d bytes)".formatted(targetPath, size));
        return size;
    }

    /**
     * Lädt ein Bild vom Server und gibt die Bytes direkt zurück (ohne Datei).
     */
    public byte[] downloadBytes(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/image/" + id))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Download fehlgeschlagen: HTTP %d".formatted(response.statusCode()));
        }

        LOG.info("Download OK: %s (%d bytes)".formatted(id, response.body().length));
        return response.body();
    }

    // ── Upload: POST /image/{id} ─────────────────────────────────────

    /**
     * Lädt ein lokales Bild auf den Server hoch.
     *
     * @param id         Bild-ID unter der es gespeichert wird
     * @param sourcePath Lokale Datei (z.B. Path.of("foto.png"))
     * @return           Server-Antwort als Text
     */
    public String upload(String id, Path sourcePath) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/image/" + id))
                .header("Content-Type", "image/png")
                .POST(HttpRequest.BodyPublishers.ofFile(sourcePath))
                .build();

        LOG.info("POST %s/image/%s ← %s (%d bytes)"
                .formatted(baseUrl, id, sourcePath, Files.size(sourcePath)));

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Upload fehlgeschlagen: HTTP %d — %s"
                    .formatted(response.statusCode(), response.body()));
        }

        LOG.info("Upload OK: " + response.body());
        return response.body();
    }

    /**
     * Lädt Bild-Bytes direkt auf den Server hoch (ohne lokale Datei).
     */
    public String uploadBytes(String id, byte[] imageData) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/image/" + id))
                .header("Content-Type", "image/png")
                .POST(HttpRequest.BodyPublishers.ofByteArray(imageData))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Upload fehlgeschlagen: HTTP %d — %s"
                    .formatted(response.statusCode(), response.body()));
        }

        LOG.info("Upload OK: " + response.body());
        return response.body();
    }

    // ── List: GET /image/list ────────────────────────────────────────

    /**
     * Listet alle gespeicherten Bild-IDs auf dem Server auf.
     */
    public String listImages() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/image/list"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    // ── Main (Demo) ──────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        String baseUrl = args.length > 0 ? args[0] : "http://localhost:8080";
        ImageClient client = new ImageClient(baseUrl);

        System.out.println("=== Download Dummy-Bild ===");
        Path downloadPath = Path.of("demo-download.png");
        long size = client.download("demo-42", downloadPath);
        System.out.printf("Gespeichert: %s (%d bytes)%n%n", downloadPath, size);

        System.out.println("=== Upload Bild ===");
        String result = client.upload("uploaded-42", downloadPath);
        System.out.println("Server: " + result + "\n");

        System.out.println("=== Re-Download (uploaded) ===");
        Path rePath = Path.of("re-download.png");
        long reSize = client.download("uploaded-42", rePath);
        System.out.printf("Gespeichert: %s (%d bytes)%n%n", rePath, reSize);

        System.out.println("=== Liste ===");
        System.out.println(client.listImages());
    }
}
