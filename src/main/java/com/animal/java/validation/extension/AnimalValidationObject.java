package com.animal.java.validation.extension;

import com.animal.java.model.animal.Image;
import com.animal.java.validation.pipeline.ValidationObject;

import java.util.List;

public interface AnimalValidationObject extends ValidationObject {

    String getSpecies();

    String getScientificName();

    List<Image> getImages();
}
