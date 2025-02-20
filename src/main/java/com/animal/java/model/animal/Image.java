package com.animal.java.model.animal;

import lombok.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    private Long id;

    private String name;

    private String type;

    private byte[] imageArray;

    public Image(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .toString();
    }
}
