package co.btg.FondosApp.infrastructure.drivenAdapters;

import co.btg.FondosApp.domain.model.Fondo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;

import java.util.List;

@EnableScan
public interface FondosRepository {
    Fondo saveFondo(Fondo fondo);
    Fondo getFondoById(String fondoId);
    String delete(String fondoId);
    String update(String fondoId, Fondo fondo);
    List<Fondo> getAllFondos();

    Fondo getFondoByTransaction(String transactionId);
}
