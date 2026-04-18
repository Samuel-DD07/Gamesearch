package fr.epita.apping.fullstack.gamesearch.presentation.api.response;

import java.util.UUID;

public record PartnerRegisterResponse(
        UUID partnerId,
        String name,
        String apiKey
) {}
