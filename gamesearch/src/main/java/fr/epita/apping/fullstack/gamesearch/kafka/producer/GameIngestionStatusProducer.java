package fr.epita.apping.fullstack.gamesearch.kafka.producer;

import fr.epita.apping.fullstack.gamesearch.config.KafkaConfig;
import fr.epita.apping.fullstack.gamesearch.kafka.dto.GameIngestionStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameIngestionStatusProducer {

  private final KafkaTemplate<String, GameIngestionStatusMessage> kafkaTemplate;

  public void sendStatus(GameIngestionStatusMessage message) {
    log.info(
        "Sending ingestion status for gameId={} status={}",
        message.getGameId(),
        message.getStatus());
    kafkaTemplate.send(KafkaConfig.GAME_INGESTION_STATUS_TOPIC, message.getGameId(), message);
  }
}
