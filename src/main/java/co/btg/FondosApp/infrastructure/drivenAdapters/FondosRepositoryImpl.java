package co.btg.FondosApp.infrastructure.drivenAdapters;

import co.btg.FondosApp.domain.model.Fondo;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FondosRepositoryImpl implements FondosRepository{

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public Fondo saveFondo(Fondo fondo) {
        dynamoDBMapper.save(fondo);
        System.out.println("Saved w/ id: "+fondo.getFondoId());
        return fondo;
    }

    public Fondo getFondoById(String fondoId) {
        return dynamoDBMapper.load(Fondo.class, fondoId);
    }

    public String delete(String fondoId) {
        Fondo emp = dynamoDBMapper.load(Fondo.class, fondoId);
        dynamoDBMapper.delete(emp);
        return "Employee Deleted!";
    }

    public String update(String fondoId, Fondo fondo) {
        dynamoDBMapper.save(fondo,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("fondoId",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(fondoId)
                                )));
        return fondoId;
    }

    @Override
    public List<Fondo> getAllFondosFPV() {

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS("FPV"));

        List<Fondo> scanResult = dynamoDBMapper.scan(Fondo.class, new DynamoDBScanExpression()
                .withFilterExpression("categoriaFondo = :val1")
                .withExpressionAttributeValues(eav));

        for (Fondo fondo : scanResult) {
            System.out.println(fondo.getNombreFondo());
        }

        return scanResult;
    }
}
