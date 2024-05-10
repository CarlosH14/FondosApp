package co.btg.FondosApp.application.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class Error{
    private String error;
    private String message;
}
