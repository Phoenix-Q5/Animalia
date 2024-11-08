package com.animal.java.qa.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TestAnimaliaUser {
    ADMIN("admin"),
    SUPER("super"),
    ALL("all"),
    TEST("test"),
    VIEW("view");

    private final String user;

    @Override
    public String toString(){
        return user;
    }

    public static  TestAnimaliaUser fromString(String user){
        for(TestAnimaliaUser u : TestAnimaliaUser.values()){
            if(u.user.equalsIgnoreCase(user)){
                return u;
            }
        }
        return null;
    }
}
