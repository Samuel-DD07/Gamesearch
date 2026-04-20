package fr.epita.apping.fullstack.gamesearch.config;

import fr.epita.apping.fullstack.gamesearch.data.model.PartnerModel;
import fr.epita.apping.fullstack.gamesearch.data.repository.PartnerRepository;
import fr.epita.apping.fullstack.gamesearch.domain.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  public static final String API_KEY_HEADER = "X-API-Key";

  private final ApiKeyService apiKeyService;
  private final PartnerRepository partnerRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String apiKey = request.getHeader(API_KEY_HEADER);

    if (apiKey != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      String hash = apiKeyService.hash(apiKey);
      Optional<PartnerModel> partner = partnerRepository.findByApiKeyHash(hash);

      if (partner.isPresent() && partner.get().getActive()) {
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                partner.get().getName(), null, List.of(new SimpleGrantedAuthority("ROLE_PARTNER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }

    filterChain.doFilter(request, response);
  }
}
