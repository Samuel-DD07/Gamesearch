package fr.epita.apping.fullstack.gamesearch;

import fr.epita.apping.fullstack.gamesearch.kafka.consumer.GameIngestionStatusConsumer;
import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionProducer;
import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionStatusProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GamesearchApplicationTests {

  @MockBean private GameIngestionProducer gameIngestionProducer;
  @MockBean private GameIngestionStatusProducer gameIngestionStatusProducer;
  @MockBean private GameIngestionStatusConsumer gameIngestionStatusConsumer;

  @MockBean
  private ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory;

  @MockBean private KafkaTemplate<String, Object> kafkaTemplate;

  @Test
  void contextLoads() {}
}
