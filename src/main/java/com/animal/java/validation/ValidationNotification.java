package com.animal.java.validation;

import com.animal.java.common.ErrorCode;
import com.animal.java.validation.pipeline.ValidationNotificationLabel;
import com.animal.java.validation.pipeline.ValidationNotificationType;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.animal.java.common.ErrorCode.VALIDATION_FAILURE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationNotification {

    @Getter
    @AllArgsConstructor
    public enum Level {
        ERROR(10),
        WARNING(5),
        INFO(0);

        private int priority;

        public boolean higherOrEqual(Level other){
            return priority >= other.priority;
        }
    }

    @Builder.Default
    private Level level = Level.ERROR;

    @Builder.Default
    private ErrorCode errorCode = VALIDATION_FAILURE;

    @Builder.Default
    private String message = VALIDATION_FAILURE.getDescription();

    private ValidationNotificationLabel label;

    @Builder.Default
    private ValidationNotificationType type = ValidationNotificationType.GENERAL_VALIDATION;

    @Builder.Default
    private Map<Class, String> idMap = new HashMap<>();

    private String attribute;
    private Object data;

    private Object failedObject;

    private  String getEntityId(Class<?> clazz){
        String result = idMap.get(clazz);

        if(result == null){
            for(Map.Entry<Class, String> entry: idMap.entrySet()){
                if(clazz.isAssignableFrom(entry.getKey())){
                    result= entry.getValue();
                    idMap.put(clazz, result);
                    break;
                }
            }
        }
        return result;
    }

    public ValidationNotification withEntityId(Class<?> clazz, String id){
        idMap.put(clazz, id);
        return this;
    }

    public ValidationNotificationLabel withLabel(ValidationNotificationLabel label){
        this.label = label;
        return label;
    }

    public ValidationNotification withLevel(Level level){
        this.level = level;
        return this;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        ValidationNotification that = (ValidationNotification) o;
        return errorCode == that.errorCode;
    }

    @Override
    public String  toString(){
      return "ValidationNotification{" +
              "level=" + level +
              ", type=" + type +
              ", attribute='" + attribute + '\'' +
              ", message='" + message + '\'' +
              '}';
    }

    @Override
    public int hashCode(){
      return Objects.hash(level, type, attribute, message);
    }

}
