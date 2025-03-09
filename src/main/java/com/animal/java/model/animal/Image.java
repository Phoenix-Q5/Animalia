package com.animal.java.model.animal;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

    private UUID id;

    protected String name;

    private String type;

    private byte[] imageArray;

    protected ZonedDateTime uploadedAt;

    protected UUID animalId;

    public Image(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .toString();
    }

//    public abstract void displayImageInfo();
}
