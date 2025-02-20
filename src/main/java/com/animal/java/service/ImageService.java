package com.animal.java.service;

import com.animal.java.model.animal.Image;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    Image uploadImage(MultipartFile file);
    Image getInfoByImageById(Long name);
    byte[] getImage(Long id);
}
