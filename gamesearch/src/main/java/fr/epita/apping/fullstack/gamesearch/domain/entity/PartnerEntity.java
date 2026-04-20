package fr.epita.apping.fullstack.gamesearch.domain.entity;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerEntity {

  private UUID id;
  private String name;
  private String apiKeyHash;
  private Boolean active;
  private String plainApiKey;
}
