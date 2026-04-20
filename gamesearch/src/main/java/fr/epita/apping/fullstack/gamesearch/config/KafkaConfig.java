package fr.epita.apping.fullstack.gamesearch.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

  public static final String GAME_INGESTION_TOPIC = "game-ingestion-topic";
  public static final String GAME_INGESTION_STATUS_TOPIC = "game-ingestion-status-topic";

  @Bean
  public NewTopic gameIngestionTopic() {
    return TopicBuilder.name(GAME_INGESTION_TOPIC).partitions(1).replicas(1).build();
  }

  @Bean
  public NewTopic gameIngestionStatusTopic() {
    return TopicBuilder.name(GAME_INGESTION_STATUS_TOPIC).partitions(1).replicas(1).build();
  }
}
