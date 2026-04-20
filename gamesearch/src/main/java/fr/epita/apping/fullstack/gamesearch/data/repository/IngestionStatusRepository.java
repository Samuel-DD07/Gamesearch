package fr.epita.apping.fullstack.gamesearch.data.repository;

import fr.epita.apping.fullstack.gamesearch.data.model.IngestionStatusModel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngestionStatusRepository extends JpaRepository<IngestionStatusModel, UUID> {
  Optional<IngestionStatusModel> findByExternalIdAndPartnerName(
      String externalId, String partnerName);
}
