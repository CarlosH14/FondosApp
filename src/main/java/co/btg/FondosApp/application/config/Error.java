package co.btg.FondosApp.application.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
public class Error extends Exception{
    private String error;
    private String message;
}
