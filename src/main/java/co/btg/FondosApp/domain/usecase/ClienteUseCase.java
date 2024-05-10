package co.btg.FondosApp.domain.usecase;

import co.btg.FondosApp.application.config.Error;
import co.btg.FondosApp.domain.model.Cliente;
import co.btg.FondosApp.domain.model.Fondo;
import co.btg.FondosApp.domain.model.FondoCliente;
import co.btg.FondosApp.domain.model.Vinculo;
import co.btg.FondosApp.infrastructure.drivenAdapters.ClienteRepository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Repository
public class ClienteUseCase implements ClienteRepository {

    private static final Logger log = LogManager.getLogger(ClienteUseCase.class);
    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    private FondoUseCase fondoUseCase;

    @Override
    public ResponseEntity saveCliente(Cliente cliente) {
        dynamoDBMapper.save(cliente);
        log.info("SAVED NEW Cliente: "+cliente.getClienteId());
        return new ResponseEntity(cliente, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity getClienteById(String clienteId){
        log.info("GET Cliente: "+clienteId);
        Cliente cliente = dynamoDBMapper.load(Cliente.class, clienteId);
        if (Objects.isNull(cliente)) {
            log.error("NOT FOUND");
            return new ResponseEntity<>(new Error("Cliente no encontrado", "El cliente con el id " + clienteId + " no se encontró"), HttpStatus.NOT_FOUND);
        }else {
            log.info("FOUND");
            return new ResponseEntity(cliente, HttpStatus.FOUND);
        }
    }

    @Override
    public ResponseEntity updateCliente(String clienteId, Cliente cliente) {
        log.info("UPDATE Cliente: "+clienteId);
        dynamoDBMapper.save(cliente,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("clienteId",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(clienteId)
                                )));
        log.info("SUCCESS");
        return new ResponseEntity<>(clienteId, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getFondosVinculados(String clienteId) {
        log.info("GET FONDOS VINCULADOS Cliente: "+clienteId);
        List<String> transacciones = new ArrayList<>();
        Cliente cliente = dynamoDBMapper.load(Cliente.class, clienteId);
        if(Objects.isNull(cliente)){
            log.error("FAILED");
            return new ResponseEntity<>(new Error("Fallo al obtener vinculaciones", "No se pudieron obtener las vinculaciones activas del cliente: "+clienteId), HttpStatus.NOT_FOUND);
        }else{
            transacciones = cliente.getVinculacionesActivas();
            log.info("SUCCESS");
        }
        List<String> fondosVinculados = new ArrayList<>();
        for(String transaccion: transacciones){
            Fondo fondo = (Fondo) fondoUseCase.getFondoByTransaction(transaccion).getBody();
            fondosVinculados.add(fondo.getFondoId());
        }
        return new ResponseEntity<>(fondosVinculados, HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity saveNuevaVinculacion(String clienteId, String tipoFondo) {
        log.info("SAVE NUEVA VINCULACION Cliente: "+clienteId+" Fondo: "+tipoFondo);
        Cliente cliente = (Cliente) getClienteById(clienteId).getBody();

        Fondo fondoById = (Fondo) fondoUseCase.getFondoById(tipoFondo).getBody();
        Integer valor = fondoById.getMontoMinimoVinculacion();
        if(cliente.getSaldo() - valor < 0){
            log.error("FAILED BALANCE NOT ENOUGH");
            return new ResponseEntity<>(new Error("Saldo insuficiente", "El cliente no puede vincularse al fondo debido a saldo insuficiente."), HttpStatus.PAYMENT_REQUIRED);
        }
        ResponseEntity<List<Fondo>> listaFondo = getFondosVinculados(cliente.getClienteId());
            if(listaFondo.getBody().contains(tipoFondo)){
                log.error("FAILED ALREADY LINKED");
                return new ResponseEntity<>(new Error("Ya vinculado", "El cliente no puede vincularse al fondo debido a que ya se encuenta vinculado."), HttpStatus.CONFLICT);
            }

        
        FondoCliente nuevaVinculacion = new FondoCliente();
        nuevaVinculacion.setClienteId(cliente.getClienteId());
        nuevaVinculacion.setFondoId(tipoFondo);
        nuevaVinculacion.setVinculo(Boolean.TRUE);
        nuevaVinculacion.setFechaActualizacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        dynamoDBMapper.save(nuevaVinculacion);

        cliente.setSaldo(cliente.getSaldo()-valor);
        List<String> vinculaciones = cliente.getVinculacionesActivas();
        vinculaciones.add(nuevaVinculacion.getTransactionId());
        cliente.setVinculacionesActivas(vinculaciones);
        updateCliente(cliente.getClienteId(), cliente);

        log.info("SUCCESS LINKING Transaction: "+nuevaVinculacion.getTransactionId());
        return new ResponseEntity<>(nuevaVinculacion, HttpStatus.OK);
    }

    @Override
    public ResponseEntity saveNuevaCancelacion(String clienteId, String vinculacionId){
        log.info("SAVE NUEVA CANCELACION Cliente: "+clienteId+" Transaction: "+vinculacionId);

        Cliente cliente = (Cliente) getClienteById(clienteId).getBody();

        if(!cliente.getVinculacionesActivas().contains(vinculacionId)){
            return new ResponseEntity<>(new Error("Fallo al cancelar", "El cliente no tiene una vinculacion activa"), HttpStatus.NOT_FOUND);
        }

        Fondo fondo = (Fondo) fondoUseCase.getFondoByTransaction(vinculacionId).getBody();

        FondoCliente nuevaVinculacion = new FondoCliente();
        nuevaVinculacion.setClienteId(cliente.getClienteId());
        nuevaVinculacion.setFondoId(fondo.getFondoId());
        nuevaVinculacion.setVinculo(Boolean.FALSE);
        nuevaVinculacion.setFechaActualizacion(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));

        dynamoDBMapper.save(nuevaVinculacion);

        cliente.setSaldo(cliente.getSaldo()+fondo.getMontoMinimoVinculacion());
        List<String> vinculaciones = cliente.getVinculacionesActivas();
        vinculaciones.remove(vinculacionId);
        cliente.setVinculacionesActivas(vinculaciones);
        updateCliente(cliente.getClienteId(), cliente);

        log.info("SUCCESS UNLINKING Transaction: "+nuevaVinculacion.getTransactionId());
        return new ResponseEntity<>(nuevaVinculacion, HttpStatus.OK);
    }

    @Override
    public ResponseEntity getHistoricoVinculaciones(String clienteId) {

        Cliente cliente = (Cliente) getClienteById(clienteId).getBody();

        List<Vinculo> listaVinculos = new ArrayList<>();

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(cliente.getClienteId()));
        List<FondoCliente> scanResult = dynamoDBMapper.scan(FondoCliente.class, new DynamoDBScanExpression()
                    .withFilterExpression("clienteId = :val1")
                    .withExpressionAttributeValues(eav));

        for (FondoCliente fondo : scanResult) {
            Fondo fondoById = (Fondo) fondoUseCase.getFondoById(fondo.getFondoId()).getBody();
            listaVinculos.add(
                    new Vinculo(
                            fondoById.getNombreFondo(),
                            fondo.getVinculo(),
                            fondo.getFechaActualizacion()
                    )
            );
        }

        return new ResponseEntity(listaVinculos, HttpStatus.OK);
    }
}
