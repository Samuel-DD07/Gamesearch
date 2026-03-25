package fr.epita.apping.fullstack.gamesearch.domain.entity;

import lombok.*;

import java.util.UUID;

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
    private String plainApiKey; // portée une seule fois lors de la création, jamais persistée
}
