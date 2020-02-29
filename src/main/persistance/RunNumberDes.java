package persistance;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import models.RunNumber;

import java.io.IOException;

public class RunNumberDes extends KeyDeserializer {
    @Override
    public Object deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
        return new RunNumber(Integer.parseInt(s.substring(0,s.indexOf("-"))), Integer.parseInt(s.substring(s.indexOf("-") + 1)));
    }
}
