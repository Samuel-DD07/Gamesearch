package fr.epita.apping.fullstack.gamesearch.presentation.api.response;

import java.util.List;

public record BulkIngestionResponse(int total, int successful, int failed, List<String> errors) {}
