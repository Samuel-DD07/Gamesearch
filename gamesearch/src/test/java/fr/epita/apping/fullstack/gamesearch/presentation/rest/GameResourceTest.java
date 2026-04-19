package fr.epita.apping.fullstack.gamesearch.presentation.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GameEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.GenreEntity;
import fr.epita.apping.fullstack.gamesearch.domain.entity.PlatformEntity;
import fr.epita.apping.fullstack.gamesearch.domain.service.GameService;
import fr.epita.apping.fullstack.gamesearch.exception.GameNotFoundException;
import fr.epita.apping.fullstack.gamesearch.presentation.api.request.GameCreateRequest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GameResource.class)
@org.springframework.context.annotation.Import({
  fr.epita.apping.fullstack.gamesearch.config.SecurityConfig.class,
  fr.epita.apping.fullstack.gamesearch.config.JwtAuthenticationFilter.class,
  fr.epita.apping.fullstack.gamesearch.config.ApiKeyAuthenticationFilter.class
})
@ActiveProfiles("test")
@DisplayName("GameResource (REST Controller)")
class GameResourceTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private GameService gameService;
  @MockBean private fr.epita.apping.fullstack.gamesearch.domain.service.JwtService jwtService;
  @MockBean private fr.epita.apping.fullstack.gamesearch.domain.service.ApiKeyService apiKeyService;

  @MockBean
  private fr.epita.apping.fullstack.gamesearch.data.repository.PartnerRepository partnerRepository;

  @MockBean
  private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

  private UUID gameId;
  private GameEntity sampleEntity;

  @BeforeEach
  void setUp() {
    gameId = UUID.randomUUID();
    sampleEntity =
        GameEntity.builder()
            .id(gameId)
            .title("Hollow Knight")
            .releaseYear(2017)
            .publisher("Team Cherry")
            .rating(9.5f)
            .genres(List.of(GenreEntity.builder().id(UUID.randomUUID()).name("Action").build()))
            .platforms(List.of(PlatformEntity.builder().id(UUID.randomUUID()).name("PC").build()))
            .tags(List.of())
            .build();
  }

  @Nested
  @DisplayName("GET /games")
  class GetGames {

    @Test
    @DisplayName("returns 200 with a page of games for anonymous users")
    void returns200WithGames() throws Exception {
      when(gameService.searchGames(any(), any(), any(), any(), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(sampleEntity)));

      mockMvc
          .perform(get("/games").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content[0].title").value("Hollow Knight"))
          .andExpect(jsonPath("$.content[0].rating").value(9.5));
    }

    @Test
    @DisplayName("returns 200 with an empty page when no games match search criteria")
    void returns200WithEmptyPage() throws Exception {
      when(gameService.searchGames(any(), any(), any(), any(), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of()));

      mockMvc
          .perform(get("/games").param("q", "nonexistent").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.content").isEmpty());
    }
  }

  @Nested
  @DisplayName("GET /games/{id}")
  class GetGame {

    @Test
    @DisplayName("returns 200 with game details for an existing game")
    void returns200ForExistingGame() throws Exception {
      when(gameService.getGame(gameId)).thenReturn(sampleEntity);
      when(gameService.getSimilarGames(gameId)).thenReturn(List.of());

      mockMvc
          .perform(get("/games/{id}", gameId).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(gameId.toString()))
          .andExpect(jsonPath("$.title").value("Hollow Knight"));
    }

    @Test
    @DisplayName("returns 404 when the game does not exist")
    void returns404ForMissingGame() throws Exception {
      UUID missing = UUID.randomUUID();
      when(gameService.getGame(missing)).thenThrow(new GameNotFoundException(missing));

      mockMvc
          .perform(get("/games/{id}", missing).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("returns 400 when the ID is not a valid UUID")
    void returns400ForInvalidUuid() throws Exception {
      mockMvc
          .perform(get("/games/{id}", "not-a-uuid").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("POST /games")
  class CreateGame {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("returns 201 when an admin creates a valid game")
    void returns201ForAdmin() throws Exception {
      GameCreateRequest request =
          GameCreateRequest.builder()
              .title("Celeste")
              .releaseYear(2018)
              .genres(List.of("Platformer"))
              .platforms(List.of("PC"))
              .rating(9.0f)
              .build();

      when(gameService.createGame(any(GameCreateRequest.class))).thenReturn(sampleEntity);

      mockMvc
          .perform(
              post("/games")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("returns 401 when an unauthenticated user tries to create a game")
    void returns401ForAnonymous() throws Exception {
      GameCreateRequest request =
          GameCreateRequest.builder()
              .title("Celeste")
              .releaseYear(2018)
              .genres(List.of("Platformer"))
              .platforms(List.of("PC"))
              .build();

      mockMvc
          .perform(
              post("/games")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("returns 400 when the request body is missing required fields")
    void returns400ForInvalidRequest() throws Exception {
      GameCreateRequest invalid = GameCreateRequest.builder().title("").releaseYear(null).build();

      mockMvc
          .perform(
              post("/games")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(invalid)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /games/{id}")
  class DeleteGame {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("returns 204 when an admin deletes an existing game")
    void returns204ForAdmin() throws Exception {
      mockMvc.perform(delete("/games/{id}", gameId)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("returns 401 when an unauthenticated user tries to delete a game")
    void returns401ForAnonymous() throws Exception {
      mockMvc.perform(delete("/games/{id}", gameId)).andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /games/recent")
  class GetRecentGames {

    @Test
    @DisplayName("returns 200 with recent games list for anonymous users")
    void returns200WithRecentGames() throws Exception {
      when(gameService.getRecentGames(10)).thenReturn(List.of(sampleEntity));

      mockMvc
          .perform(get("/games/recent").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].title").value("Hollow Knight"));
    }
  }

  @Nested
  @DisplayName("GET /games/popular")
  class GetPopularGames {

    @Test
    @DisplayName("returns 200 with popular games list for anonymous users")
    void returns200WithPopularGames() throws Exception {
      when(gameService.getPopularGames(10)).thenReturn(List.of(sampleEntity));

      mockMvc
          .perform(get("/games/popular").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$[0].rating").value(9.5));
    }
  }
}
