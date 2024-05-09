package co.btg.FondosApp.domain.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "FondoCliente")
public class FondoCliente {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String transactionId;

    @DynamoDBAttribute
    private String fondoId;

    @DynamoDBAttribute
    private String clienteId;

    @DynamoDBAttribute
    private Date fechaActualizacion;

    @DynamoDBAttribute
    private Boolean vinculo;
    //True: vinculación, False: cancelación



}