package fr.epita.apping.fullstack.gamesearch.domain.service;

import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.GameRepository;
import fr.epita.apping.fullstack.gamesearch.data.repository.GameSpecification;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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
        GameModel game =  gameRepository.findById(id).stream().findFirst().orElse(null);
        return GameConverter.toEntity(game);
    }
}
