package fr.epita.apping.fullstack.gamesearch.presentation.rest;

import fr.epita.apping.fullstack.gamesearch.converter.GameConverter;
import fr.epita.apping.fullstack.gamesearch.domain.service.GameService;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameCreateRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameUpdateRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.GameResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameResource {

    private final GameService gameService;

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
