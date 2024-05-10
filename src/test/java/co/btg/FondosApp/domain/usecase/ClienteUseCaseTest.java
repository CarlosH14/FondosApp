package co.btg.FondosApp.domain.usecase;

import co.btg.FondosApp.domain.model.Cliente;
import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.model.FondoCliente;
import co.btg.FondosApp.domain.model.Vinculo;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
@TestPropertySource(properties = {
        "amazon.dynamodb.endpoint=http://localhost:8000/",
        "amazon.aws.accesskey=test1",
        "amazon.aws.secretkey=test231" })
class ClienteUseCaseTest {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private FondoUseCase fondoUseCase;

    @Autowired
    private ClienteUseCase clienteUseCase;

    @BeforeEach
    void setUp() {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);
    }

    @Test
    void saveCliente() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 1, new ArrayList<>());
        assertNotNull(clienteTest.getClienteId());
        clienteUseCase.saveCliente(clienteTest);
        dynamoDBMapper.delete(clienteTest);
    }

    @Test
    void getClienteById() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 1, new ArrayList<>());
        dynamoDBMapper.save(clienteTest);
        ResponseEntity response = clienteUseCase.getClienteById(clienteTest.getClienteId());
        assertEquals(response.getStatusCode(), HttpStatus.FOUND);
        assertNotNull(response.getBody());
        dynamoDBMapper.delete(clienteTest);
    }

    @Test
    void getClienteByIdFail() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 1, new ArrayList<>());
        dynamoDBMapper.save(clienteTest);
        ResponseEntity response = clienteUseCase.getClienteById("fake");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNotNull(response.getBody());
        dynamoDBMapper.delete(clienteTest);
    }

    @Test
    void updateCliente() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 1, new ArrayList<>());
        dynamoDBMapper.save(clienteTest);
        Cliente clienteTestUpdate = new Cliente("idtest", "nomTest2", "apeTest2", 2, new ArrayList<>());
        clienteUseCase.updateCliente(clienteTest.getClienteId(), clienteTestUpdate);
        assertEquals(clienteTest.getClienteId(), clienteTestUpdate.getClienteId());
        dynamoDBMapper.delete(clienteTest);
    }

    @Test
    void getFondosVinculados() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 1, Arrays.asList("traTest"));
        dynamoDBMapper.save(clienteTest);

        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        FondoCliente fondoClienteTest = new FondoCliente("traTest", "idtest", "cliTest", Date.from(Instant.now()), Boolean.TRUE);
        dynamoDBMapper.save(fondoTest);
        dynamoDBMapper.save(fondoClienteTest);

        ResponseEntity<List<String>> response = clienteUseCase.getFondosVinculados(clienteTest.getClienteId());

        assertNotNull(response.getBody());
        assertTrue(response.getBody().size()>0);

        dynamoDBMapper.delete(clienteTest);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(fondoClienteTest);
    }

    @Test
    void getFondosVinculadosFail() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 1, Arrays.asList("traTest"));
        dynamoDBMapper.save(clienteTest);
        ResponseEntity<List<String>> response = clienteUseCase.getFondosVinculados("fake");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNotNull(response.getBody());
        dynamoDBMapper.delete(clienteTest);
    }

    @Test
    void saveNuevaVinculacion() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 10, new ArrayList<>());
        dynamoDBMapper.save(clienteTest);
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        ResponseEntity<FondoCliente> response = clienteUseCase.saveNuevaVinculacion(clienteTest.getClienteId(), fondoTest.getFondoId());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getTransactionId());

        dynamoDBMapper.delete(clienteTest);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(response.getBody());
    }

    @Test
    void saveNuevaVinculacionFailBalance() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 0, new ArrayList<>());
        dynamoDBMapper.save(clienteTest);
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        ResponseEntity<FondoCliente> response = clienteUseCase.saveNuevaVinculacion(clienteTest.getClienteId(), fondoTest.getFondoId());
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.PAYMENT_REQUIRED);
        dynamoDBMapper.delete(clienteTest);
        dynamoDBMapper.delete(fondoTest);
    }

    @Test
    void saveNuevaVinculacionFailAlready() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 10, Arrays.asList("traTest"));
        dynamoDBMapper.save(clienteTest);
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        FondoCliente fondoClienteTest = new FondoCliente("traTest", "idtest", "cliTest", Date.from(Instant.now()), Boolean.TRUE);
        dynamoDBMapper.save(fondoClienteTest);
        ResponseEntity<FondoCliente> response = clienteUseCase.saveNuevaVinculacion(clienteTest.getClienteId(), fondoTest.getFondoId());
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        dynamoDBMapper.delete(clienteTest);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(fondoClienteTest);
    }

    @Test
    void saveNuevaCancelacion() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 10, Arrays.asList("traTest"));
        dynamoDBMapper.save(clienteTest);
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        FondoCliente fondoClienteTest = new FondoCliente("traTest", "idtest", "cliTest", Date.from(Instant.now()), Boolean.TRUE);
        dynamoDBMapper.save(fondoClienteTest);
        ResponseEntity<FondoCliente> response = clienteUseCase.saveNuevaCancelacion(clienteTest.getClienteId(), fondoClienteTest.getTransactionId());
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        dynamoDBMapper.delete(clienteTest);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(fondoClienteTest);
    }

    @Test
    void saveNuevaCancelacionFail() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 10, Arrays.asList("traTest"));
        dynamoDBMapper.save(clienteTest);
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        FondoCliente fondoClienteTest = new FondoCliente("traTest", "idtest", "cliTest", Date.from(Instant.now()), Boolean.TRUE);
        dynamoDBMapper.save(fondoClienteTest);
        ResponseEntity<FondoCliente> response = clienteUseCase.saveNuevaCancelacion(clienteTest.getClienteId(), "fake");
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        dynamoDBMapper.delete(clienteTest);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(fondoClienteTest);
    }

    @Test
    void getHistoricoVinculaciones() {
        Cliente clienteTest = new Cliente("idtest", "nomTest", "apeTest", 10, Arrays.asList("traTest"));
        dynamoDBMapper.save(clienteTest);
        Fondo fondoTest = new Fondo("idtest", "catTest", 1, "nomTest");
        dynamoDBMapper.save(fondoTest);
        FondoCliente fondoClienteTest = new FondoCliente("traTest", "idtest", "cliTest", Date.from(Instant.now()), Boolean.TRUE);
        dynamoDBMapper.save(fondoClienteTest);
        ResponseEntity<List<Vinculo>> response = clienteUseCase.getHistoricoVinculaciones(clienteTest.getClienteId());
        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        dynamoDBMapper.delete(clienteTest);
        dynamoDBMapper.delete(fondoTest);
        dynamoDBMapper.delete(fondoClienteTest);
    }
}