package fr.epita.apping.fullstack.gamesearch.presentation.api.response;

import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameDetailResponse {

  private UUID id;
  private String title;
  private Integer releaseYear;
  private String publisher;
  private String description;
  private String coverUrl;
  private Float rating;
  private List<String> genres;
  private List<String> platforms;
  private List<String> tags;
  private List<GameResponse> similarGames;
}
