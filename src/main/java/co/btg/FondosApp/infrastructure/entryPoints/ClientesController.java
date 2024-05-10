package co.btg.FondosApp.infrastructure.entryPoints;

import co.btg.FondosApp.domain.model.Cliente;
import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.model.Vinculo;
import co.btg.FondosApp.domain.usecase.ClienteUseCase;
import co.btg.FondosApp.infrastructure.drivenAdapters.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClientesController {

    @Autowired
    ClienteUseCase clienteRepository;

    @PostMapping("/cliente")
    public ResponseEntity saveCliente(@RequestBody Cliente cliente) {
        return clienteRepository.saveCliente(cliente);
    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity getCliente(@PathVariable("id") String clienteId) {
        return clienteRepository.getClienteById(clienteId);
    }

    @GetMapping("/cliente/{id}/vinculaciones")
    public ResponseEntity getAllVinculacionesActivas(@PathVariable("id") String clienteId) {
        return clienteRepository.getFondosVinculados(clienteId);
    }

    @PutMapping("/cliente/{id}")
    public ResponseEntity updateCliente(@PathVariable("id") String clienteId, @RequestBody Cliente cliente) {
        return clienteRepository.updateCliente(clienteId,cliente);
    }

    @PostMapping("/cliente/{id}/nueva")
    public ResponseEntity<?> saveNuevaVinculacion(@PathVariable("id") String clienteId, @RequestParam("fondo") String fondoId) {
        return clienteRepository.saveNuevaVinculacion(
                clienteId,
                fondoId
        );
    }

    @PostMapping("/cliente/{id}/cancel")
    public ResponseEntity saveNuevaCancelacion(@PathVariable("id") String clienteId, @RequestParam("transaction") String transactionId) {
        return clienteRepository.saveNuevaCancelacion(
                clienteId,
                transactionId
        );
    }

    @GetMapping("/cliente/{id}/historico")
    public ResponseEntity getHistoriaVinculaciones(@PathVariable("id") String clienteId) {
        return clienteRepository.getHistoricoVinculaciones(clienteId);
    }

}
