package fr.epita.apping.fullstack.gamesearch.kafka.dto;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameIngestionMessage {

  private String partnerName;
  private String partnerApiKey;

  private String gameId;

  private String title;

  private Integer releaseYear;

  private List<String> genres;

  private List<String> platforms;

  private String publisher;

  private String description;

  private String coverUrl;

  private Float rating;

  private List<String> tags;
}
