package fr.epita.apping.fullstack.gamesearch.domain.entity;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformEntity {

  private UUID id;
  private String name;
}
