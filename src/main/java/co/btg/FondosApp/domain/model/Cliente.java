package co.btg.FondosApp.domain.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "Clientes")
public class Cliente {

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    private String clienteId;

    @DynamoDBAttribute
    private String nombre;

    @DynamoDBAttribute
    private String apellido;

    @DynamoDBAttribute
    private Integer saldo;

    @DynamoDBAttribute
    private List<String> vinculacionesActivas;
}

