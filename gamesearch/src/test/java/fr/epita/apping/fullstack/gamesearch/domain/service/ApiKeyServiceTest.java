package fr.epita.apping.fullstack.gamesearch.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ApiKeyService")
class ApiKeyServiceTest {

  private ApiKeyService apiKeyService;

  @BeforeEach
  void setUp() {
    apiKeyService = new ApiKeyService();
  }

  @Nested
  @DisplayName("generateApiKey()")
  class GenerateApiKey {

    @Test
    @DisplayName("returns a key with the 'gs_' prefix")
    void returnsKeyWithGsPrefix() {
      String key = apiKeyService.generateApiKey();
      assertThat(key).startsWith("gs_");
    }

    @Test
    @DisplayName("returns a key of the expected length (3 prefix + 64 hex chars)")
    void returnsKeyOfExpectedLength() {
      String key = apiKeyService.generateApiKey();
      assertThat(key).hasSize(3 + 64);
    }

    @Test
    @DisplayName("generates unique keys on each call")
    void generatesUniqueKeys() {
      String key1 = apiKeyService.generateApiKey();
      String key2 = apiKeyService.generateApiKey();
      assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    @DisplayName("generated key contains only valid hexadecimal characters after the prefix")
    void generatedKeyContainsOnlyHexChars() {
      String key = apiKeyService.generateApiKey();
      String hexPart = key.substring(3);
      assertThat(hexPart).matches("[0-9a-f]+");
    }
  }

  @Nested
  @DisplayName("hash()")
  class Hash {

    @Test
    @DisplayName("same input always produces the same hash (deterministic)")
    void deterministicHash() {
      String input = "gs_someApiKey";
      String hash1 = apiKeyService.hash(input);
      String hash2 = apiKeyService.hash(input);
      assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("different inputs produce different hashes")
    void differentInputsDifferentHashes() {
      String hash1 = apiKeyService.hash("gs_keyA");
      String hash2 = apiKeyService.hash("gs_keyB");
      assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("returns a 64-character SHA-256 hex string")
    void returnsSha256HexString() {
      String hash = apiKeyService.hash("any-value");
      assertThat(hash).hasSize(64).matches("[0-9a-f]+");
    }

    @Test
    @DisplayName("hashing a generated key produces a valid hash")
    void hashOfGeneratedKey() {
      String key = apiKeyService.generateApiKey();
      String hash = apiKeyService.hash(key);
      assertThat(hash).isNotBlank().hasSize(64);
    }
  }
}
