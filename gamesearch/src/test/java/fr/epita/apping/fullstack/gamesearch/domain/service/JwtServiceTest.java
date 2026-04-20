package fr.epita.apping.fullstack.gamesearch.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("JwtService")
class JwtServiceTest {

  private static final String VALID_SECRET =
      "dGhpcy1pcy1hLXZlcnktbG9uZy1zZWNyZXQta2V5LWZvci1qd3QtaHMyNTYtZ2FtZXNlYXJjaA==";
  private static final long EXPIRATION_MS = 86_400_000L;
  private static final long EXPIRED_MS = -1000L;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(jwtService, "secret", VALID_SECRET);
    ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRATION_MS);
  }

  @Nested
  @DisplayName("generateToken()")
  class GenerateToken {

    @Test
    @DisplayName("returns a non-blank JWT string")
    void returnsNonBlankToken() {
      String token = jwtService.generateToken("admin");
      assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("standard JWT format: three dot-separated parts")
    void hasThreeParts() {
      String token = jwtService.generateToken("user");
      assertThat(token.split("\\.")).hasSize(3);
    }
  }

  @Nested
  @DisplayName("extractUsername()")
  class ExtractUsername {

    @Test
    @DisplayName("extracts the exact username embedded in the token")
    void extractsCorrectUsername() {
      String token = jwtService.generateToken("samuel");
      assertThat(jwtService.extractUsername(token)).isEqualTo("samuel");
    }

    @Test
    @DisplayName("throws when the token is malformed")
    void throwsOnMalformedToken() {
      assertThatThrownBy(() -> jwtService.extractUsername("not.a.jwt"))
          .isInstanceOf(Exception.class);
    }
  }

  @Nested
  @DisplayName("isTokenValid()")
  class IsTokenValid {

    @Test
    @DisplayName("returns true for a valid, non-expired token matching the user")
    void returnsTrueForValidToken() {
      String token = jwtService.generateToken("admin");
      UserDetails userDetails = User.withUsername("admin").password("x").roles("ADMIN").build();
      assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("returns false when the username does not match")
    void returnsFalseForWrongUser() {
      String token = jwtService.generateToken("admin");
      UserDetails otherUser = User.withUsername("hacker").password("x").roles("ADMIN").build();
      assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    @DisplayName("throws exception for an expired token")
    void throwsExceptionForExpiredToken() {
      ReflectionTestUtils.setField(jwtService, "expirationMs", EXPIRED_MS);
      String token = jwtService.generateToken("admin");
      UserDetails userDetails = User.withUsername("admin").password("x").roles("ADMIN").build();
      assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
          .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }
  }
}
