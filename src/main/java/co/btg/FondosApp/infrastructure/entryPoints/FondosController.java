package co.btg.FondosApp.infrastructure.entryPoints;

import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.usecase.FondoUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FondosController {

    @Autowired
    private FondoUseCase fondosRepository;

    @PostMapping("/fondo")
    public Fondo saveFondo(@RequestBody Fondo fondo) {
        return fondosRepository.saveFondo(fondo);
    }

    @GetMapping("/fondo/{id}")
    public Fondo getFondo(@PathVariable("id") String fondoId) {
        return fondosRepository.getFondoById(fondoId);
    }

    @GetMapping("/fondos")
    public List<Fondo> getAllFondosFPV() {
        return fondosRepository.getAllFondos();
    }

    @DeleteMapping("/fondo/{id}")
    public String deleteFondo(@PathVariable("id") String fondoId) {
        return  fondosRepository.delete(fondoId);
    }

    @PutMapping("/fondo/{id}")
    public String updateFondo(@PathVariable("id") String fondoId, @RequestBody Fondo fondo) {
        return fondosRepository.update(fondoId,fondo);
    }
}
