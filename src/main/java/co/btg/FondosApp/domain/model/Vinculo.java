package co.btg.FondosApp.domain.model;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
public class Vinculo {
    private String nombreFondo;
    private Boolean vinculado;
    private Date fechaVinculacion;
}
