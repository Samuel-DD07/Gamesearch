package fr.epita.apping.fullstack.gamesearch.domain.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenreEntity {

    private UUID id;
    private String name;
}
