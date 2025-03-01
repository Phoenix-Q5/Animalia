package com.animal.java.validation.pipeline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class ValidationNotificationType {

    public static final ValidationNotificationType GENERAL_VALIDATION = ValidationNotificationType.builder().build();

    @Builder.Default
    private String name =  "GENERAL_VALIDATION";

    @Builder.Default
    private String group = Group.CUSTOM.name();

    @Builder.Default
    private String type = Type.CUSTOM.name();


    @Getter
    @AllArgsConstructor
    public enum Group{
        ANIMAL, IMAGE, SCOPE, CUSTOM;
    }

    public enum Type{
        MANDATORY, OPTIONAL, CONDITIONAL, TRANSFORMATION, CUSTOM;
    }

    public static ValidationNotificationType create(String name, Group group, Type type){
        return ValidationNotificationType.builder().name(name).group(group.name()).type(type.name()).build();
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        ValidationNotificationType that = (ValidationNotificationType) o;
        return name.equals(that.name) && group.equals(that.group) && type.equals(that.type);
    }

    @Override
    public int hashCode(){
        return Objects.hash(name, group, type);
    }

    public static ValidationNotificationType fromGroup(Group group){
        return ValidationNotificationType.builder().group(group.name()).build();
    }
}
