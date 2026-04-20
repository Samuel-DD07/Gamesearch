package fr.epita.apping.fullstack.gamesearch.domain.service;

import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.data.model.GenreModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PlatformModel;
import fr.epita.apping.fullstack.gamesearch.data.model.TagModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.*;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GenreEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PlatformEntity;
import fr.epita.apping.fullstack.gamesearch.exception.GameNotFoundException;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameCreateRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameUpdateRequest;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

  private final GameRepository gameRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final TagRepository tagRepository;

  public Page<GameEntity> searchGames(
      String title, String genre, String platform, Integer year, Pageable pageable) {
    Specification<GameModel> spec =
        Specification.where(GameSpecification.hasTitle(title))
            .and(GameSpecification.hasGenre(genre))
            .and(GameSpecification.hasPlatform(platform))
            .and(GameSpecification.hasReleaseYear(year));

    return gameRepository.findAll(spec, pageable).map(GameConverter::toEntity);
  }

  public GameEntity getGame(UUID id) {
    GameModel game = gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException(id));
    return GameConverter.toEntity(game);
  }

  public List<GameEntity> getSimilarGames(UUID gameId) {
    GameModel game = gameRepository.findById(gameId).orElse(null);
    if (game == null) return List.of();

    List<UUID> genreIds = game.getGenres().stream().map(g -> g.getId()).toList();
    List<UUID> platformIds = game.getPlatforms().stream().map(p -> p.getId()).toList();

    Set<GameModel> candidates = new HashSet<>();
    if (!genreIds.isEmpty()) {
      candidates.addAll(gameRepository.findBySharedGenres(gameId, genreIds));
    }
    if (!platformIds.isEmpty()) {
      candidates.addAll(gameRepository.findBySharedPlatforms(gameId, platformIds));
    }

    GameEntity gameEntity = GameConverter.toEntity(game);

    return candidates.stream()
        .map(GameConverter::toEntity)
        .sorted(Comparator.comparingInt(c -> -computeSimilarityScore(gameEntity, c)))
        .limit(5)
        .toList();
  }

  private int computeSimilarityScore(GameEntity game, GameEntity candidate) {
    int score = 0;

    Set<String> gameGenres =
        game.getGenres().stream().map(GenreEntity::getName).collect(Collectors.toSet());
    for (GenreEntity g : candidate.getGenres()) {
      if (gameGenres.contains(g.getName())) score += 2;
    }

    Set<String> gamePlatforms =
        game.getPlatforms().stream().map(PlatformEntity::getName).collect(Collectors.toSet());
    for (PlatformEntity p : candidate.getPlatforms()) {
      if (gamePlatforms.contains(p.getName())) score += 1;
    }

    if (game.getPublisher() != null && game.getPublisher().equals(candidate.getPublisher())) {
      score += 1;
    }

    if (game.getReleaseYear() != null
        && candidate.getReleaseYear() != null
        && Math.abs(game.getReleaseYear() - candidate.getReleaseYear()) <= 3) {
      score += 1;
    }

    return score;
  }

  @Transactional
  public GameEntity createGame(GameCreateRequest request) {
    GameModel game =
        GameModel.builder()
            .title(request.getTitle())
            .releaseYear(request.getReleaseYear())
            .publisher(request.getPublisher())
            .description(request.getDescription())
            .coverUrl(request.getCoverUrl())
            .rating(request.getRating() != null ? request.getRating() : 0.0f)
            .genres(resolveGenres(request.getGenres()))
            .platforms(resolvePlatforms(request.getPlatforms()))
            .tags(resolveTags(request.getTags()))
            .build();

    return GameConverter.toEntity(gameRepository.save(game));
  }

  @Transactional
  public GameEntity updateGame(UUID id, GameUpdateRequest request) {
    GameModel game = gameRepository.findById(id).orElseThrow(() -> new GameNotFoundException(id));

    if (request.getTitle() != null) {
      game.setTitle(request.getTitle());
    }
    if (request.getReleaseYear() != null) game.setReleaseYear(request.getReleaseYear());
    if (request.getPublisher() != null) game.setPublisher(request.getPublisher());
    if (request.getDescription() != null) {
      game.setDescription(request.getDescription());
    }
    if (request.getCoverUrl() != null) game.setCoverUrl(request.getCoverUrl());
    if (request.getRating() != null) {
      game.setRating(request.getRating());
    }
    if (request.getGenres() != null) {
      game.setGenres(resolveGenres(request.getGenres()));
    }
    if (request.getPlatforms() != null) game.setPlatforms(resolvePlatforms(request.getPlatforms()));
    if (request.getTags() != null) {
      game.setTags(resolveTags(request.getTags()));
    }

    return GameConverter.toEntity(gameRepository.save(game));
  }

  @Transactional
  public void deleteGame(UUID id) {
    if (!gameRepository.existsById(id)) {
      throw new GameNotFoundException(id);
    }
    gameRepository.deleteById(id);
  }

  public List<GameEntity> getRecentGames(int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
    return gameRepository.findAll(pageable).map(GameConverter::toEntity).getContent();
  }

  public List<GameEntity> getPopularGames(int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rating"));
    return gameRepository.findAll(pageable).map(GameConverter::toEntity).getContent();
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
    if (names == null) {
      return List.of();
    }
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
