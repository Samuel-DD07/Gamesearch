package fr.epita.apping.fullstack.gamesearch.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import fr.epita.apping.fullstack.gamesearch.data.model.*;
import fr.epita.apping.fullstack.gamesearch.data.repository.*;
import fr.epita.apping.fullstack.gamesearch.domain.entity.BulkIngestionEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PartnerEntity;
import fr.epita.apping.fullstack.gamesearch.kafka.dto.GameIngestionMessage;
import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionProducer;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameIngestionRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.PartnerRegisterRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("PartnerService")
class PartnerServiceTest {

  @Mock private PartnerRepository partnerRepository;
  @Mock private GameRepository gameRepository;
  @Mock private GenreRepository genreRepository;
  @Mock private PlatformRepository platformRepository;
  @Mock private TagRepository tagRepository;
  @Mock private ApiKeyService apiKeyService;
  @Mock private GameIngestionProducer ingestionProducer;
  @Mock private IngestionStatusRepository ingestionStatusRepository;

  @InjectMocks private PartnerService partnerService;

  private PartnerModel savedPartner;
  private GameIngestionRequest validIngestionRequest;

  @BeforeEach
  void setUp() {
    savedPartner =
        PartnerModel.builder()
            .id(UUID.randomUUID())
            .name("Ubisoft")
            .apiKeyHash("hashedkey")
            .active(true)
            .build();

    validIngestionRequest =
        GameIngestionRequest.builder()
            .gameId("ext-001")
            .title("Assassin's Creed")
            .releaseYear(2007)
            .genres(List.of("Action"))
            .platforms(List.of("PC"))
            .publisher("Ubisoft")
            .build();
  }

  @Nested
  @DisplayName("register()")
  class Register {

    @Test
    @DisplayName("generates an API key, hashes it, and returns an entity with the plain key")
    void returnsEntityWithPlainApiKey() {
      when(apiKeyService.generateApiKey()).thenReturn("gs_plaintextkey");
      when(apiKeyService.hash("gs_plaintextkey")).thenReturn("hashedvalue");
      when(partnerRepository.save(any(PartnerModel.class))).thenReturn(savedPartner);

      PartnerEntity result = partnerService.register(new PartnerRegisterRequest("Ubisoft"));

      assertThat(result.getPlainApiKey()).isEqualTo("gs_plaintextkey");
      assertThat(result.getName()).isEqualTo("Ubisoft");
      assertThat(result.getId()).isEqualTo(savedPartner.getId());
    }

    @Test
    @DisplayName("persists the partner with the hashed API key, not the plain text")
    void persistsHashedKeyNotPlainText() {
      when(apiKeyService.generateApiKey()).thenReturn("gs_plaintextkey");
      when(apiKeyService.hash("gs_plaintextkey")).thenReturn("sha256hash");
      when(partnerRepository.save(any(PartnerModel.class))).thenReturn(savedPartner);

      partnerService.register(new PartnerRegisterRequest("Ubisoft"));

      ArgumentCaptor<PartnerModel> captor = ArgumentCaptor.forClass(PartnerModel.class);
      verify(partnerRepository).save(captor.capture());
      assertThat(captor.getValue().getApiKeyHash()).isEqualTo("sha256hash");
    }

    @Test
    @DisplayName("creates the partner as active by default")
    void createsPartnerAsActive() {
      when(apiKeyService.generateApiKey()).thenReturn("gs_key");
      when(apiKeyService.hash(any())).thenReturn("hash");
      when(partnerRepository.save(any())).thenReturn(savedPartner);

      PartnerEntity result = partnerService.register(new PartnerRegisterRequest("EA"));

      assertThat(result.getActive()).isTrue();
    }
  }

  @Nested
  @DisplayName("submitGame()")
  class SubmitGame {

    @Test
    @DisplayName("saves a new game when no existing game matches the external ID")
    void savesNewGameWhenNoExistingMatch() {
      GenreModel genre = GenreModel.builder().id(UUID.randomUUID()).name("Action").build();
      PlatformModel platform = PlatformModel.builder().id(UUID.randomUUID()).name("PC").build();
      GameModel saved =
          GameModel.builder()
              .id(UUID.randomUUID())
              .title("Assassin's Creed")
              .releaseYear(2007)
              .genres(List.of(genre))
              .platforms(List.of(platform))
              .tags(List.of())
              .rating(0.0f)
              .build();

      when(partnerRepository.findByName("Ubisoft")).thenReturn(Optional.of(savedPartner));
      when(gameRepository.findByExternalId("ext-001")).thenReturn(Optional.empty());
      when(genreRepository.findByName("Action")).thenReturn(Optional.of(genre));
      when(platformRepository.findByName("PC")).thenReturn(Optional.of(platform));
      when(gameRepository.save(any(GameModel.class))).thenReturn(saved);

      GameEntity result = partnerService.submitGame(validIngestionRequest, "Ubisoft");

      assertThat(result.getTitle()).isEqualTo("Assassin's Creed");
      verify(gameRepository).save(any(GameModel.class));
    }

    @Test
    @DisplayName("updates the existing game when the external ID already exists")
    void updatesExistingGameWhenExternalIdMatches() {
      GameModel existing =
          GameModel.builder()
              .id(UUID.randomUUID())
              .title("Old Title")
              .releaseYear(2000)
              .genres(List.of())
              .platforms(List.of())
              .tags(List.of())
              .rating(0.0f)
              .build();

      GenreModel genre = GenreModel.builder().id(UUID.randomUUID()).name("Action").build();
      PlatformModel platform = PlatformModel.builder().id(UUID.randomUUID()).name("PC").build();

      when(partnerRepository.findByName("Ubisoft")).thenReturn(Optional.of(savedPartner));
      when(gameRepository.findByExternalId("ext-001")).thenReturn(Optional.of(existing));
      when(genreRepository.findByName("Action")).thenReturn(Optional.of(genre));
      when(platformRepository.findByName("PC")).thenReturn(Optional.of(platform));
      when(gameRepository.save(any(GameModel.class))).thenReturn(existing);

      partnerService.submitGame(validIngestionRequest, "Ubisoft");

      verify(gameRepository).save(existing);
      assertThat(existing.getTitle()).isEqualTo("Assassin's Creed");
    }
  }

  @Nested
  @DisplayName("enqueueGame()")
  class EnqueueGame {

    @Test
    @DisplayName("saves a PENDING status and sends a Kafka message")
    void savesPendingStatusAndSendsKafka() {
      IngestionStatusModel pendingStatus =
          IngestionStatusModel.builder()
              .id(UUID.randomUUID())
              .externalId("ext-001")
              .partnerName("Ubisoft")
              .status(IngestionStatusModel.IngestionStatus.PENDING)
              .message("Waiting for processing")
              .build();

      when(ingestionStatusRepository.save(any(IngestionStatusModel.class)))
          .thenReturn(pendingStatus);

      IngestionStatusModel result = partnerService.enqueueGame(validIngestionRequest, "Ubisoft");

      assertThat(result.getStatus()).isEqualTo(IngestionStatusModel.IngestionStatus.PENDING);
      verify(ingestionProducer).sendIngestionRequest(any(GameIngestionMessage.class));
    }

    @Test
    @DisplayName("the Kafka message contains the correct title and partner name")
    void kafkaMessageContainsCorrectData() {
      IngestionStatusModel pending =
          IngestionStatusModel.builder()
              .id(UUID.randomUUID())
              .externalId("ext-001")
              .partnerName("Ubisoft")
              .status(IngestionStatusModel.IngestionStatus.PENDING)
              .build();

      when(ingestionStatusRepository.save(any())).thenReturn(pending);

      partnerService.enqueueGame(validIngestionRequest, "Ubisoft");

      ArgumentCaptor<GameIngestionMessage> captor =
          ArgumentCaptor.forClass(GameIngestionMessage.class);
      verify(ingestionProducer).sendIngestionRequest(captor.capture());

      GameIngestionMessage sent = captor.getValue();
      assertThat(sent.getTitle()).isEqualTo("Assassin's Creed");
      assertThat(sent.getPartnerName()).isEqualTo("Ubisoft");
      assertThat(sent.getPartnerApiKey()).isNull();
    }
  }

  @Nested
  @DisplayName("getIngestionStatus()")
  class GetIngestionStatus {

    @Test
    @DisplayName("returns the status model when found")
    void returnsStatusWhenFound() {
      IngestionStatusModel status =
          IngestionStatusModel.builder()
              .externalId("ext-001")
              .partnerName("Ubisoft")
              .status(IngestionStatusModel.IngestionStatus.SUCCESS)
              .build();

      when(ingestionStatusRepository.findByExternalIdAndPartnerName("ext-001", "Ubisoft"))
          .thenReturn(Optional.of(status));

      IngestionStatusModel result = partnerService.getIngestionStatus("ext-001", "Ubisoft");

      assertThat(result.getStatus()).isEqualTo(IngestionStatusModel.IngestionStatus.SUCCESS);
    }

    @Test
    @DisplayName("throws RuntimeException when the status is not found")
    void throwsWhenNotFound() {
      when(ingestionStatusRepository.findByExternalIdAndPartnerName("unknown", "Ubisoft"))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> partnerService.getIngestionStatus("unknown", "Ubisoft"))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("unknown");
    }
  }

  @Nested
  @DisplayName("bulkImport()")
  class BulkImport {

    @Test
    @DisplayName("parses a valid JSON file and returns the correct totals")
    void parsesJsonAndReturnsCorrectTotals() throws Exception {
      String json =
          """
          [
            {
              "gameId": "g1",
              "title": "Game One",
              "releaseYear": 2020,
              "genres": ["RPG"],
              "platforms": ["PC"]
            },
            {
              "gameId": "g2",
              "title": "Game Two",
              "releaseYear": 2021,
              "genres": ["Action"],
              "platforms": ["PS5"]
            }
          ]
          """;

      MockMultipartFile file =
          new MockMultipartFile("file", "games.json", "application/json", json.getBytes());

      GenreModel rpg = GenreModel.builder().id(UUID.randomUUID()).name("RPG").build();
      GenreModel action = GenreModel.builder().id(UUID.randomUUID()).name("Action").build();
      PlatformModel pc = PlatformModel.builder().id(UUID.randomUUID()).name("PC").build();
      PlatformModel ps5 = PlatformModel.builder().id(UUID.randomUUID()).name("PS5").build();

      when(partnerRepository.findByName("Ubisoft")).thenReturn(Optional.of(savedPartner));
      when(gameRepository.findByExternalId(any())).thenReturn(Optional.empty());
      when(genreRepository.findByName("RPG")).thenReturn(Optional.of(rpg));
      when(genreRepository.findByName("Action")).thenReturn(Optional.of(action));
      when(platformRepository.findByName("PC")).thenReturn(Optional.of(pc));
      when(platformRepository.findByName("PS5")).thenReturn(Optional.of(ps5));
      when(gameRepository.save(any(GameModel.class)))
          .thenAnswer(
              inv -> {
                GameModel m = inv.getArgument(0);
                m =
                    GameModel.builder()
                        .id(UUID.randomUUID())
                        .title(m.getTitle())
                        .releaseYear(m.getReleaseYear())
                        .genres(m.getGenres())
                        .platforms(m.getPlatforms())
                        .tags(List.of())
                        .rating(m.getRating() != null ? m.getRating() : 0.0f)
                        .build();
                return m;
              });

      BulkIngestionEntity result = partnerService.bulkImport(file, "Ubisoft");

      assertThat(result.total()).isEqualTo(2);
      assertThat(result.successful()).isEqualTo(2);
      assertThat(result.failed()).isEqualTo(0);
      assertThat(result.errors()).isEmpty();
    }

    @Test
    @DisplayName("parses a valid CSV file and returns the correct totals")
    void parsesCsvAndReturnsCorrectTotals() throws Exception {
      String csv =
          "title,releaseYear,genres,platforms,publisher,description,coverUrl,rating,tags\n"
              + "Celeste,2018,Platformer,PC,Maddy Makes Games,,, 9.0,\n";

      MockMultipartFile file =
          new MockMultipartFile("file", "games.csv", "text/csv", csv.getBytes());

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

      when(partnerRepository.findByName("Ubisoft")).thenReturn(Optional.of(savedPartner));
      when(genreRepository.findByName("Platformer")).thenReturn(Optional.of(genre));
      when(platformRepository.findByName("PC")).thenReturn(Optional.of(platform));
      when(gameRepository.save(any(GameModel.class))).thenReturn(saved);

      BulkIngestionEntity result = partnerService.bulkImport(file, "Ubisoft");

      assertThat(result.total()).isEqualTo(1);
      assertThat(result.successful()).isEqualTo(1);
      assertThat(result.failed()).isEqualTo(0);
    }

    @Test
    @DisplayName("throws IllegalArgumentException for unsupported file formats")
    void throwsForUnsupportedFormat() {
      MockMultipartFile file =
          new MockMultipartFile("file", "games.xml", "application/xml", "<xml/>".getBytes());

      assertThatThrownBy(() -> partnerService.bulkImport(file, "Ubisoft"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unsupported file format");
    }

    @Test
    @DisplayName("counts individual game failures without aborting the entire import")
    void countsFailuresWithoutAbortingImport() throws Exception {
      String json =
          """
          [
            {
              "gameId": "ok-1",
              "title": "Good Game",
              "releaseYear": 2020,
              "genres": ["RPG"],
              "platforms": ["PC"]
            },
            {
              "gameId": "bad-1",
              "title": "Bad Game",
              "releaseYear": 2021,
              "genres": ["Action"],
              "platforms": ["PS5"]
            }
          ]
          """;

      MockMultipartFile file =
          new MockMultipartFile("file", "games.json", "application/json", json.getBytes());

      GenreModel rpg = GenreModel.builder().id(UUID.randomUUID()).name("RPG").build();
      GenreModel action = GenreModel.builder().id(UUID.randomUUID()).name("Action").build();
      PlatformModel pc = PlatformModel.builder().id(UUID.randomUUID()).name("PC").build();
      PlatformModel ps5 = PlatformModel.builder().id(UUID.randomUUID()).name("PS5").build();

      GameModel goodGame =
          GameModel.builder()
              .id(UUID.randomUUID())
              .title("Good Game")
              .releaseYear(2020)
              .genres(List.of(rpg))
              .platforms(List.of(pc))
              .tags(List.of())
              .rating(0.0f)
              .build();

      when(partnerRepository.findByName("Ubisoft")).thenReturn(Optional.of(savedPartner));
      when(gameRepository.findByExternalId("ok-1")).thenReturn(Optional.empty());
      when(gameRepository.findByExternalId("bad-1")).thenReturn(Optional.empty());
      when(genreRepository.findByName("RPG")).thenReturn(Optional.of(rpg));
      when(genreRepository.findByName("Action")).thenReturn(Optional.of(action));
      when(platformRepository.findByName("PC")).thenReturn(Optional.of(pc));
      when(platformRepository.findByName("PS5")).thenReturn(Optional.of(ps5));

      when(gameRepository.save(any(GameModel.class)))
          .thenReturn(goodGame)
          .thenThrow(new RuntimeException("DB constraint violation"));

      BulkIngestionEntity result = partnerService.bulkImport(file, "Ubisoft");

      assertThat(result.total()).isEqualTo(2);
      assertThat(result.successful()).isEqualTo(1);
      assertThat(result.failed()).isEqualTo(1);
      assertThat(result.errors()).hasSize(1);
    }
  }
}
