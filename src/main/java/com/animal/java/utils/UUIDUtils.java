package com.animal.java.utils;

import com.animal.java.exception.AnimaliaException;
import com.animal.java.exception.error.ErrorCode;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UUIDUtils {

    public static final String EMPTY_UUID = "00000000-0000-0000-0000-000000000000";

    private static final UUIDSerializer UUID_SERIALIZER = new UUIDSerializer();

    public static <T> T notEmpty(T id) throws AnimaliaException{
        return notEmpty(id, "Empty id");
    }

    public static <T> T notEmpty(T id, String message) throws AnimaliaException{
        if(isEmpty(id)){
            throw new AnimaliaException(ErrorCode.INTERNAL_SERVER_ERROR, message);
        }
        return id;
    }

    public static boolean isEmpty(Object id) throws AnimaliaException {
        return id == null
                || "".equals(id)
                || EMPTY_UUID.equals(id)
                || id instanceof UUID && UUID_SERIALIZER.isEmpty(null, (UUID) id);
    }

    public static UUID newID(){
        return UUID.randomUUID();
    }

    public static String newStringId(){
        return String.valueOf(newID());
    }

    public static boolean isUUID(String uuid){
        try{
            if(uuid.length() > 36){
                uuid = uuid.substring(0,36);
            }
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    public static UUID newIfEmpty(UUID uuid){
        return isEmpty(uuid) ? newID() : uuid;
    }

    public static String toStr (UUID uuid){
        return uuid != null ? uuid.toString() : null;
    }

    public static UUID toUUID (String uuid) throws AnimaliaException {
        if(StringUtils.isBlank(uuid)){
            return null;
        }
        if(uuid.length() >36){
            uuid = uuid.substring(0,36);
        }
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e){
            throw new AnimaliaException(ErrorCode.UUID_FORMAT_ERROR, new Object[] {uuid});
        }
    }
}
