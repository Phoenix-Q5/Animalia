package com.animal.java.model.animal;

import java.time.ZonedDateTime;
import java.util.UUID;

public class GalleryImage extends Image {

    public GalleryImage() {
        super();
    }

    public GalleryImage(UUID id, String name, String type, byte[] imageData, ZonedDateTime uploadedAt, UUID animalId) {
        super();
        setId(id);
        setName(name);
        setType(type);
        setImageArray(imageData);
        setUploadedAt(uploadedAt);
        setAnimalId(animalId);
    }
}
