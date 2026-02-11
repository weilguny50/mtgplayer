package server.ImageDownload;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

/**
 * Scryfall Downloader
 * Java + JSoup Version
 * Features:
 * - Set-Link Scraping
 * - Large Image Download
 * - Filename Sanitizing
 * - Duplicate Check
 * - Resume Download (HTTP Range)
 *
 * Requires:
 * JSoup 1.15+
 * https://jsoup.org/
 */
public class OldDownloadScript_featMatthias {

    private static final String BASE_URL = "https://scryfall.com/sets";
    private static final String OUTPUT_DIR = "scryfall_cards";

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public static void main(String[] args) {
        try {
            ensureDirectory();

            System.out.println("Lade Übersichtsseite...");
            Document overview = Jsoup.connect(BASE_URL)
                    .userAgent("Mozilla/5.0")
                    .timeout(60_000)
                    .get();

            Set<String> setLinks = extractSetLinks(overview);
            System.out.println("Gefundene Set-Seiten: " + setLinks.size());

            List<DownloadItem> allDownloads = new ArrayList<>();

            int idx = 1;
            for (String url : new TreeSet<>(setLinks)) {
                System.out.println("[" + idx++ + "/" + setLinks.size() + "] Lade Set-Seite: " + url);

                try {
                    Document setDoc = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0")
                            .timeout(60_000)
                            .get();

                    allDownloads.addAll(extractImages(setDoc));
                    Thread.sleep(500);

                } catch (Exception e) {
                    System.out.println("  Fehler: " + e.getMessage());
                }
            }

            System.out.println("\nInsgesamt identifizierte Bilder: " + allDownloads.size());

            List<DownloadItem> finalList = filterExisting(allDownloads);
            System.out.println("Verbleibende Downloads: " + finalList.size());

            downloadWithResume(finalList);

            System.out.println("\nFertig! Resume‑fähiger Download abgeschlossen.");

        } catch (Exception e) {
            System.err.println("Fataler Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------------------------------
    // Set Links
    // --------------------------------------------------

    private static Set<String> extractSetLinks(Document doc) {
        Set<String> links = new HashSet<>();

        Elements aTags = doc.select("a[href^=https://scryfall.com/sets/]");

        for (Element a : aTags) {
            links.add(a.attr("href"));
        }

        return links;
    }

    // --------------------------------------------------
    // Images
    // --------------------------------------------------

    private static List<DownloadItem> extractImages(Document doc) {
        List<DownloadItem> list = new ArrayList<>();

        Elements imgs = doc.select("img[src^=https://cards.scryfall.io/normal/]");

        for (Element img : imgs) {
            String src = img.attr("src");
            String title = img.attr("title");

            if (title.isBlank()) {
                title = img.attr("alt");
            }
            if (title.isBlank()) {
                title = "Unbenannt";
            }

            // Front / Back Erkennung
            String side = "";
            if (src.contains("/front/")) {
                side = "front";
            } else if (src.contains("/back/")) {
                side = "back";
            }

            // Normal -> Large
            src = src.replace("/normal/", "/large/");

            title = sanitizeFilename(title);

            list.add(new DownloadItem(src, title, side));
        }

        return list;
    }

    // --------------------------------------------------
    // Filename Utils
    // --------------------------------------------------

    private static String sanitizeFilename(String name) {
        name = name.replace("#", "Nr.");
        name = name.replaceAll("[\\\\/:*?\"<>|]", "_");
        name = name.trim();

        while (name.endsWith(".")) {
            name = name.substring(0, name.length() - 1);
        }

        String[] split = name.split("\\(");
        String[] split1 = split[1].split("\\)");


        return split1[0];
    }

    // --------------------------------------------------
    // Directory
    // --------------------------------------------------

    private static void ensureDirectory() throws IOException {
        Path dir = Paths.get(OUTPUT_DIR);
        Files.createDirectories(dir);

        Path test = dir.resolve(".write_test");
        Files.writeString(test, "test");
        Files.delete(test);
    }

    // --------------------------------------------------
    // Duplicate Filter
    // --------------------------------------------------

    private static List<DownloadItem> filterExisting(List<DownloadItem> list) {
        List<DownloadItem> finalList = new ArrayList<>();

        for (DownloadItem item : list) {
            try {
                String original = Paths.get(new URI(item.url).getPath())
                        .getFileName().toString();

                String sideSuffix = item.side.isBlank() ? "" : "_" + item.side;
                String filename = item.title + sideSuffix + "_"+".jpg";
                Path path = Paths.get(OUTPUT_DIR, filename);

                if (!Files.exists(path)) {
                    item.filename = filename;
                    item.path = path;
                    finalList.add(item);
                }

            } catch (Exception e) {
                System.out.println("Dateiname Fehler: " + e.getMessage());
            }
        }

        return finalList;
    }

    // --------------------------------------------------
    // Resume Download
    // --------------------------------------------------

    private static void downloadWithResume(List<DownloadItem> list)
            throws Exception {

        int idx = 1;

        for (DownloadItem item : list) {
            System.out.println("[" + idx++ + "/" + list.size() + "] Lade: " + item.filename);

            long existingSize = 0;

            if (Files.exists(item.path)) {
                existingSize = Files.size(item.path);
            }

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(item.url))
                    .timeout(Duration.ofMinutes(2))
                    .GET();

            if (existingSize > 0) {
                builder.header("Range", "bytes=" + existingSize + "-");
                System.out.println("  Resume ab Byte: " + existingSize);
            }

            HttpRequest request = builder.build();

            HttpResponse<InputStream> response = CLIENT.send(
                    request,
                    HttpResponse.BodyHandlers.ofInputStream());

            try (InputStream in = response.body();
                 RandomAccessFile raf = new RandomAccessFile(
                         item.path.toFile(), "rw")) {

                if (existingSize > 0) {
                    raf.seek(existingSize);
                }

                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    raf.write(buffer, 0, bytesRead);
                }

                // 0.5 Sekunden Delay zwischen Downloads (Server schonen bzw. Auto-Bann-Risiko verringern)
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // --------------------------------------------------
    // Helper Class
    // --------------------------------------------------

    private static class DownloadItem {
        String url;
        String title;
        String side;
        String filename;
        Path path;

        DownloadItem(String url, String title, String side) {
            this.url = url;
            this.title = title;
            this.side = side == null ? "" : side;
        }
    }
    }

