package com.animal.java.utils.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

public class ObjectJsonSerializer<T> extends JsonDeserializer<Set<T>> {

    private final Class<T> valueType;

    public ObjectJsonSerializer(){
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.valueType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    @Override
    public Set<T> deserialize(JsonParser p, DeserializationContext context) throws IOException {
        ObjectCodec objectCodec = p.getCodec();
        JsonNode tree = objectCodec.readTree(p);
        Set<T> result = new HashSet<>();
        if(tree.isArray()){
            for (JsonNode node : tree){
                result.add(objectCodec.treeToValue(node, valueType));
            }
        }else {
            result.add(objectCodec.treeToValue(tree, valueType));
        }
        return  result;
    }

    public static class AsString extends ObjectJsonSerializer<String>{}
}
