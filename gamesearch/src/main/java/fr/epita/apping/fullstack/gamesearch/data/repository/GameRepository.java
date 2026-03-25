package fr.epita.apping.fullstack.gamesearch.data.repository;

import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<GameModel, UUID>,
        JpaSpecificationExecutor<GameModel> {

    Optional<GameModel> findByExternalId(String externalId);

    @Query("SELECT DISTINCT g FROM GameModel g JOIN g.genres gen WHERE g.id != :gameId AND gen.id IN :genreIds")
    List<GameModel> findBySharedGenres(@Param("gameId") UUID gameId, @Param("genreIds") List<UUID> genreIds);

    @Query("SELECT DISTINCT g FROM GameModel g JOIN g.platforms plat WHERE g.id != :gameId AND plat.id IN :platformIds")
    List<GameModel> findBySharedPlatforms(@Param("gameId") UUID gameId, @Param("platformIds") List<UUID> platformIds);
}
