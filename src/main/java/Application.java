import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {

  private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

  public static void main(String[] args) {
    if (args == null)
      throw new IllegalArgumentException("A apiKey deve ser passada via argumento!");

    String jsonResponse;
    try {
      //Day 1
      String url = "https://imdb-api.com/en/API/Top250Movies/<apiKey>";
      URI uri = new URI(url.replace("<apiKey>", args[0]));
      HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

      LOGGER.info("URI criada para host: " + uri.getHost());

      HttpClient client = HttpClient.newHttpClient();
      HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

      jsonResponse = response.body();
      //System.out.println(jsonResponse);

    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }

    //Day 2
    String[] movies = convertToJsonMovies(jsonResponse);

    LOGGER.info("Filtrando apenas pela chave TITLE dos objetos JSON");
    List<String> titles = filterAllTitles(movies);
    titles.forEach(System.out::println);

    LOGGER.info("Filtrando apenas pela chave IMAGE dos objetos JSON");
    List<String> urlImages = filterAllUrlImages(movies);
    urlImages.forEach(System.out::println);

  }

  private static List<String> filterAllUrlImages(String[] movies) {
    return filterByAttribute(movies, 5);
  }

  private static List<String> filterAllTitles(String[] movies) {
    return filterByAttribute(movies, 3);
  }

  private static List<String> filterByAttribute(String[] movies, int position) {
    return Stream.of(movies).map(key -> key.split("\",\"")[position])
        .map(val -> val.split(":\"")[1])
        .map(element -> element.replaceAll("\"", ""))
        .collect(Collectors.toList());
  }

  private static String[] convertToJsonMovies(String json) {
    Pattern pattern = Pattern.compile("(.*\\[(.*)].*)");
    Matcher matcher = pattern.matcher(json);

    if (!matcher.matches())
      throw new IllegalStateException("Pattern " + pattern.pattern() +" has no match in JSON");

    String[] moviesArr = matcher.group(2).split("},\\{");
    moviesArr[0] = moviesArr[0].substring(1);
    int last = moviesArr.length - 1;
    String lastString = moviesArr[last];
    moviesArr[last] = lastString.substring(0, lastString.length() - 1);
    return moviesArr;
  }
}
