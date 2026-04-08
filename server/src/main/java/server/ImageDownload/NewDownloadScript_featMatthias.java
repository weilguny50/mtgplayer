package server.ImageDownload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewDownloadScript_featMatthias {

    //Website, die nach Sets durchsucht werden soll
    private static final String BASE_URL = "https://scryfall.com/sets";
    //Ordner-Name für Download
    private static final String OUTPUT_DIR = "scryfall_cards";
    //Wie soll sich das Download-Skript beim Server zeigen (08/15 Browser-Signatur)
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0 Safari/537.36";

    private static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public static void main(String[] args) {
        try {
            ensureDirectory(Paths.get(OUTPUT_DIR));
            System.out.println("Lade Übersichtsseite...");
            String html = fetchHtml(BASE_URL);

            // Filtert nur /sets/abc, keine Unterordner (nicht-englischsprachige Sets) wie /sets/abc/en
            Set<String> setLinks = extractLinks(html, "https://scryfall\\.com/sets/[a-z0-9]+(?=[\"'])");
            System.out.println("Gefundene Set-Seiten: " + setLinks.size());

            int count = 1;
            //Ladet ein Set nach dem anderen
            for (String setUrl : setLinks) {
                System.out.printf("[%d/%d] Verarbeite Set: %s%n", count++, setLinks.size(), setUrl);
                //Startet den Download aller Bilder pro Set
                processSetPage(setUrl);
                //Wartet eine halbe Sekunde zwischen den Sets
                Thread.sleep(500);
            }
            System.out.println("\nFertig! Alle Bilder wurden gescannt.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processSetPage(String setUrl) {
        try {
            String html = fetchHtml(setUrl);

            // Regex sucht den kompletten <img> Tag, der ein 'normal' (normalgroßes) Scryfall Bild enthält
            Pattern imgTagPattern = Pattern.compile("<img[^>]+src=\"(https://cards\\.scryfall\\.io/normal/[^\"]+)\"[^>]*>");
            Matcher matcher = imgTagPattern.matcher(html);

            while (matcher.find()) {
                String fullTag = matcher.group(0);
                String normalUrl = matcher.group(1);
                //Ändert die normal-Bildgröße zu large und entfernt am Ende des Bild-Links das ? mit den nachfolgenden Zahlen
                String largeUrl = normalUrl.replace("/normal/", "/large/")
                                            .replaceAll("\\?[0-9]+$", "");


                // Extrahiere Titel (alt oder title Attribut)
                String cardTitle = extractAttribute(fullTag, "title");
                if (cardTitle.isEmpty() || cardTitle.equals("Unbenannt")) {
                    cardTitle = extractAttribute(fullTag, "alt");
                }
                if (cardTitle.isEmpty()) cardTitle = "Unbenannt";

                // Front/Back Info wird aus der Bild URL extrahiert
                String sideInfo = "";
                if (largeUrl.contains("/back/")) sideInfo = "_back";
                else if (largeUrl.contains("/front/")) sideInfo = "_front";

                String fileName = buildFileName(cardTitle, largeUrl, sideInfo);
                downloadImage(largeUrl, fileName);


            }
        } catch (Exception e) {
            System.err.println("Fehler bei Seite " + setUrl + ": " + e.getMessage());
        }
    }

    private static String extractAttribute(String tag, String attr) {
        Pattern p = Pattern.compile(attr + "=\"([^\"]*)\"");
        Matcher m = p.matcher(tag);
        return m.find() ? m.group(1) : "";
    }

    private static String buildFileName(String title, String url, String sideInfo) {
        String safeTitle = title.replace("&#39;", "")
                                .replace("#", "Nr.")
                                .replaceAll("[\\\\/:*?\"<>|]", "_")
                                .trim();
        String [] split = safeTitle.split("\\(");
        String [] split1 = split[1].split("\\)");
        // ID aus der URL extrahieren (der Teil vor .jpg)
        String[] parts = url.split("/");
        String id = parts[parts.length - 1];

        String finalName = split1[0] + sideInfo + "_.jpg";

        // Schutz gegen zu lange Pfade (Windows Limit ~255 Zeichen)
        if (finalName.length() > 200) {
            finalName = finalName.substring(0, 100) + "_.jpg";
        }
        return finalName;
    }

    private static void downloadImage(String url, String fileName) throws InterruptedException {
        Path path = Paths.get(OUTPUT_DIR, fileName);
        if (Files.exists(path)) return;

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("User-Agent", USER_AGENT).GET().build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() == 200) {
                Files.copy(response.body(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("  OK: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("  Fehler beim Download: " + fileName);
        }
        //Wartet eine halbe Sekunde zwischen den Bilder-Downloads (damit man eher nicht geblockt wird)
        Thread.sleep(500);
    }

    private static String fetchHtml(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private static Set<String> extractLinks(String html, String regex) {
        Set<String> links = new HashSet<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(html);
        while (m.find()) links.add(m.group());
        return links;
    }

    private static void ensureDirectory(Path path) throws IOException {
        if (!Files.exists(path)) Files.createDirectories(path);
    }
}