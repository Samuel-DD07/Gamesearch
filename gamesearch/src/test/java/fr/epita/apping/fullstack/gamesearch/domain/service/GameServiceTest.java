package fr.epita.apping.fullstack.gamesearch.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.data.model.GenreModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PlatformModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.*;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.exception.GameNotFoundException;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameCreateRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameUpdateRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService")
class GameServiceTest {

  @Mock private GameRepository gameRepository;
  @Mock private GenreRepository genreRepository;
  @Mock private PlatformRepository platformRepository;
  @Mock private TagRepository tagRepository;

  @InjectMocks private GameService gameService;

  private UUID gameId;
  private GameModel sampleModel;

  @BeforeEach
  void setUp() {
    gameId = UUID.randomUUID();

    GenreModel action = GenreModel.builder().id(UUID.randomUUID()).name("Action").build();
    PlatformModel pc = PlatformModel.builder().id(UUID.randomUUID()).name("PC").build();

    sampleModel =
        GameModel.builder()
            .id(gameId)
            .title("Hollow Knight")
            .releaseYear(2017)
            .publisher("Team Cherry")
            .rating(9.5f)
            .genres(List.of(action))
            .platforms(List.of(pc))
            .tags(List.of())
            .build();
  }

  @Nested
  @DisplayName("searchGames()")
  class SearchGames {

    @Test
    @DisplayName("delegates to repository and maps each result to a GameEntity")
    void delegatesToRepositoryAndMaps() {
      when(gameRepository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(sampleModel)));

      Page<GameEntity> result = gameService.searchGames(null, null, null, null, Pageable.unpaged());

      assertThat(result).hasSize(1);
      assertThat(result.getContent().get(0).getTitle()).isEqualTo("Hollow Knight");
      verify(gameRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("returns an empty page when no games match the criteria")
    void returnsEmptyPageWhenNoMatch() {
      when(gameRepository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(Page.empty());

      Page<GameEntity> result =
          gameService.searchGames("nonexistent", "FPS", "PS5", 2099, Pageable.unpaged());

      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("getGame()")
  class GetGame {

    @Test
    @DisplayName("returns the mapped entity when the game exists")
    void returnsEntityWhenFound() {
      when(gameRepository.findById(gameId)).thenReturn(Optional.of(sampleModel));

      GameEntity result = gameService.getGame(gameId);

      assertThat(result.getId()).isEqualTo(gameId);
      assertThat(result.getTitle()).isEqualTo("Hollow Knight");
    }

    @Test
    @DisplayName("throws GameNotFoundException when the game does not exist")
    void throwsWhenNotFound() {
      UUID missing = UUID.randomUUID();
      when(gameRepository.findById(missing)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> gameService.getGame(missing))
          .isInstanceOf(GameNotFoundException.class)
          .hasMessageContaining(missing.toString());
    }
  }

  @Nested
  @DisplayName("getSimilarGames()")
  class GetSimilarGames {

    @Test
    @DisplayName("returns empty list when the reference game does not exist")
    void returnsEmptyWhenGameMissing() {
      UUID missingId = UUID.randomUUID();
      when(gameRepository.findById(missingId)).thenReturn(Optional.empty());

      List<GameEntity> result = gameService.getSimilarGames(missingId);

      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("returns at most 5 similar games sorted by similarity score")
    void returnsAtMostFiveSortedBySimilarity() {
      when(gameRepository.findById(gameId)).thenReturn(Optional.of(sampleModel));

      List<GameModel> candidates =
          List.of(
              buildGame("Game A", "Action", "PC", "Team Cherry", 2017),
              buildGame("Game B", "Action", "PS4", "Other", 2019),
              buildGame("Game C", "RPG", "PC", "Other", 2015),
              buildGame("Game D", "Action", "PC", "Team Cherry", 2016),
              buildGame("Game E", "Action", "Switch", "Other", 2018),
              buildGame("Game F", "Action", "PC", "Indie", 2020));

      when(gameRepository.findBySharedGenres(eq(gameId), any())).thenReturn(candidates);
      when(gameRepository.findBySharedPlatforms(eq(gameId), any())).thenReturn(List.of());

      List<GameEntity> result = gameService.getSimilarGames(gameId);

      assertThat(result).hasSizeLessThanOrEqualTo(5);
    }
  }

  @Nested
  @DisplayName("createGame()")
  class CreateGame {

    @Test
    @DisplayName("saves a new game and returns the mapped entity")
    void savesAndReturnsEntity() {
      GameCreateRequest request =
          GameCreateRequest.builder()
              .title("Celeste")
              .releaseYear(2018)
              .genres(List.of("Platformer"))
              .platforms(List.of("PC"))
              .publisher("Maddy Makes Games")
              .rating(9.0f)
              .build();

      GenreModel genre = GenreModel.builder().id(UUID.randomUUID()).name("Platformer").build();
      PlatformModel platform = PlatformModel.builder().id(UUID.randomUUID()).name("PC").build();
      GameModel saved =
          GameModel.builder()
              .id(UUID.randomUUID())
              .title("Celeste")
              .releaseYear(2018)
              .genres(List.of(genre))
              .platforms(List.of(platform))
              .tags(List.of())
              .rating(9.0f)
              .build();

      when(genreRepository.findByName("Platformer")).thenReturn(Optional.of(genre));
      when(platformRepository.findByName("PC")).thenReturn(Optional.of(platform));
      when(gameRepository.save(any(GameModel.class))).thenReturn(saved);

      GameEntity result = gameService.createGame(request);

      assertThat(result.getTitle()).isEqualTo("Celeste");
      assertThat(result.getRating()).isEqualTo(9.0f);
      verify(gameRepository).save(any(GameModel.class));
    }

    @Test
    @DisplayName("creates new Genre and Platform entries when they do not exist")
    void createsNewGenreAndPlatformIfAbsent() {
      GameCreateRequest request =
          GameCreateRequest.builder()
              .title("New Game")
              .releaseYear(2024)
              .genres(List.of("NewGenre"))
              .platforms(List.of("NewPlatform"))
              .build();

      GenreModel newGenre = GenreModel.builder().id(UUID.randomUUID()).name("NewGenre").build();
      PlatformModel newPlatform =
          PlatformModel.builder().id(UUID.randomUUID()).name("NewPlatform").build();
      GameModel saved =
          GameModel.builder()
              .id(UUID.randomUUID())
              .title("New Game")
              .releaseYear(2024)
              .genres(List.of(newGenre))
              .platforms(List.of(newPlatform))
              .tags(List.of())
              .rating(0.0f)
              .build();

      when(genreRepository.findByName("NewGenre")).thenReturn(Optional.empty());
      when(genreRepository.save(any(GenreModel.class))).thenReturn(newGenre);
      when(platformRepository.findByName("NewPlatform")).thenReturn(Optional.empty());
      when(platformRepository.save(any(PlatformModel.class))).thenReturn(newPlatform);
      when(gameRepository.save(any(GameModel.class))).thenReturn(saved);

      gameService.createGame(request);

      verify(genreRepository).save(any(GenreModel.class));
      verify(platformRepository).save(any(PlatformModel.class));
    }

    @Test
    @DisplayName("defaults rating to 0.0 when not provided in the request")
    void defaultsRatingToZero() {
      GameCreateRequest request =
          GameCreateRequest.builder()
              .title("No Rating Game")
              .releaseYear(2020)
              .genres(List.of("Action"))
              .platforms(List.of("PC"))
              .build();

      GenreModel genre = GenreModel.builder().id(UUID.randomUUID()).name("Action").build();
      PlatformModel platform = PlatformModel.builder().id(UUID.randomUUID()).name("PC").build();
      GameModel saved =
          GameModel.builder()
              .id(UUID.randomUUID())
              .title("No Rating Game")
              .releaseYear(2020)
              .genres(List.of(genre))
              .platforms(List.of(platform))
              .tags(List.of())
              .rating(0.0f)
              .build();

      when(genreRepository.findByName("Action")).thenReturn(Optional.of(genre));
      when(platformRepository.findByName("PC")).thenReturn(Optional.of(platform));
      when(gameRepository.save(any(GameModel.class))).thenReturn(saved);

      GameEntity result = gameService.createGame(request);

      assertThat(result.getRating()).isEqualTo(0.0f);
    }
  }

  @Nested
  @DisplayName("updateGame()")
  class UpdateGame {

    @Test
    @DisplayName("updates only the non-null fields and persists changes")
    void updatesOnlyNonNullFields() {
      when(gameRepository.findById(gameId)).thenReturn(Optional.of(sampleModel));
      when(gameRepository.save(any(GameModel.class))).thenReturn(sampleModel);

      GameUpdateRequest request =
          GameUpdateRequest.builder().title("Hollow Knight: Silksong").build();

      GameEntity result = gameService.updateGame(gameId, request);

      verify(gameRepository).save(sampleModel);
      assertThat(sampleModel.getTitle()).isEqualTo("Hollow Knight: Silksong");
    }

    @Test
    @DisplayName("throws GameNotFoundException when updating a non-existent game")
    void throwsWhenGameNotFound() {
      UUID missing = UUID.randomUUID();
      when(gameRepository.findById(missing)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> gameService.updateGame(missing, new GameUpdateRequest()))
          .isInstanceOf(GameNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("deleteGame()")
  class DeleteGame {

    @Test
    @DisplayName("calls deleteById when the game exists")
    void deletesExistingGame() {
      when(gameRepository.existsById(gameId)).thenReturn(true);

      gameService.deleteGame(gameId);

      verify(gameRepository).deleteById(gameId);
    }

    @Test
    @DisplayName("throws GameNotFoundException when the game does not exist")
    void throwsWhenNotFound() {
      UUID missing = UUID.randomUUID();
      when(gameRepository.existsById(missing)).thenReturn(false);

      assertThatThrownBy(() -> gameService.deleteGame(missing))
          .isInstanceOf(GameNotFoundException.class);
      verify(gameRepository, never()).deleteById(any());
    }
  }

  @Nested
  @DisplayName("getRecentGames()")
  class GetRecentGames {

    @Test
    @DisplayName("returns the requested number of most recently created games")
    void returnsRecentGames() {
      when(gameRepository.findAll(any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(sampleModel)));

      List<GameEntity> result = gameService.getRecentGames(5);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getTitle()).isEqualTo("Hollow Knight");
    }
  }

  @Nested
  @DisplayName("getPopularGames()")
  class GetPopularGames {

    @Test
    @DisplayName("returns the requested number of highest-rated games")
    void returnsPopularGames() {
      when(gameRepository.findAll(any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(sampleModel)));

      List<GameEntity> result = gameService.getPopularGames(10);

      assertThat(result).hasSize(1);
      assertThat(result.get(0).getRating()).isEqualTo(9.5f);
    }
  }

  private GameModel buildGame(
      String title, String genre, String platform, String publisher, int year) {
    return GameModel.builder()
        .id(UUID.randomUUID())
        .title(title)
        .releaseYear(year)
        .publisher(publisher)
        .rating(7.0f)
        .genres(List.of(GenreModel.builder().id(UUID.randomUUID()).name(genre).build()))
        .platforms(List.of(PlatformModel.builder().id(UUID.randomUUID()).name(platform).build()))
        .tags(List.of())
        .build();
  }
}
