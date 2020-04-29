package persistance;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import models.RunNumber;

import java.io.IOException;

/**
 * A special Deserializer for the RunNumber class. It uses the To String class to know deserialize.
 *
 * As of this moment, the numbers before the "-" are the team number and the numbers after are the heat number.
 */
public class RunNumberDes extends KeyDeserializer {
    @Override
    public Object deserializeKey(String s, DeserializationContext deserializationContext) {
        return new RunNumber(Integer.parseInt(s.substring(0,s.indexOf("-"))), Integer.parseInt(s.substring(s.indexOf("-") + 1)));
    }
}
