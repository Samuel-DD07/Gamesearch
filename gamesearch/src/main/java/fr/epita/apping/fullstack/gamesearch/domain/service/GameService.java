package fr.epita.apping.fullstack.gamesearch.domain.service;

import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.data.model.GenreModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PlatformModel;
import fr.epita.apping.fullstack.gamesearch.data.model.TagModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.GameRepository;
import fr.epita.apping.fullstack.gamesearch.data.repository.GenreRepository;
import fr.epita.apping.fullstack.gamesearch.data.repository.PlatformRepository;
import fr.epita.apping.fullstack.gamesearch.data.repository.TagRepository;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.exception.GameNotFoundException;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameCreateRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final TagRepository tagRepository;

    @Transactional
    public GameEntity createGame(GameCreateRequest request) {
        GameModel game = GameModel.builder()
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
        GameModel game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        if (request.getTitle() != null) {
            game.setTitle(request.getTitle());
        }
        if (request.getReleaseYear() != null)
            game.setReleaseYear(request.getReleaseYear());
        if (request.getPublisher() != null)
            game.setPublisher(request.getPublisher());
        if (request.getDescription() != null) {
            game.setDescription(request.getDescription());
        }
        if (request.getCoverUrl() != null)
            game.setCoverUrl(request.getCoverUrl());
        if (request.getRating() != null) {
            game.setRating(request.getRating());
        }
        if (request.getGenres() != null) {
            game.setGenres(resolveGenres(request.getGenres()));
        }
        if (request.getPlatforms() != null)
            game.setPlatforms(resolvePlatforms(request.getPlatforms()));
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


    private List<GenreModel> resolveGenres(List<String> names) {
        if (names == null)
            return List.of();
        return names.stream()
                .map(name -> genreRepository.findByName(name)
                        .orElseGet(() -> genreRepository.save(GenreModel.builder().name(name).build())))
                .toList();
    }

    private List<PlatformModel> resolvePlatforms(List<String> names) {
        if (names == null) {
            return List.of();
        }
        return names.stream()
                .map(name -> platformRepository.findByName(name)
                        .orElseGet(() -> platformRepository.save(PlatformModel.builder().name(name).build())))
                .toList();
    }

    private List<TagModel> resolveTags(List<String> names) {
        if (names == null)
            return List.of();
        return names.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> tagRepository.save(TagModel.builder().name(name).build())))
                .toList();
    }
}
