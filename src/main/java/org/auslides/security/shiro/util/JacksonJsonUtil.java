package org.auslides.security.shiro.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.io.Reader;

public class JacksonJsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper() ;

    static {
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) ;
        mapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static ObjectReader getObjectReader() {
        return mapper.reader();
    }

    public static ObjectWriter getObojectWriter() {
        return mapper.writerWithDefaultPrettyPrinter();
    }

    public static <T> T readValue(String jsonStr, Class<T> cls) throws IOException {
        T t = getMapper().readValue(jsonStr, cls) ;
        return t ;
    }

    public static <T> T readValue(Reader reader, Class<T> cls) throws IOException {
        T t =getMapper().readValue(reader, cls)  ;
        return t ;
    }

    public static <T> T convertValue(JsonNode node, Class<T> cls) {
        T t =getMapper().convertValue(node, cls) ;
        return t ;
    }

    public static <T> T convertValue(Object o, Class<T> cls) {
        T t =getMapper().convertValue(o, cls) ;
        return t ;
    }

    public static String writeValueAsString(Object o) throws JsonProcessingException {
        //String t = getObojectWriter().writeValueAsString(o) ;
        String t = mapper.writeValueAsString(o) ;
        return t ;
    }
}
