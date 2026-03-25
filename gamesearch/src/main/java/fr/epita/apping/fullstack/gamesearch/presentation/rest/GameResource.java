package fr.epita.apping.fullstack.gamesearch.presentation.rest;

import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.domain.service.GameService;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.GameDetailResponse;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.GameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameCreateRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameResource {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<Page<GameResponse>> getGames(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<GameResponse> result = gameService.searchGames(q, genre, platform, year, pageable)
                .map(GameConverter::toResponse);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailResponse> getGame(@PathVariable UUID id) {
        return ResponseEntity.ok(
                GameConverter.toDetailResponse(gameService.getGame(id), gameService.getSimilarGames(id))
        );
    }

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@RequestBody @Valid GameCreateRequest request) {
        GameResponse response = GameConverter.toResponse(gameService.createGame(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GameResponse> updateGame(@PathVariable UUID id,
                                                   @RequestBody @Valid GameUpdateRequest request) {
        return ResponseEntity.ok(GameConverter.toResponse(gameService.updateGame(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
