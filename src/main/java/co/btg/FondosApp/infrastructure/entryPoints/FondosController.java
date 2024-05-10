package co.btg.FondosApp.infrastructure.entryPoints;

import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.usecase.FondoUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FondosController {

    @Autowired
    private FondoUseCase fondosRepository;

    @PostMapping("/fondo")
    public ResponseEntity saveFondo(@RequestBody Fondo fondo) {
        return fondosRepository.saveFondo(fondo);
    }

    @GetMapping("/fondo/{id}")
    public ResponseEntity getFondo(@PathVariable("id") String fondoId) {
        return fondosRepository.getFondoById(fondoId);
    }

    @GetMapping("/fondos")
    public ResponseEntity getAllFondosFPV() {
        return fondosRepository.getAllFondos();
    }

    @DeleteMapping("/fondo/{id}")
    public ResponseEntity deleteFondo(@PathVariable("id") String fondoId) {
        return  fondosRepository.delete(fondoId);
    }

    @PutMapping("/fondo/{id}")
    public ResponseEntity updateFondo(@PathVariable("id") String fondoId, @RequestBody Fondo fondo) {
        return fondosRepository.update(fondoId,fondo);
    }
}
