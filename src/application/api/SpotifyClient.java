package application.api;

import java.util.List;
import java.util.ArrayList;

import application.model.Song;
import application.model.Status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SpotifyClient {

    private final String clientId;
    private final String clientSecret;
    private final HttpClient client;

    public SpotifyClient(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.client = HttpClient.newHttpClient();
    }

    public String getAccessToken() throws IOException, InterruptedException {
        String body = "grant_type=client_credentials"
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        String json = response.body();

        // Simple extraction without a JSON library
        int start = json.indexOf("\"access_token\":\"") + 16;
        int end = json.indexOf("\"", start);

        return json.substring(start, end);
    }

    public List<Song> searchTracks(String query) throws IOException, InterruptedException {
        String accessToken = getAccessToken();

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

        String url = "https://api.spotify.com/v1/search?q=" + encodedQuery + "&type=track&limit=10";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String json = response.body();

        List<Song> songs = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        if (!root.has("tracks")) {
            System.out.println("Spotify did not return any tracks.");
            return songs;
        }

        JsonNode items = root.get("tracks").get("items");

        for (JsonNode item : items) {
            String title = item.get("name").asText();

            String artist = item.get("artists").get(0).get("name").asText();

            String album = item.get("album").get("name").asText();

            int yearReleased = Integer.parseInt(item.get("album").get("release_date").asText().substring(0, 4));

            int runtimeSeconds = item.get("duration_ms").asInt() / 1000;

            Status status = Status.PLANNED;
            double userRating = 0.0;
            String review = "";

            songs.add(new Song(title, status, userRating, album, artist, yearReleased, runtimeSeconds, review));
        }
        
        return songs;
    }
}