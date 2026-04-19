package fr.epita.apping.fullstack.gamesearch.domain.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

  private static final String PREFIX = "gs_";
  private static final int KEY_BYTES = 32;

  public String generateApiKey() {
    byte[] bytes = new byte[KEY_BYTES];
    new SecureRandom().nextBytes(bytes);
    return PREFIX + HexFormat.of().formatHex(bytes);
  }

  public String hash(String apiKey) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(apiKey.getBytes());
      return HexFormat.of().formatHex(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 not available", e);
    }
  }
}
