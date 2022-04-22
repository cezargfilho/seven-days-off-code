import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class Movie {

  private String title;
  private String urlImage;
  private String rating;
  private String year;

}
