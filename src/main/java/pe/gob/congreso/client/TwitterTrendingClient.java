package pe.gob.congreso.client;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TwitterTrendingClient {

    private static final String BASE_URL = "https://www.twitter-trending.com/";

    public String getCookies() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, List<String>> headers = response.headers().map();
            List<String> setCookies = headers.getOrDefault("set-cookie", List.of());

            return setCookies.stream()
                    .map(cookie -> cookie.split(";")[0])
                    .collect(Collectors.joining("; "));
        } catch (Exception e) {
            throw new RuntimeException("Error fetching cookies", e);
        }
    }

    public String getTrends(String country) {
        try {
            String cookies = getCookies();
            HttpClient client = HttpClient.newHttpClient();
            String postUrl = BASE_URL + "other/trendslist/trend-result.php";
            String postData = "country=" + country;

            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(URI.create(postUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", cookies)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                    .POST(HttpRequest.BodyPublishers.ofString(postData))
                    .build();

            HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
            return postResponse.body();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching trends", e);
        }
    }
}
