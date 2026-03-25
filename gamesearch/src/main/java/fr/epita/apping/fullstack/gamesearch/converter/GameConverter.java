package fr.epita.apping.fullstack.gamesearch.converter;

import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GenreEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PartnerEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PlatformEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.TagEntity;
import fr.epita.apping.fullstack.gamesearch.presentation.api.response.GameResponse;

public class GameConverter {

    public static GameEntity toEntity(GameModel model) {
        return GameEntity.builder()
                .id(model.getId())
                .externalId(model.getExternalId())
                .title(model.getTitle())
                .releaseYear(model.getReleaseYear())
                .publisher(model.getPublisher())
                .description(model.getDescription())
                .coverUrl(model.getCoverUrl())
                .rating(model.getRating())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .genres(model.getGenres().stream()
                        .map(g -> GenreEntity.builder().id(g.getId()).name(g.getName()).build())
                        .toList())
                .platforms(model.getPlatforms().stream()
                        .map(p -> PlatformEntity.builder().id(p.getId()).name(p.getName()).build())
                        .toList())
                .tags(model.getTags().stream()
                        .map(t -> TagEntity.builder().id(t.getId()).name(t.getName()).build())
                        .toList())
                .partner(model.getPartner() == null ? null : PartnerEntity.builder()
                        .id(model.getPartner().getId())
                        .name(model.getPartner().getName())
                        .build())
                .build();
    }

    public static GameResponse toResponse(GameEntity entity) {
        return GameResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .releaseYear(entity.getReleaseYear())
                .publisher(entity.getPublisher())
                .description(entity.getDescription())
                .coverUrl(entity.getCoverUrl())
                .rating(entity.getRating())
                .genres(entity.getGenres().stream().map(GenreEntity::getName).toList())
                .platforms(entity.getPlatforms().stream().map(PlatformEntity::getName).toList())
                .tags(entity.getTags().stream().map(TagEntity::getName).toList())
                .build();
    }
}
