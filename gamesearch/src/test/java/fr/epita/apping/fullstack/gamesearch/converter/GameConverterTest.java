package fr.epita.apping.fullstack.gamesearch.converter;

import static org.assertj.core.api.Assertions.assertThat;

import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.data.model.GenreModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PartnerModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PlatformModel;
import fr.epita.apping.fullstack.gamesearch.data.model.TagModel;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GenreEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PlatformEntity;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.GameDetailResponse;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.GameResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GameConverter")
class GameConverterTest {

  private UUID gameId;
  private UUID genreId;
  private UUID platformId;
  private UUID tagId;
  private UUID partnerId;
  private LocalDateTime now;

  @BeforeEach
  void setUp() {
    gameId = UUID.randomUUID();
    genreId = UUID.randomUUID();
    platformId = UUID.randomUUID();
    tagId = UUID.randomUUID();
    partnerId = UUID.randomUUID();
    now = LocalDateTime.now();
  }

  private GameModel buildFullModel() {
    return GameModel.builder()
        .id(gameId)
        .externalId("ext-42")
        .title("Dark Souls")
        .releaseYear(2011)
        .publisher("FromSoftware")
        .description("A very hard game.")
        .coverUrl("https://example.com/cover.jpg")
        .rating(9.0f)
        .createdAt(now)
        .updatedAt(now)
        .genres(List.of(GenreModel.builder().id(genreId).name("Action RPG").build()))
        .platforms(List.of(PlatformModel.builder().id(platformId).name("PC").build()))
        .tags(List.of(TagModel.builder().id(tagId).name("Soulslike").build()))
        .partner(PartnerModel.builder().id(partnerId).name("FromSoft Partner").build())
        .build();
  }

  @Nested
  @DisplayName("toEntity()")
  class ToEntity {

    @Test
    @DisplayName("maps all scalar fields from GameModel to GameEntity")
    void mapsScalarFields() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());

      assertThat(entity.getId()).isEqualTo(gameId);
      assertThat(entity.getExternalId()).isEqualTo("ext-42");
      assertThat(entity.getTitle()).isEqualTo("Dark Souls");
      assertThat(entity.getReleaseYear()).isEqualTo(2011);
      assertThat(entity.getPublisher()).isEqualTo("FromSoftware");
      assertThat(entity.getDescription()).isEqualTo("A very hard game.");
      assertThat(entity.getCoverUrl()).isEqualTo("https://example.com/cover.jpg");
      assertThat(entity.getRating()).isEqualTo(9.0f);
      assertThat(entity.getCreatedAt()).isEqualTo(now);
      assertThat(entity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("maps genres list with correct id and name")
    void mapsGenres() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());

      assertThat(entity.getGenres()).hasSize(1);
      assertThat(entity.getGenres().get(0).getId()).isEqualTo(genreId);
      assertThat(entity.getGenres().get(0).getName()).isEqualTo("Action RPG");
    }

    @Test
    @DisplayName("maps platforms list with correct id and name")
    void mapsPlatforms() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());

      assertThat(entity.getPlatforms()).hasSize(1);
      assertThat(entity.getPlatforms().get(0).getId()).isEqualTo(platformId);
      assertThat(entity.getPlatforms().get(0).getName()).isEqualTo("PC");
    }

    @Test
    @DisplayName("maps tags list with correct id and name")
    void mapsTags() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());

      assertThat(entity.getTags()).hasSize(1);
      assertThat(entity.getTags().get(0).getId()).isEqualTo(tagId);
      assertThat(entity.getTags().get(0).getName()).isEqualTo("Soulslike");
    }

    @Test
    @DisplayName("maps the partner with correct id and name")
    void mapsPartner() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());

      assertThat(entity.getPartner()).isNotNull();
      assertThat(entity.getPartner().getId()).isEqualTo(partnerId);
      assertThat(entity.getPartner().getName()).isEqualTo("FromSoft Partner");
    }

    @Test
    @DisplayName("maps partner as null when the model has no partner")
    void mapsNullPartner() {
      GameModel model = buildFullModel();
      model.setPartner(null);

      GameEntity entity = GameConverter.toEntity(model);

      assertThat(entity.getPartner()).isNull();
    }

    @Test
    @DisplayName("maps empty genre, platform, and tag lists correctly")
    void mapsEmptyCollections() {
      GameModel model =
          GameModel.builder()
              .id(gameId)
              .title("Minimal Game")
              .releaseYear(2020)
              .rating(5.0f)
              .genres(List.of())
              .platforms(List.of())
              .tags(List.of())
              .build();

      GameEntity entity = GameConverter.toEntity(model);

      assertThat(entity.getGenres()).isEmpty();
      assertThat(entity.getPlatforms()).isEmpty();
      assertThat(entity.getTags()).isEmpty();
    }
  }

  @Nested
  @DisplayName("toResponse()")
  class ToResponse {

    @Test
    @DisplayName("maps scalar fields to the API response object")
    void mapsScalarFields() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());
      GameResponse response = GameConverter.toResponse(entity);

      assertThat(response.getId()).isEqualTo(gameId);
      assertThat(response.getTitle()).isEqualTo("Dark Souls");
      assertThat(response.getReleaseYear()).isEqualTo(2011);
      assertThat(response.getPublisher()).isEqualTo("FromSoftware");
      assertThat(response.getRating()).isEqualTo(9.0f);
    }

    @Test
    @DisplayName("flattens genre, platform, and tag collections to lists of names")
    void flattensCollectionsToNames() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());
      GameResponse response = GameConverter.toResponse(entity);

      assertThat(response.getGenres()).containsExactly("Action RPG");
      assertThat(response.getPlatforms()).containsExactly("PC");
      assertThat(response.getTags()).containsExactly("Soulslike");
    }
  }

  @Nested
  @DisplayName("toDetailResponse()")
  class ToDetailResponse {

    @Test
    @DisplayName("includes all main game fields in the detail response")
    void includesMainFields() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());
      GameDetailResponse response = GameConverter.toDetailResponse(entity, List.of());

      assertThat(response.getId()).isEqualTo(gameId);
      assertThat(response.getTitle()).isEqualTo("Dark Souls");
    }

    @Test
    @DisplayName("includes the similar games list in the detail response")
    void includesSimilarGames() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());

      GameEntity similar =
          GameEntity.builder()
              .id(UUID.randomUUID())
              .title("Elden Ring")
              .releaseYear(2022)
              .genres(
                  List.of(GenreEntity.builder().id(UUID.randomUUID()).name("Action RPG").build()))
              .platforms(List.of(PlatformEntity.builder().id(UUID.randomUUID()).name("PC").build()))
              .tags(List.of())
              .rating(9.8f)
              .build();

      GameDetailResponse response = GameConverter.toDetailResponse(entity, List.of(similar));

      assertThat(response.getSimilarGames()).hasSize(1);
      assertThat(response.getSimilarGames().get(0).getTitle()).isEqualTo("Elden Ring");
    }

    @Test
    @DisplayName("returns an empty similar games list when none are provided")
    void emptySimilarGamesList() {
      GameEntity entity = GameConverter.toEntity(buildFullModel());
      GameDetailResponse response = GameConverter.toDetailResponse(entity, List.of());

      assertThat(response.getSimilarGames()).isEmpty();
    }
  }
}
