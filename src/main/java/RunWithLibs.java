import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunWithLibs {

  public static void main(String[] args) throws Exception {
    if (args == null)
      throw new IllegalArgumentException("A apiKey deve ser passada via argumento!");

    String json = getJsonFromUrl(args[0]);

    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(json);

    JsonNode items = jsonNode.get("items");

    List<String> titleList = getAllAttributeValueByKey(items, "titles");
    List<String> imageList = getAllAttributeValueByKey(items, "image");
    List<String> yearList = getAllAttributeValueByKey(items, "year");
    List<String> ratingList = getAllAttributeValueByKey(items, "rating");

    List<Movie> movieList = new ArrayList<>();

    for (JsonNode node : items) {
      movieList.add(
          new Movie(
              node.get("title").textValue(), node.get("year").textValue(),
              node.get("image").textValue(), node.get("imDbRating").textValue()));
    }

    titleList.forEach(System.out::println);
    imageList.forEach(System.out::println);
    yearList.forEach(System.out::println);
    ratingList.forEach(System.out::println);
    movieList.forEach(System.out::println);

  }

  private static List<String> getAllAttributeValueByKey(JsonNode items, String key) {
    return Stream.of(items).map(e -> e.get(key).textValue()).collect(Collectors.toList());
  }

  private static String getJsonFromUrl(String key) {
    try {
      String url = "https://imdb-api.com/en/API/Top250Movies/<apiKey>";
      URI uri = new URI(url.replace("<apiKey>", key));
      HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

      return response.body();

    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
