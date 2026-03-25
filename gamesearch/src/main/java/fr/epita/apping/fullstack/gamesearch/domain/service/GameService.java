package fr.epita.apping.fullstack.gamesearch.domain.service;

import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.GameRepository;
import fr.epita.apping.fullstack.gamesearch.data.repository.GameSpecification;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GenreEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PlatformEntity;
import fr.epita.apping.fullstack.gamesearch.exception.GameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Page<GameEntity> searchGames(String title, String genre, String platform,
                                        Integer year, Pageable pageable) {
        Specification<GameModel> spec = Specification
                .where(GameSpecification.hasTitle(title))
                .and(GameSpecification.hasGenre(genre))
                .and(GameSpecification.hasPlatform(platform))
                .and(GameSpecification.hasReleaseYear(year));

        return gameRepository.findAll(spec, pageable)
                .map(GameConverter::toEntity);
    }

    public GameEntity getGame(UUID id) {
        GameModel game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
        return GameConverter.toEntity(game);
    }

    public List<GameEntity> getSimilarGames(UUID gameId) {
        GameModel game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return List.of();

        List<UUID> genreIds = game.getGenres().stream()
                .map(g -> g.getId()).toList();
        List<UUID> platformIds = game.getPlatforms().stream()
                .map(p -> p.getId()).toList();

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

        Set<String> gameGenres = game.getGenres().stream()
                .map(GenreEntity::getName).collect(Collectors.toSet());
        for (GenreEntity g : candidate.getGenres()) {
            if (gameGenres.contains(g.getName()))
                score += 2;
        }

        Set<String> gamePlatforms = game.getPlatforms().stream()
                .map(PlatformEntity::getName).collect(Collectors.toSet());
        for (PlatformEntity p : candidate.getPlatforms()) {
            if (gamePlatforms.contains(p.getName()))
                score += 1;
        }

        if (game.getPublisher() != null && game.getPublisher().equals(candidate.getPublisher())) {
            score += 1;
        }

        if (game.getReleaseYear() != null && candidate.getReleaseYear() != null
                && Math.abs(game.getReleaseYear() - candidate.getReleaseYear()) <= 3) {
            score += 1;
        }

        return score;
    }
}
