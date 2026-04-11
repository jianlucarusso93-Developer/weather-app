package util;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Scanner;

import com.google.gson.*;

public class finalGetWeather {

    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Insert a city: ");
            String city = scanner.nextLine().trim();

            if (city.isEmpty()) {
                System.out.println("❌ Please enter a valid city.");
                return;
            }

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);

            // ---------------- GEO ----------------
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name="
                    + encodedCity + "&language=en";

            HttpResponse<String> geoResponse = sendWithRetry(client, geoUrl);

            if (geoResponse == null) {
                System.out.println("🌐 Geocoding service unavailable.");
                return;
            }

            JsonObject geoJson = JsonParser.parseString(geoResponse.body()).getAsJsonObject();

            if (!geoJson.has("results")) {
                System.out.println("❌ City not found.");
                return;
            }

            JsonArray results = geoJson.getAsJsonArray("results");

            int choice = 0;

            if (results.size() > 1) {
                System.out.println("\n📍 Multiple cities found:");

                for (int i = 0; i < results.size(); i++) {
                    JsonObject obj = results.get(i).getAsJsonObject();

                    String name = obj.get("name").getAsString();
                    String country = obj.get("country").getAsString();
                    String region = obj.has("admin1")
                            ? obj.get("admin1").getAsString()
                            : "Unknown";

                    System.out.println(i + " → " + name + " - " + region + " (" + country + ")");
                }

                System.out.print("\nChoose an index: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("❌ Invalid input. Please enter a number.");
                    return;
                }

                choice = scanner.nextInt();

                if (choice < 0 || choice >= results.size()) {
                    System.out.println("❌ Invalid index.");
                    return;
                }
            }

            JsonObject selected = results.get(choice).getAsJsonObject();

            double lat = selected.get("latitude").getAsDouble();
            double lon = selected.get("longitude").getAsDouble();
            String name = selected.get("name").getAsString();
            String country = selected.get("country").getAsString();
            String region = selected.has("admin1")
                    ? selected.get("admin1").getAsString()
                    : "Unknown";

            // ---------------- WEATHER ----------------
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat
                    + "&longitude=" + lon
                    + "&current=temperature_2m,weathercode";

            HttpResponse<String> weatherResponse = sendWithRetry(client, weatherUrl);

            if (weatherResponse == null) {
                System.out.println("🌐 Weather service unavailable.");
                return;
            }

            JsonObject weatherJson = JsonParser.parseString(weatherResponse.body()).getAsJsonObject();

            if (!weatherJson.has("current")) {
                System.out.println("⚠️ Weather data not available.");
                return;
            }

            JsonObject current = weatherJson.getAsJsonObject("current");

            double temperature = current.get("temperature_2m").getAsDouble();
            int weatherCode = current.get("weathercode").getAsInt();

            String description = getWeatherDescription(weatherCode);

            // ---------------- OUTPUT ----------------
            System.out.println("\n🌤️ Weather in " + name + " - " + region + " (" + country + ")");
            System.out.println("🌡️ Temperature: " + temperature + "°C");
            System.out.println("📌 Condition: " + description);

        } catch (Exception e) {
            System.out.println("❌ Unexpected error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // 🔁 Retry automatico
    private static HttpResponse<String> sendWithRetry(HttpClient client, String url) {

        for (int i = 1; i <= MAX_RETRIES; i++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return response;
                } else {
                    System.out.println("⚠️ Attempt " + i + ": HTTP error " + response.statusCode());
                }

            } catch (IOException e) {
                System.out.println("🌐 Attempt " + i + ": Network error.");
            } catch (InterruptedException e) {
                System.out.println("⚠️ Request interrupted.");
                Thread.currentThread().interrupt();
                return null;
            }

            try {
                Thread.sleep(1000); // attesa 1 secondo tra tentativi
            } catch (InterruptedException ignored) {}
        }

        return null;
    }

    // 🌦️ Descrizione meteo
    private static String getWeatherDescription(int code) {
        switch (code) {
            case 0: return "Clear sky";
            case 1: case 2: case 3: return "Partly cloudy";
            case 45: case 48: return "Fog";
            case 51: case 53: case 55: return "Drizzle";
            case 61: case 63: case 65: return "Rain";
            case 71: case 73: case 75: return "Snow";
            case 80: case 81: case 82: return "Rain showers";
            case 95: return "Thunderstorm";
            default: return "Unknown";
        }
    }
}