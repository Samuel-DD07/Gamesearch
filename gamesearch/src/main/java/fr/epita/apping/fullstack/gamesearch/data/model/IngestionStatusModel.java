package fr.epita.apping.fullstack.gamesearch.data.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "ingestion_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngestionStatusModel {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String externalId;

  @Column(nullable = false)
  private String partnerName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private IngestionStatus status;

  private String message;

  private String internalGameId;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }

  public enum IngestionStatus {
    PENDING,
    SUCCESS,
    ERROR
  }
}
