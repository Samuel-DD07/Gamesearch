package fr.epita.apping.fullstack.gamesearch.data.repository;

import fr.epita.apping.fullstack.gamesearch.data.model.PlatformModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlatformRepository extends JpaRepository<PlatformModel, UUID> {

    Optional<PlatformModel> findByName(String name);

    boolean existsByName(String name);
}
