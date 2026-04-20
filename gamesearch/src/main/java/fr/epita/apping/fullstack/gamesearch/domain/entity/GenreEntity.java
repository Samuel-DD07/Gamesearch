package fr.epita.apping.fullstack.gamesearch.domain.entity;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreEntity {

  private UUID id;
  private String name;
}
