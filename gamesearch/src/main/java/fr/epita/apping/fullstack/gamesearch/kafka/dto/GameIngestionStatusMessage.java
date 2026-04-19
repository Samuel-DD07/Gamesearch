package fr.epita.apping.fullstack.gamesearch.kafka.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameIngestionStatusMessage {

  private String gameId;

  private String status;

  private String message;

  private String internalGameId;
}
