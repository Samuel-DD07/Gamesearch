package fr.epita.apping.fullstack.gamesearch.presentation.rest;

import fr.epita.apping.fullstack.gamesearch.data.model.IngestionStatusModel;
import fr.epita.apping.fullstack.gamesearch.domain.entity.BulkIngestionEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PartnerEntity;
import fr.epita.apping.fullstack.gamesearch.domain.service.PartnerService;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameIngestionRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.PartnerRegisterRequest;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.BulkIngestionResponse;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.IngestionStatusResponse;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.PartnerRegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerResource {

  private final PartnerService partnerService;

  @PostMapping("/register")
  public ResponseEntity<PartnerRegisterResponse> register(
      @RequestBody @Valid PartnerRegisterRequest request) {
    PartnerEntity entity = partnerService.register(request);
    PartnerRegisterResponse response =
        new PartnerRegisterResponse(entity.getId(), entity.getName(), entity.getPlainApiKey());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/games")
  public ResponseEntity<IngestionStatusResponse> submitGame(
      @RequestBody @Valid GameIngestionRequest request, Authentication authentication) {
    IngestionStatusModel status = partnerService.enqueueGame(request, authentication.getName());
    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(
            IngestionStatusResponse.builder()
                .gameId(status.getExternalId())
                .status(status.getStatus())
                .message(status.getMessage())
                .createdAt(status.getCreatedAt())
                .build());
  }

  @GetMapping("/ingestions/{gameId}")
  public ResponseEntity<IngestionStatusResponse> getIngestionStatus(
      @PathVariable String gameId, Authentication authentication) {
    IngestionStatusModel status =
        partnerService.getIngestionStatus(gameId, authentication.getName());
    return ResponseEntity.ok(
        IngestionStatusResponse.builder()
            .gameId(status.getExternalId())
            .status(status.getStatus())
            .message(status.getMessage())
            .internalGameId(status.getInternalGameId())
            .createdAt(status.getCreatedAt())
            .build());
  }

  @PostMapping("/games/bulk")
  public ResponseEntity<BulkIngestionResponse> bulkImport(
      @RequestParam("file") MultipartFile file, Authentication authentication) {
    BulkIngestionEntity entity = partnerService.bulkImport(file, authentication.getName());
    BulkIngestionResponse response =
        new BulkIngestionResponse(
            entity.total(), entity.successful(), entity.failed(), entity.errors());
    return ResponseEntity.ok(response);
  }
}
