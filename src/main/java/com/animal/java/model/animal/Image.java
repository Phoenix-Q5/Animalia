package com.animal.java.model.animal;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.ZonedDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Image {

    private int id;

    protected String name;

    private String type;

    private byte[] imageArray;

    protected ZonedDateTime uploadedAt;

    public Image(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .toString();
    }

    public abstract void displayImageInfo();
}
