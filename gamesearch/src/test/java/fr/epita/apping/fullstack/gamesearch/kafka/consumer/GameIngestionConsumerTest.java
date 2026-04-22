package fr.epita.apping.fullstack.gamesearch.kafka.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import fr.epita.apping.fullstack.gamesearch.data.model.IngestionStatusModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PartnerModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.IngestionStatusRepository;
import fr.epita.apping.fullstack.gamesearch.data.repository.PartnerRepository;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.service.ApiKeyService;
import fr.epita.apping.fullstack.gamesearch.domain.service.PartnerService;
import fr.epita.apping.fullstack.gamesearch.kafka.dto.GameIngestionMessage;
import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionStatusProducer;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameIngestionRequest;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("GameIngestionStatusConsumer")
class GameIngestionConsumerTest {

  @Mock private PartnerService partnerService;
  @Mock private PartnerRepository partnerRepository;
  @Mock private ApiKeyService apiKeyService;
  @Mock private GameIngestionStatusProducer statusProducer;
  @Mock private IngestionStatusRepository ingestionStatusRepository;

  @InjectMocks private GameIngestionStatusConsumer consumer;

  private GameIngestionMessage validMessage;
  private IngestionStatusModel pendingStatus;

  @BeforeEach
  void setUp() {
    validMessage =
        GameIngestionMessage.builder()
            .gameId("ext-001")
            .title("The Witcher 3")
            .releaseYear(2015)
            .genres(List.of("RPG"))
            .platforms(List.of("PC"))
            .publisher("CD Projekt Red")
            .partnerName("CDProject")
            .partnerApiKey("gs_somekey")
            .build();

    pendingStatus =
        IngestionStatusModel.builder()
            .id(UUID.randomUUID())
            .externalId("ext-001")
            .partnerName("CDProject")
            .status(IngestionStatusModel.IngestionStatus.PENDING)
            .build();
  }

  @Nested
  @DisplayName("consume() — happy path")
  class ConsumeHappyPath {

    @Test
    @DisplayName("calls submitGame and sends a SUCCESS status message when processing succeeds")
    void sendsSuccessStatusOnSuccess() {
      GameEntity savedGame =
          GameEntity.builder().id(UUID.randomUUID()).title("The Witcher 3").build();

      when(partnerService.submitGame(any(GameIngestionRequest.class), any())).thenReturn(savedGame);

      consumer.consume(validMessage);

      verify(statusProducer)
          .sendStatus(
              argThat(
                  msg -> "SUCCESS".equals(msg.getStatus()) && "ext-001".equals(msg.getGameId())));
    }
  }

  @Nested
  @DisplayName("consume() — missing required fields")
  class ConsumeMissingFields {

    @Test
    @DisplayName("sends MISSING_DATA status and skips processing when title is absent")
    void sendsMissingDataWhenTitleIsNull() {
      GameIngestionMessage noTitle =
          GameIngestionMessage.builder()
              .gameId("ext-002")
              .releaseYear(2020)
              .genres(List.of("Action"))
              .platforms(List.of("PC"))
              .partnerName("Partner")
              .build();

      consumer.consume(noTitle);

      verify(partnerService, never()).submitGame(any(), any());
      verify(statusProducer).sendStatus(argThat(msg -> "MISSING_DATA".equals(msg.getStatus())));
    }

    @Test
    @DisplayName("sends MISSING_DATA status and skips processing when genres list is empty")
    void sendsMissingDataWhenGenresIsEmpty() {
      GameIngestionMessage noGenres =
          GameIngestionMessage.builder()
              .gameId("ext-003")
              .title("Some Game")
              .releaseYear(2020)
              .genres(List.of())
              .platforms(List.of("PC"))
              .partnerName("Partner")
              .build();

      consumer.consume(noGenres);

      verify(partnerService, never()).submitGame(any(), any());
      verify(statusProducer).sendStatus(argThat(msg -> "MISSING_DATA".equals(msg.getStatus())));
    }

    @Test
    @DisplayName("sends MISSING_DATA status and skips processing when platforms list is empty")
    void sendsMissingDataWhenPlatformsIsEmpty() {
      GameIngestionMessage noPlatforms =
          GameIngestionMessage.builder()
              .gameId("ext-004")
              .title("Some Game")
              .releaseYear(2020)
              .genres(List.of("RPG"))
              .platforms(List.of())
              .partnerName("Partner")
              .build();

      consumer.consume(noPlatforms);

      verify(partnerService, never()).submitGame(any(), any());
      verify(statusProducer).sendStatus(argThat(msg -> "MISSING_DATA".equals(msg.getStatus())));
    }

    @Test
    @DisplayName("sends MISSING_DATA status when release year is missing")
    void sendsMissingDataWhenReleaseYearIsNull() {
      GameIngestionMessage noYear =
          GameIngestionMessage.builder()
              .gameId("ext-005")
              .title("Some Game")
              .releaseYear(null)
              .genres(List.of("RPG"))
              .platforms(List.of("PC"))
              .partnerName("Partner")
              .build();

      consumer.consume(noYear);

      verify(partnerService, never()).submitGame(any(), any());
      verify(statusProducer).sendStatus(argThat(msg -> "MISSING_DATA".equals(msg.getStatus())));
    }
  }

  @Nested
  @DisplayName("consume() — processing failure")
  class ConsumeError {

    @Test
    @DisplayName("sends an ERROR status message when submitGame throws an exception")
    void sendsErrorStatusOnException() {
      when(partnerService.submitGame(any(GameIngestionRequest.class), any()))
          .thenThrow(new RuntimeException("DB error"));

      consumer.consume(validMessage);

      verify(statusProducer)
          .sendStatus(
              argThat(
                  msg -> "ERROR".equals(msg.getStatus()) && msg.getMessage().contains("DB error")));
    }

    @Test
    @DisplayName("never calls submitGame when the message has multiple missing fields")
    void neverCallsSubmitGameOnMultipleMissingFields() {
      GameIngestionMessage empty = GameIngestionMessage.builder().gameId("ext-006").build();

      consumer.consume(empty);

      verify(partnerService, never()).submitGame(any(), any());
    }
  }

  @Nested
  @DisplayName("partner name resolution")
  class PartnerNameResolution {

    @Test
    @DisplayName("uses partnerName from message when it is provided")
    void usesPartnerNameFromMessage() {
      GameEntity savedGame =
          GameEntity.builder().id(UUID.randomUUID()).title("The Witcher 3").build();
      when(partnerService.submitGame(any(GameIngestionRequest.class), eq("CDProject")))
          .thenReturn(savedGame);

      consumer.consume(validMessage);

      verify(partnerService).submitGame(any(GameIngestionRequest.class), eq("CDProject"));
    }

    @Test
    @DisplayName("resolves partner name from API key hash when partnerName is absent")
    void resolvesPartnerNameFromApiKeyWhenAbsent() {
      GameIngestionMessage noPartnerName =
          GameIngestionMessage.builder()
              .gameId("ext-007")
              .title("Game X")
              .releaseYear(2020)
              .genres(List.of("RPG"))
              .platforms(List.of("PC"))
              .partnerApiKey("gs_thekey")
              .build();

      PartnerModel partner =
          PartnerModel.builder()
              .id(UUID.randomUUID())
              .name("ResolvedPartner")
              .apiKeyHash("hash")
              .build();

      when(apiKeyService.hash("gs_thekey")).thenReturn("hash");
      when(partnerRepository.findByApiKeyHash("hash")).thenReturn(Optional.of(partner));
      when(partnerService.submitGame(any(GameIngestionRequest.class), eq("ResolvedPartner")))
          .thenReturn(GameEntity.builder().id(UUID.randomUUID()).title("Game X").build());

      consumer.consume(noPartnerName);

      verify(partnerService).submitGame(any(GameIngestionRequest.class), eq("ResolvedPartner"));
    }
  }
}
