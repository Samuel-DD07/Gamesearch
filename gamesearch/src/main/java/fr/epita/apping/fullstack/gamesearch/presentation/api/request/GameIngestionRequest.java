package fr.epita.apping.fullstack.gamesearch.presentation.api.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameIngestionRequest {

    private String gameId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Release year is required")
    @Min(value = 1970, message = "Release year must be >= 1970")
    @Max(value = 2030, message = "Release year must be <= 2030")
    private Integer releaseYear;

    @NotEmpty(message = "At least one genre is required")
    private List<String> genres;

    @NotEmpty(message = "At least one platform is required")
    private List<String> platforms;

    private String publisher;

    private String description;

    private String coverUrl;

    @DecimalMin(value = "0.0", message = "Rating must be >= 0.0")
    @DecimalMax(value = "10.0", message = "Rating must be <= 10.0")
    private Float rating;

    private List<String> tags;
}
