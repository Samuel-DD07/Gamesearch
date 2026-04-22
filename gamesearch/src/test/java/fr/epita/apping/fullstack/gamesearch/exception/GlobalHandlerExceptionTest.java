package fr.epita.apping.fullstack.gamesearch.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import fr.epita.apping.fullstack.gamesearch.domain.service.GameService;
import fr.epita.apping.fullstack.gamesearch.presentation.rest.GameResource;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@DisplayName("GlobalHandlerException (via GameResource)")
class GlobalHandlerExceptionTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private GameService gameService;
  @MockBean private fr.epita.apping.fullstack.gamesearch.domain.service.JwtService jwtService;
  @MockBean private fr.epita.apping.fullstack.gamesearch.domain.service.ApiKeyService apiKeyService;

  @MockBean
  private fr.epita.apping.fullstack.gamesearch.data.repository.PartnerRepository partnerRepository;

  @MockBean
  private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

  @Nested
  @DisplayName("GameNotFoundException → 404")
  class HandleGameNotFound {

    @Test
    @DisplayName("returns 404 with structured error body when the game is not found")
    void returns404WithErrorBody() throws Exception {
      UUID missing = UUID.randomUUID();
      Mockito.when(gameService.getGame(missing)).thenThrow(new GameNotFoundException(missing));

      mockMvc
          .perform(get("/games/{id}", missing).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not Found"))
          .andExpect(
              jsonPath("$.message")
                  .value(org.hamcrest.Matchers.containsString(missing.toString())));
    }
  }

  @Nested
  @DisplayName("MethodArgumentNotValidException → 400")
  class HandleValidation {

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("returns 400 with validation details when the request body is invalid")
    void returns400WithValidationDetails() throws Exception {
      String invalidJson =
          """
          {
            "title": "",
            "releaseYear": null,
            "genres": [],
            "platforms": []
          }
          """;

      mockMvc
          .perform(post("/games").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.error").value("Bad Request"));
    }
  }

  @Nested
  @DisplayName("MethodArgumentTypeMismatchException → 400")
  class HandleTypeMismatch {

    @Test
    @DisplayName("returns 400 with a descriptive message when the UUID path variable is malformed")
    void returns400ForMalformedUuid() throws Exception {
      mockMvc
          .perform(get("/games/{id}", "not-a-uuid").accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400));
    }
  }

  @Nested
  @DisplayName("Generic Exception → 500")
  class HandleGenericException {

    @Test
    @DisplayName("returns 500 when an unexpected exception occurs during request processing")
    void returns500OnUnexpectedError() throws Exception {
      UUID id = UUID.randomUUID();
      Mockito.when(gameService.getGame(id)).thenThrow(new RuntimeException("unexpected"));

      mockMvc
          .perform(get("/games/{id}", id).accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isInternalServerError())
          .andExpect(jsonPath("$.status").value(500))
          .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }
  }
}
