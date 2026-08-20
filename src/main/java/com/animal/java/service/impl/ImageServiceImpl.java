package com.animal.java.service.impl;

import com.animal.java.dao.ImageRepository;
import com.animal.java.model.animal.Image;
import com.animal.java.service.CloudinaryStorageService;
import com.animal.java.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final CloudinaryStorageService cloudinaryStorageService;

    @Override
    public Image getById(UUID id) {
        return imageRepository.getById(id);
    }

    @Override
    public List<Image> getByType(String type) {
        return imageRepository.getByType(type);
    }

    @Override
    public List<Image> getAllImages() {
        return imageRepository.getAllImages();
    }

    @Override
    public Image addImage(Image image) {
        if (image != null && image.hasCdnUrl() && image.getId() == null) {
            image = cloudinaryStorageService.fromUrl(image.getUrl(), image.getName());
        }
        return imageRepository.addImage(image);
    }

    public Image uploadImage(MultipartFile file) {
        Image uploaded = cloudinaryStorageService.upload(file, "animalia/uploads");
        return imageRepository.addImage(uploaded);
    }

    @Override
    public List<Image> addImages(List<Image> images) {
        return imageRepository.addImages(images);
    }

    @Override
    public Image updateImage(UUID id, Image image) {
        return imageRepository.updateImage(id, image);
    }

    @Override
    public List<Image> updateImages(List<Image> images) {
        return imageRepository.updateImages(images);
    }

    @Override
    public void deleteImage(UUID id) {
        imageRepository.deleteImage(id);
    }

    @Override
    public void deleteImages(List<UUID> ids) {
        imageRepository.deleteImages(ids);
    }
}
