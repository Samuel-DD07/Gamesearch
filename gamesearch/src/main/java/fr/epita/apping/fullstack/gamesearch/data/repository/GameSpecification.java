package fr.epita.apping.fullstack.gamesearch.data.repository;

import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import fr.epita.apping.fullstack.gamesearch.data.model.GenreModel;
import fr.epita.apping.fullstack.gamesearch.data.model.PlatformModel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class GameSpecification {

  public static Specification<GameModel> hasTitle(String title) {
    return (root, query, cb) ->
        (title == null || title.isBlank())
            ? null
            : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
  }

  public static Specification<GameModel> hasGenre(String genre) {
    return (root, query, cb) -> {
      if (genre == null || genre.isBlank()) {
        return null;
      }
      Join<GameModel, GenreModel> genres = root.join("genres", JoinType.INNER);
      return cb.equal(cb.lower(genres.get("name")), genre.toLowerCase());
    };
  }

  public static Specification<GameModel> hasPlatform(String platform) {
    return (root, query, cb) -> {
      if (platform == null || platform.isBlank()) return null;
      Join<GameModel, PlatformModel> platforms = root.join("platforms", JoinType.INNER);
      return cb.equal(cb.lower(platforms.get("name")), platform.toLowerCase());
    };
  }

  public static Specification<GameModel> hasReleaseYear(Integer year) {
    return (root, query, cb) -> year == null ? null : cb.equal(root.get("releaseYear"), year);
  }
}
