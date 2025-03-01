package com.animal.java.model.animal;

import java.time.ZonedDateTime;

public class IndexImage  extends Image{

    public IndexImage(int id, String name, String type, byte[] imageData, ZonedDateTime uploadedAt) {
        super(id, name, type,imageData, uploadedAt);
    }

    @Override
    public void displayImageInfo() {
        System.out.println("Index Image: " + name + " uploaded at " + uploadedAt);
    }
}
