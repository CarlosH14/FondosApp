package co.btg.FondosApp.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Vinculo {
    private String nombreFondo;
    private Boolean vinculado;
    private Date fechaVinculacion;
}
