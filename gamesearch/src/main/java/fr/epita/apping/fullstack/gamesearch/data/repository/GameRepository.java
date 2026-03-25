package fr.epita.apping.fullstack.gamesearch.data.repository;

import fr.epita.apping.fullstack.gamesearch.data.model.GameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRepository extends JpaRepository<GameModel, UUID> {

    Optional<GameModel> findByExternalId(String externalId);

    List<GameModel> findByTitleContainingIgnoreCase(String title);

    List<GameModel> findByReleaseYear(Integer releaseYear);

    @Query("SELECT g FROM GameModel g JOIN g.genres genre WHERE genre.name = :genreName")
    List<GameModel> findByGenreName(@Param("genreName") String genreName);

    @Query("SELECT g FROM GameModel g JOIN g.platforms platform WHERE platform.name = :platformName")
    List<GameModel> findByPlatformName(@Param("platformName") String platformName);

    List<GameModel> findByPartnerId(UUID partnerId);
}
