package co.btg.FondosApp.domain.usecase;

import co.btg.FondosApp.application.config.Error;
import co.btg.FondosApp.domain.model.Cliente;
import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.model.FondoCliente;
import co.btg.FondosApp.infrastructure.drivenAdapters.FondosRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FondoUseCase implements FondosRepository {

    private static final Logger log = LogManager.getLogger(ClienteUseCase.class);
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public ResponseEntity saveFondo(Fondo fondo) {
        dynamoDBMapper.save(fondo);
        log.info("SAVED NEW Fondo: "+fondo.getFondoId());
        return new ResponseEntity(fondo, HttpStatus.CREATED);
    }

    public ResponseEntity getFondoById(String fondoId) {
        log.info("GET Fondo: "+fondoId);
        try {
            Fondo fondo = dynamoDBMapper.load(Fondo.class, fondoId);
            log.info("FOUND");
            return new ResponseEntity(fondo, HttpStatus.FOUND);
        }catch (Exception e){
            log.error("NOT FOUND");
            return new ResponseEntity<>(new Error("Fondo no encontrado", "El fondo con el id "+fondoId+" no se encontró"), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity delete(String fondoId) {
        log.info("DELETE Fondo: "+fondoId);
        try {
            Fondo fondo = dynamoDBMapper.load(Fondo.class, fondoId);
            dynamoDBMapper.delete(fondo);
            log.info("DELETED");
            return new ResponseEntity("Fondo Deleted!", HttpStatus.FOUND);
        }catch (Exception e){
            log.error("NOT FOUND");
            return new ResponseEntity<>(new Error("Fondo no encontrado", "El fondo con el id "+fondoId+" no se encontró"), HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity update(String fondoId, Fondo fondo) {
        log.info("UPDATE Fondo: "+fondoId);
        try{
            dynamoDBMapper.save(fondo,
                    new DynamoDBSaveExpression()
                            .withExpectedEntry("fondoId",
                                    new ExpectedAttributeValue(
                                            new AttributeValue().withS(fondoId)
                                    )));
            log.info("SUCCESS");
            return new ResponseEntity<>(fondoId, HttpStatus.OK);
        }catch (Exception e){
            log.error("FAILED");
            return new ResponseEntity(new Error("Fallo al actualizar", "No se pudo actualizar la info del Fondo: "+fondoId), HttpStatus.CONFLICT);
        }
    }

    @Override
    public ResponseEntity getAllFondos() {

        List<Fondo> scanResult = dynamoDBMapper.scan(Fondo.class, new DynamoDBScanExpression());

        for (Fondo fondo : scanResult) {
            log.info(fondo.getNombreFondo());
        }

        return new ResponseEntity<>(scanResult, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getFondoByTransaction(String transactionId) {
        FondoCliente transaction = dynamoDBMapper.load(FondoCliente.class, transactionId);
        Fondo fondo = dynamoDBMapper.load(Fondo.class, transaction.getFondoId());
        return new ResponseEntity(fondo, HttpStatus.OK);
    }
}
