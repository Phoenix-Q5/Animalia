package com.animal.java.utils;

import java.util.UUID;

public class UUIDUtils {

    public static UUID newID(){
        return UUID.randomUUID();
    }

    public static String newStringId(){
        return String.valueOf(newID());
    }

    public static boolean isUUID(){
        return false;
    }
}
