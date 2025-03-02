package com.animal.java.service;

import com.animal.java.model.animal.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    Image getById(UUID id);

    List<Image> getByType(String type);

    List<Image> getAllImages();

    Image addImage(Image image);

    List<Image> addImages(List<Image> images);

    Image updateImage(UUID id, Image image);

    List<Image> updateImages(List<Image> images);

    void deleteImage(UUID id);

    void deleteImages(List<UUID> ids);
}
