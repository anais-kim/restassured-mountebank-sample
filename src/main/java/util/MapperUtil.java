package util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class MapperUtil {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public static String writeValueAsString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException("MapperUtil fail to parse object to string value.", e.getCause());
        }
    }
}
