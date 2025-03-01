package com.animal.java.model.animal;

import java.time.ZonedDateTime;
import java.util.UUID;

public class IndexImage  extends Image{

    private UUID id;
    private UUID animalId;

    public IndexImage(UUID id, String name, String type, byte[] imageData, ZonedDateTime uploadedAt) {
        super(id, name, type,imageData, uploadedAt);
    }

    @Override
    public void displayImageInfo() {
        System.out.println("Index Image: " + name + " uploaded at " + uploadedAt);
    }
}
