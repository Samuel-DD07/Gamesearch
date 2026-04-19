package fr.epita.apping.fullstack.gamesearch.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEntity {

  private UUID id;
  private String externalId;
  private String title;
  private Integer releaseYear;
  private String publisher;
  private String description;
  private String coverUrl;
  private Float rating;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder.Default private List<GenreEntity> genres = new ArrayList<>();

  @Builder.Default private List<PlatformEntity> platforms = new ArrayList<>();

  @Builder.Default private List<TagEntity> tags = new ArrayList<>();

  private PartnerEntity partner;
}
