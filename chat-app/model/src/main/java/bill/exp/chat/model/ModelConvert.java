package bill.exp.chat.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;

public class ModelConvert {

    final private static ObjectMapper JSON_MAPPER = createDefaultMapper();

    private static ObjectMapper createDefaultMapper() {

        final ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static <T> T deserialize(Reader reader, Class<T> resultType) throws IOException {

        return JSON_MAPPER.readValue(reader, resultType);
    }

    public static <T> T deserialize(String content, Class<T> resultType) throws IOException {

        return deserialize(new StringReader(content), resultType);
    }

    public static Writer serialize(Writer writer, Object value) throws IOException {

        JSON_MAPPER.writeValue(writer, value);
        return writer;
    }

    public static String serialize(Object value) throws IOException {

        return serialize(new StringWriter(), value).toString();
    }
}
