package com.animal.java.model.animal;

import java.time.ZonedDateTime;

public class GalleryImage extends Image{
    public GalleryImage(int id, String name, String type, byte[] imageData, ZonedDateTime uploadedAt) {
        super(id, name, type, imageData, uploadedAt);
    }

    @Override
    public void displayImageInfo() {
        System.out.println("Gallery Image: " + name + " uploaded at " + uploadedAt);
    }

}
