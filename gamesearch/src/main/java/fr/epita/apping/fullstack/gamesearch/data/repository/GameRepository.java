package fr.epita.apping.fullstack.gamesearch.data.repository;

import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository
    extends JpaRepository<GameModel, UUID>, JpaSpecificationExecutor<GameModel> {

  List<GameModel> findByReleaseYear(Integer releaseYear);

  @Query("SELECT g FROM GameModel g JOIN g.genres genre WHERE genre.name = :genreName")
  List<GameModel> findByGenreName(@Param("genreName") String genreName);

  @Query("SELECT g FROM GameModel g JOIN g.platforms platform WHERE platform.name = :platformName")
  List<GameModel> findByPlatformName(@Param("platformName") String platformName);

  List<GameModel> findByPartnerId(UUID partnerId);

  Optional<GameModel> findByExternalId(String externalId);

  Optional<GameModel> findByTitle(String title);

  @Query(
      "SELECT DISTINCT g FROM GameModel g JOIN g.genres gen WHERE g.id != :gameId AND gen.id IN :genreIds")
  List<GameModel> findBySharedGenres(
      @Param("gameId") UUID gameId, @Param("genreIds") List<UUID> genreIds);

  @Query(
      "SELECT DISTINCT g FROM GameModel g JOIN g.platforms plat WHERE g.id != :gameId AND plat.id IN :platformIds")
  List<GameModel> findBySharedPlatforms(
      @Param("gameId") UUID gameId, @Param("platformIds") List<UUID> platformIds);
}
