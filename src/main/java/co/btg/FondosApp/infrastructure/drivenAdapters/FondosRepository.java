package co.btg.FondosApp.infrastructure.drivenAdapters;

import co.btg.FondosApp.domain.model.Fondo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.http.ResponseEntity;

import java.util.List;

@EnableScan
public interface FondosRepository {
    ResponseEntity saveFondo(Fondo fondo);
    ResponseEntity getFondoById(String fondoId);
    ResponseEntity delete(String fondoId);
    ResponseEntity update(String fondoId, Fondo fondo);
    ResponseEntity getAllFondos();

    ResponseEntity getFondoByTransaction(String transactionId);
}
