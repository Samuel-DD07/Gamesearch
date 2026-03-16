package fr.epita.apping.fullstack.gamesearch.data.repository;

import fr.epita.apping.fullstack.gamesearch.data.model.PartnerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerModel, UUID> {

    Optional<PartnerModel> findByApiKeyHash(String apiKeyHash);

    boolean existsByApiKeyHash(String apiKeyHash);
}
