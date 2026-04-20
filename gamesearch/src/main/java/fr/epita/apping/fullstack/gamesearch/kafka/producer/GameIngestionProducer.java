package fr.epita.apping.fullstack.gamesearch.kafka.producer;

import fr.epita.apping.fullstack.gamesearch.config.KafkaConfig;
import fr.epita.apping.fullstack.gamesearch.kafka.dto.GameIngestionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameIngestionProducer {

  private final KafkaTemplate<String, GameIngestionMessage> kafkaTemplate;

  public void sendIngestionRequest(GameIngestionMessage message) {
    log.info("Sending ingestion request to Kafka for game: {}", message.getTitle());
    kafkaTemplate.send(KafkaConfig.GAME_INGESTION_TOPIC, message.getGameId(), message);
  }
}
