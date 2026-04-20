package fr.epita.apping.fullstack.gamesearch.kafka.consumer;

import fr.epita.apping.fullstack.gamesearch.config.KafkaConfig;
import fr.epita.apping.fullstack.gamesearch.data.model.IngestionStatusModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PartnerModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.IngestionStatusRepository;
import fr.epita.apping.fullstack.gamesearch.data.repository.PartnerRepository;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.service.ApiKeyService;
import fr.epita.apping.fullstack.gamesearch.domain.service.PartnerService;
import fr.epita.apping.fullstack.gamesearch.kafka.dto.GameIngestionMessage;
import fr.epita.apping.fullstack.gamesearch.kafka.dto.GameIngestionStatusMessage;
import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionStatusProducer;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameIngestionRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameIngestionStatusConsumer {

  private final PartnerService partnerService;
  private final PartnerRepository partnerRepository;
  private final ApiKeyService apiKeyService;
  private final GameIngestionStatusProducer statusProducer;
  private final IngestionStatusRepository ingestionStatusRepository;

  @KafkaListener(topics = KafkaConfig.GAME_INGESTION_TOPIC, groupId = "gamesearch-group")
  public void consume(GameIngestionMessage message) {
    log.info(
        "Received game ingestion message for gameId={} title={}",
        message.getGameId(),
        message.getTitle());

    List<String> missingFields = validateRequiredFields(message);
    if (!missingFields.isEmpty()) {
      log.warn("Missing required fields: {}", missingFields);
      updateStatusInDb(
          message.getGameId(),
          resolvePartnerName(message.getPartnerApiKey()),
          IngestionStatusModel.IngestionStatus.ERROR,
          "Missing required fields: " + missingFields,
          null);
      statusProducer.sendStatus(
          GameIngestionStatusMessage.builder()
              .gameId(message.getGameId())
              .status("MISSING_DATA")
              .message("Missing required fields: " + missingFields)
              .build());
      return;
    }

    String partnerApiKeyToken = message.getPartnerApiKey();
    String partnerName =
        message.getPartnerName() != null
            ? message.getPartnerName()
            : resolvePartnerName(partnerApiKeyToken);

    try {
      GameIngestionRequest request = toIngestionRequest(message);
      GameEntity saved = partnerService.submitGame(request, partnerName);

      updateStatusInDb(
          message.getGameId(),
          partnerName,
          IngestionStatusModel.IngestionStatus.SUCCESS,
          "Game indexed successfully",
          saved.getId().toString());

      statusProducer.sendStatus(
          GameIngestionStatusMessage.builder()
              .gameId(message.getGameId())
              .status("SUCCESS")
              .message("Game indexed successfully")
              .internalGameId(saved.getId() != null ? saved.getId().toString() : null)
              .build());

      log.info("Game indexed successfully: internalId={}", saved.getId());
    } catch (Exception e) {
      log.error(
          "Failed to process game ingestion for gameId={}: {}",
          message.getGameId(),
          e.getMessage());
      updateStatusInDb(
          message.getGameId(),
          partnerName,
          IngestionStatusModel.IngestionStatus.ERROR,
          "Ingestion failed: " + e.getMessage(),
          null);
      statusProducer.sendStatus(
          GameIngestionStatusMessage.builder()
              .gameId(message.getGameId())
              .status("ERROR")
              .message("Ingestion failed: " + e.getMessage())
              .build());
    }
  }

  private void updateStatusInDb(
      String externalId,
      String partnerName,
      IngestionStatusModel.IngestionStatus status,
      String message,
      String internalId) {
    ingestionStatusRepository
        .findByExternalIdAndPartnerName(externalId, partnerName)
        .ifPresent(
            s -> {
              s.setStatus(status);
              s.setMessage(message);
              s.setInternalGameId(internalId);
              ingestionStatusRepository.save(s);
            });
  }

  private List<String> validateRequiredFields(GameIngestionMessage message) {
    List<String> missing = new ArrayList<>();
    if (message.getTitle() == null || message.getTitle().isBlank()) {
      missing.add("title");
    }
    if (message.getReleaseYear() == null) {
      missing.add("releaseYear");
    }
    if (message.getGenres() == null || message.getGenres().isEmpty()) {
      missing.add("genres");
    }
    if (message.getPlatforms() == null || message.getPlatforms().isEmpty()) {
      missing.add("platforms");
    }
    return missing;
  }

  private String resolvePartnerName(String partnerApiKey) {
    if (partnerApiKey == null || partnerApiKey.isBlank()) {
      return null;
    }
    String hash = apiKeyService.hash(partnerApiKey);
    return partnerRepository.findByApiKeyHash(hash).map(PartnerModel::getName).orElse(null);
  }

  private GameIngestionRequest toIngestionRequest(GameIngestionMessage message) {
    return GameIngestionRequest.builder()
        .gameId(message.getGameId())
        .title(message.getTitle())
        .releaseYear(message.getReleaseYear())
        .genres(message.getGenres())
        .platforms(message.getPlatforms())
        .publisher(message.getPublisher())
        .description(message.getDescription())
        .coverUrl(message.getCoverUrl())
        .rating(message.getRating())
        .tags(message.getTags())
        .build();
  }
}
