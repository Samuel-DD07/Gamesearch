package fr.epita.apping.fullstack.gamesearch.presentation.api.request;

import jakarta.validation.constraints.NotBlank;

public record PartnerRegisterRequest(
        @NotBlank(message = "Partner name is required") String name
) {}
