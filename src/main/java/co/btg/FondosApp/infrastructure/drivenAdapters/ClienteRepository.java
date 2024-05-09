package co.btg.FondosApp.infrastructure.drivenAdapters;

import co.btg.FondosApp.domain.model.Cliente;
import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.model.FondoCliente;
import co.btg.FondosApp.domain.model.Vinculo;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@EnableScan
public interface ClienteRepository {
    ResponseEntity saveCliente(Cliente cliente);
    ResponseEntity getClienteById(String clienteId);
    ResponseEntity updateCliente(String clienteId, Cliente cliente);
    ResponseEntity getFondosVinculados(String clienteId);

    ResponseEntity saveNuevaVinculacion(String clienteId, String tipoFondo);
    ResponseEntity saveNuevaCancelacion(String clienteId, String vinculacionId);
    ResponseEntity getHistoricoVinculaciones(String clienteId);
}
