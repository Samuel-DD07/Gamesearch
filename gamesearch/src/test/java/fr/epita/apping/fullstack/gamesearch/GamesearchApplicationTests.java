package fr.epita.apping.fullstack.gamesearch;

import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionProducer;
import fr.epita.apping.fullstack.gamesearch.kafka.producer.GameIngestionStatusProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GamesearchApplicationTests {

  @MockBean private GameIngestionProducer gameIngestionProducer;
  @MockBean private GameIngestionStatusProducer gameIngestionStatusProducer;

  @Test
  void contextLoads() {}
}
