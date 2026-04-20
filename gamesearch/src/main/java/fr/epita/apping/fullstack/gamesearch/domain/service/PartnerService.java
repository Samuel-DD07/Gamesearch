package fr.epita.apping.fullstack.gamesearch.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.data.model.*;
import fr.epita.apping.fullstack.gamesearch.data.repository.*;
import fr.epita.apping.fullstack.gamesearch.domain.entity.BulkIngestionEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PartnerEntity;
import fr.epita.apping.fullstack.gamesearch.kafka.dto.GameIngestionMessage;
import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionProducer;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameIngestionRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.PartnerRegisterRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {

  private final PartnerRepository partnerRepository;
  private final GameRepository gameRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final TagRepository tagRepository;
  private final ApiKeyService apiKeyService;
  private final GameIngestionProducer ingestionProducer;
  private final IngestionStatusRepository ingestionStatusRepository;

  @Transactional
  public PartnerEntity register(PartnerRegisterRequest request) {
    String apiKey = apiKeyService.generateApiKey();
    String apiKeyHash = apiKeyService.hash(apiKey);

    PartnerModel partner =
        PartnerModel.builder().name(request.name()).apiKeyHash(apiKeyHash).active(true).build();

    PartnerModel saved = partnerRepository.save(partner);

    return PartnerEntity.builder()
        .id(saved.getId())
        .name(saved.getName())
        .active(saved.getActive())
        .plainApiKey(apiKey)
        .build();
  }

  @Transactional
  public GameEntity submitGame(GameIngestionRequest request, String partnerName) {
    PartnerModel partner = partnerRepository.findByName(partnerName).orElse(null);

    GameModel game = null;
    if (request.getGameId() != null) {
      game = gameRepository.findByExternalId(request.getGameId()).orElse(null);
    }

    if (game == null) {
      game = new GameModel();
    }

    game.setExternalId(request.getGameId());
    game.setTitle(request.getTitle());
    game.setReleaseYear(request.getReleaseYear());
    game.setPublisher(request.getPublisher());
    game.setDescription(request.getDescription());
    game.setCoverUrl(request.getCoverUrl());
    game.setRating(request.getRating() != null ? request.getRating() : 0.0f);
    game.setGenres(resolveGenres(request.getGenres()));
    game.setPlatforms(resolvePlatforms(request.getPlatforms()));
    game.setTags(resolveTags(request.getTags()));
    game.setPartner(partner);

    return GameConverter.toEntity(gameRepository.save(game));
  }

  @Transactional
  public IngestionStatusModel enqueueGame(GameIngestionRequest request, String partnerName) {
    log.info(
        ">>> [KAFKA PROD] Enqueuing game ingestion for {}: {}", partnerName, request.getTitle());

    IngestionStatusModel status =
        IngestionStatusModel.builder()
            .externalId(request.getGameId())
            .partnerName(partnerName)
            .status(IngestionStatusModel.IngestionStatus.PENDING)
            .message("Waiting for processing")
            .build();
    status = ingestionStatusRepository.save(status);

    GameIngestionMessage message =
        GameIngestionMessage.builder()
            .gameId(request.getGameId())
            .title(request.getTitle())
            .releaseYear(request.getReleaseYear())
            .genres(request.getGenres())
            .platforms(request.getPlatforms())
            .publisher(request.getPublisher())
            .description(request.getDescription())
            .coverUrl(request.getCoverUrl())
            .rating(request.getRating())
            .tags(request.getTags())
            .partnerName(partnerName)
            .partnerApiKey(null)
            .build();

    ingestionProducer.sendIngestionRequest(message);

    return status;
  }

  public IngestionStatusModel getIngestionStatus(String externalId, String partnerName) {
    return ingestionStatusRepository
        .findByExternalIdAndPartnerName(externalId, partnerName)
        .orElseThrow(
            () -> new RuntimeException("Ingestion status not found for ID: " + externalId));
  }

  @Transactional
  public BulkIngestionEntity bulkImport(MultipartFile file, String partnerName) {
    String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
    List<GameIngestionRequest> requests;

    try {
      if (filename.endsWith(".json")) {
        requests = parseJson(file);
      } else if (filename.endsWith(".csv")) {
        requests = parseCsv(file);
      } else {
        throw new IllegalArgumentException("Unsupported file format. Use .json or .csv");
      }
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to parse file: " + e.getMessage());
    }

    int total = requests.size();
    int successful = 0;
    List<String> errors = new ArrayList<>();

    for (int i = 0; i < requests.size(); i++) {
      try {
        submitGame(requests.get(i), partnerName);
        successful++;
      } catch (Exception e) {
        errors.add("Entry " + (i + 1) + ": " + e.getMessage());
      }
    }

    return new BulkIngestionEntity(total, successful, total - successful, errors);
  }

  private List<GameIngestionRequest> parseJson(MultipartFile file) throws Exception {
    return new ObjectMapper().readValue(file.getInputStream(), new TypeReference<>() {});
  }

  private List<GameIngestionRequest> parseCsv(MultipartFile file) throws Exception {
    List<GameIngestionRequest> result = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

      String headerLine = reader.readLine();
      if (headerLine == null) return result;

      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isBlank()) continue;
        String[] cols = line.split(",", -1);

        result.add(
            GameIngestionRequest.builder()
                .title(col(cols, 0))
                .releaseYear(
                    cols.length > 1 && !cols[1].isBlank() ? Integer.parseInt(cols[1].trim()) : null)
                .genres(splitPipe(col(cols, 2)))
                .platforms(splitPipe(col(cols, 3)))
                .publisher(col(cols, 4))
                .description(col(cols, 5))
                .coverUrl(col(cols, 6))
                .rating(
                    cols.length > 7 && !cols[7].isBlank() ? Float.parseFloat(cols[7].trim()) : null)
                .tags(splitPipe(col(cols, 8)))
                .build());
      }
    }
    return result;
  }

  private String col(String[] cols, int index) {
    if (index >= cols.length) return null;
    String val = cols[index].trim();
    return val.isBlank() ? null : val;
  }

  private List<String> splitPipe(String value) {
    if (value == null || value.isBlank()) return List.of();
    return Arrays.stream(value.split("\\|")).map(String::trim).filter(s -> !s.isBlank()).toList();
  }

  private List<GenreModel> resolveGenres(List<String> names) {
    if (names == null) return List.of();
    return names.stream()
        .map(
            name ->
                genreRepository
                    .findByName(name)
                    .orElseGet(() -> genreRepository.save(GenreModel.builder().name(name).build())))
        .toList();
  }

  private List<PlatformModel> resolvePlatforms(List<String> names) {
    if (names == null) return List.of();
    return names.stream()
        .map(
            name ->
                platformRepository
                    .findByName(name)
                    .orElseGet(
                        () -> platformRepository.save(PlatformModel.builder().name(name).build())))
        .toList();
  }

  private List<TagModel> resolveTags(List<String> names) {
    if (names == null) return List.of();
    return names.stream()
        .map(
            name ->
                tagRepository
                    .findByName(name)
                    .orElseGet(() -> tagRepository.save(TagModel.builder().name(name).build())))
        .toList();
  }
}
