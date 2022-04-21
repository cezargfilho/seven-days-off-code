import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Logger;

public class Application {

  private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

  public static void main(String[] args) {
    String url = "https://imdb-api.com/en/API/Top250Movies/<apiKey>";

    try {
      URI uri = new URI(url.replace("<apiKey>", args[0]));
      HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

      LOGGER.info("URI criada para host: " + uri.getHost());

      HttpClient client = HttpClient.newHttpClient();
      client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
          .thenApply(HttpResponse::body)
          .thenAccept(System.out::println)
          .join();

      LOGGER.info("Outra forma de capturar o corpo de response");

      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

      String jsonResponse = response.body();
      System.out.println(jsonResponse);

    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
