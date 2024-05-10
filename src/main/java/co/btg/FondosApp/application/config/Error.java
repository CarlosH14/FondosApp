package co.btg.FondosApp.application.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class Error extends Exception{
    private String error;
    private String message;
}
