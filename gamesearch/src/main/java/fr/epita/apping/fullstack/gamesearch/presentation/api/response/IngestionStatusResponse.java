package fr.epita.apping.fullstack.gamesearch.presentation.api.response;

import fr.epita.apping.fullstack.gamesearch.data.model.IngestionStatusModel;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IngestionStatusResponse {
  private String gameId;
  private IngestionStatusModel.IngestionStatus status;
  private String message;
  private String internalGameId;
  private LocalDateTime createdAt;
}
