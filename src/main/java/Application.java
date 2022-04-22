import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {

  public static void main(String[] args) {
    if (args == null)
      throw new IllegalArgumentException("A apiKey deve ser passada via argumento!");

    String jsonResponse = getJsonFromUrl(args[0]);

    String[] movies = convertToJsonMovies(jsonResponse);

    List<String> titles = filterAllAttributesByPosition(movies, 3);
    List<String> urlImages = filterAllAttributesByPosition(movies, 5);
    List<String> ratingList = filterAllAttributesByPosition(movies, 7);
    List<String> yearList = filterAllAttributesByPosition(movies, 4);

    List<Movie> moviesList = parseToMovieList(titles, urlImages, ratingList, yearList);

    moviesList.forEach(System.out::println);
  }

  private static List<Movie> parseToMovieList(
      List<String> titles, List<String> urlImages, List<String> ratingList, List<String> yearList) {

    List<Movie> moviesList = new ArrayList<>();

    for (int i = 0; i < titles.size(); i++) {
      moviesList.add(
          new Movie(titles.get(i), urlImages.get(i), ratingList.get(i), yearList.get(i)));
    }
    return moviesList;
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

  private static List<String> filterAllAttributesByPosition(String[] movies, int position) {
    return filterByAttribute(movies, position);
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
