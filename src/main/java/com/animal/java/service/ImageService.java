package com.animal.java.service;

import com.animal.java.dao.ImageRepository;
import com.animal.java.exception.AnimaliaException;
import com.animal.java.model.Image;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.animal.java.utils.ImageUtils.compressImage;
import static com.animal.java.utils.ImageUtils.decompressImage;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    @Transactional
    public Image uploadImage(MultipartFile file){
        try {
          return imageRepository.save(Image.builder()
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .image(compressImage(file.getBytes())).build());
        }
        catch (IOException e){
            throw new AnimaliaException("Failed to upload image", e);
        }
    }

    @Transactional
    public Image getInfoByImageById(Long name) {
        Optional<Image> dbImage = imageRepository.findById(name);

        return Image.builder()
                .name(dbImage.get().getName())
                .type(dbImage.get().getType())
                .image(decompressImage(dbImage.get().getImage())).build();

    }

    @Transactional
    public byte[] getImage(Long id) {
        Optional<Image> dbImage = imageRepository.findById(id);
        byte[] image = decompressImage(dbImage.get().getImage());
        return image;
    }

}
