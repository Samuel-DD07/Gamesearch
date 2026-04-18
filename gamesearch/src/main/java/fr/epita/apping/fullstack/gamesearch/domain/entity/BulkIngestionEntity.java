package fr.epita.apping.fullstack.gamesearch.domain.entity;

import java.util.List;

public record BulkIngestionEntity(
        int total,
        int successful,
        int failed,
        List<String> errors
) {}
