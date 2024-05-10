package co.btg.FondosApp.domain.usecase;

import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.model.FondoCliente;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@WebAppConfiguration
@TestPropertySource(properties = {
        "amazon.dynamodb.endpoint=http://localhost:8000/",
        "amazon.aws.accesskey=test1",
        "amazon.aws.secretkey=test231" })
class FondoUseCaseTest {


    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private FondoUseCase fondoUseCase;

    @BeforeEach
    void setUp() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
    }

    @Test
    void saveFondo() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        assertNotNull(fondoTest.getFondoId());
        fondoUseCase.saveFondo(fondoTest);
        dynamoDBMapper.delete(fondoTest);
    }

    @Test
    void getFondoById() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        ResponseEntity response = fondoUseCase.getFondoById(fondoTest.getFondoId());
        assertEquals(response.getStatusCode(), HttpStatus.FOUND);
        assertNotNull(response.getBody());
        dynamoDBMapper.delete(fondoTest);
    }

    @Test
    void getFondoByIdFail() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        ResponseEntity response = fondoUseCase.getFondoById("fake");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNotNull(response.getBody());
        dynamoDBMapper.delete(fondoTest);
    }

    @Test
    void delete() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        ResponseEntity response = fondoUseCase.delete(fondoTest.getFondoId());
        assertEquals(response.getStatusCode(), HttpStatus.FOUND);
        assertNotNull(response.getBody());
        dynamoDBMapper.delete(fondoTest);
    }

    @Test
    void deleteFail() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        ResponseEntity response = fondoUseCase.delete("fake");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNotNull(response.getBody());
        dynamoDBMapper.delete(fondoTest);
    }

    @Test
    void update() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        Fondo fondoTestUpdate = new Fondo(fondoTest.getFondoId(),"catTest2", 2, "nomTest2");
        fondoUseCase.update(fondoTest.getFondoId(), fondoTestUpdate);
        assertEquals(fondoTest.getFondoId(), fondoTestUpdate.getFondoId());
        dynamoDBMapper.delete(fondoTest);
    }

    @Test
    void getAllFondos() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        Fondo fondoTest2 = new Fondo("idtest2","catTest2", 2, "nomTest2");
        dynamoDBMapper.save(fondoTest);
        dynamoDBMapper.save(fondoTest2);
        ResponseEntity<List<Fondo>> listaFondos = fondoUseCase.getAllFondos();
        assertNotNull(listaFondos.getBody());
        assertTrue(listaFondos.getBody().size()>0);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(fondoTest2);
    }

    @Test
    void getFondoByTransaction() {
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        FondoCliente fondoClienteTest = new FondoCliente("traTest", "idtest", "cliTest", Date.from(Instant.now()), Boolean.TRUE);
        dynamoDBMapper.save(fondoTest);
        dynamoDBMapper.save(fondoClienteTest);
        ResponseEntity<Fondo> fondo = fondoUseCase.getFondoByTransaction(fondoClienteTest.getTransactionId());
        assertEquals(fondo.getBody(), fondoTest);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(fondoClienteTest);
    }
}