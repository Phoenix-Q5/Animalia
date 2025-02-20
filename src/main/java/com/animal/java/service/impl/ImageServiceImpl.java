package com.animal.java.service.impl;

import com.animal.java.dao.ImageRepository;
import com.animal.java.exception.AnimaliaException;
import com.animal.java.model.animal.Image;
import com.animal.java.service.ImageService;
import com.animal.java.utils.RandomUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Image uploadImage(MultipartFile file){
        try {
          return imageRepository.save(Image.builder()
                          .id(Long.valueOf(RandomUtils.getNumberReqLength(5)))
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .imageArray(file.getBytes()).build());
        }
        catch (IOException e){
            throw new AnimaliaException("Failed to upload image", e);
        }
    }

    @Transactional
    public Image getInfoByImageById(Long name) {
        Image dbImage = imageRepository.findById(name);

        return Image.builder()
                .name(dbImage.getName())
                .type(dbImage.getType())
                .imageArray(dbImage.getImageArray()).build();

    }

    @Transactional
    public byte[] getImage(Long id) {
        Image dbImage = imageRepository.findById(id);
        byte[] image = dbImage.getImageArray();
        return image;
    }

}
