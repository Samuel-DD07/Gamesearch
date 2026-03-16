package fr.epita.apping.fullstack.gamesearch.presentation.rest;

import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.domain.service.GameService;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.GameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GameResource {

    private final GameService gameService;

    @GetMapping("/games")
    public Page<GameResponse> getGames(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return gameService.searchGames(q, genre, platform, year, pageable)
                          .map(GameConverter::toResponse);
    }

    @GetMapping("/games/{id}")
    public GameResponse getGame(@PathVariable UUID id) {
        return GameConverter.toResponse(gameService.getGame(id));
    }
}
