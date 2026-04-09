package server.httpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Einfacher HTTP-Server mit reinem JDK (com.sun.net.httpserver).
 *
 * Endpoints:
 *   GET  /image/{id}   → Dummy-Bild generieren und downloaden
 *   POST /image/{id}   → Bild hochladen (Binary im Body)
 *   GET  /image/list   → Alle gespeicherten IDs als Text
 */
public class ImageServer {

    private static final Logger LOG = Logger.getLogger(ImageServer.class.getName());
    private final HttpServer httpServer;
    private final Map<String, byte[]> imageStore = new ConcurrentHashMap<>();

    Path allCardsDirectory = Path.of(System.getProperty("user.dir") + "/1PrüfungsBilder");//directory wo der Server alle Bilder hat

    public ImageServer(int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        httpServer.createContext("/image", this::handleImage);

        LOG.info("ImageServer konfiguriert auf Port " + port);
    }

    public void start() {
        httpServer.start();
        LOG.info("ImageServer gestartet: http://localhost:" + httpServer.getAddress().getPort() + "/image/{id}");
    }

    public void stop() {
        httpServer.stop(0);
        LOG.info("ImageServer gestoppt");
    }

    public int getPort() {
        return httpServer.getAddress().getPort();
    }

    // ── Request-Routing ──────────────────────────────────────────────

    private void handleImage(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // ID aus Pfad extrahieren: /image/{id}
        String id = path.replaceFirst("^/image/?", "");

        if (id.isEmpty()) {
            sendText(exchange, 400, "Bitte ID angeben: /image/{id}");
            return;
        }

        // GET /image/list → alle IDs auflisten
        if ("GET".equals(method) && "list".equals(id)) {
            String body = imageStore.isEmpty()
                    ? "(keine Bilder gespeichert)"
                    : String.join("\n", imageStore.keySet());
            sendText(exchange, 200, body);
            return;
        }

        switch (method) {
            case "GET"  -> handleDownload(exchange, id);
            case "POST" -> handleUpload(exchange, id);
            default     -> sendText(exchange, 405, "Method Not Allowed: " + method);
        }
    }

    // ── GET: Bild downloaden ─────────────────────────────────────────

    /*private void handleDownload(HttpExchange exchange, String id) throws IOException {

        id = id.replaceAll("\\*"," ");

        byte[] imageData = Files.readAllBytes(Path.of(allCardsDirectory + "/" + id));//Pfad wo alle gespeichert + Name.jpg
        if (imageData == null) {//FRAGE: kann man zuerst den Statuscode senden, und wenn der 200/OK ist dann das bild? Also 200 wenn das bild vorhanden ist?
            imageData = Files.readAllBytes(Path.of(allCardsDirectory + "/backside.jpg"));//hab versucht eine placeholder datei zu schicken
        } else {
            LOG.info("GET /image/%s → Gespeichertes Bild (%d bytes)".formatted(id, imageData.length));
        }

        exchange.getResponseHeaders().set("Content-Type", "image/png");
        exchange.getResponseHeaders().set("Content-Disposition",
                "attachment; filename=\"%s.png\"".formatted(id));
        exchange.sendResponseHeaders(200, imageData.length);
        exchange.getResponseBody().write(imageData);
        exchange.close();

    }*/
    private void handleDownload(HttpExchange exchange, String id) throws IOException {
        // Sicherheit: nur Dateiname erlauben, keine Pfad-Traversal (../)
        id = id.replaceAll("\\*"," ");

        Path imagePath = allCardsDirectory.resolve(id);

        if (!Files.exists(imagePath)) {
            LOG.warning("GET /image/%s → Datei nicht gefunden: %s".formatted(id, imagePath));
            sendError(exchange, 404, "Image not found: " + id);
            return;
        }

        byte[] imageData = Files.readAllBytes(imagePath);
        LOG.info("GET /image/%s → %s (%d bytes)".formatted(id, imagePath, imageData.length));

        exchange.getResponseHeaders().set("Content-Type", "image/png");
        exchange.getResponseHeaders().set("Content-Disposition",
                "attachment; filename=\"%s.png\"".formatted(id));
        exchange.sendResponseHeaders(200, imageData.length);
        exchange.getResponseBody().write(imageData);
        exchange.close();
    }

    private void sendError(HttpExchange exchange, int status, String message) throws IOException {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    // ── POST: Bild hochladen ─────────────────────────────────────────

    private void handleUpload(HttpExchange exchange, String id) throws IOException {
        byte[] data;
        try (InputStream is = exchange.getRequestBody()) {
            data = is.readAllBytes();
        }

        if (data.length == 0) {
            sendText(exchange, 400, "Leerer Body — Bild fehlt");
            return;
        }

        imageStore.put(id, data);
        LOG.info("POST /image/%s → %d bytes gespeichert".formatted(id, data.length));

        String response = "OK: Bild '%s' gespeichert (%d bytes)".formatted(id, data.length);
        sendText(exchange, 200, response);
    }

    // ── Dummy-Bild generieren ────────────────────────────────────────

    /**
     * Erzeugt ein 300x200 PNG mit der ID als Text.
     * Reines JDK — java.awt + javax.imageio.
     */
    public static byte[] generateDummyImage(String id) throws IOException {
        int width = 300, height = 200;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        // Hintergrund
        g.setColor(new Color(0x33, 0x66, 0x99));
        g.fillRect(0, 0, width, height);

        // Rahmen
        g.setColor(Color.WHITE);
        g.drawRect(5, 5, width - 11, height - 11);

        // Text
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("ID: " + id, 30, 80);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Dummy-Bild (300x200)", 30, 120);
        g.drawString("Generated by ImageServer", 30, 150);

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

    // ── Hilfsmethode ─────────────────────────────────────────────────

    private void sendText(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    // ── Main ─────────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        ImageServer server = new ImageServer(port);
        server.start();
        System.out.println("ImageServer läuft auf http://localhost:" + port);
        System.out.println("  GET  /image/{id}  → Bild downloaden");
        System.out.println("  POST /image/{id}  → Bild uploaden");
        System.out.println("  GET  /image/list  → Alle IDs auflisten");
        System.out.println("ENTER drücken zum Beenden...");
        System.in.read();
        server.stop();
    }
}
