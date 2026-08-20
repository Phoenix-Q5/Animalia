package com.animal.java.service;

import com.animal.java.model.animal.Image;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryStorageService {

    /**
     * Uploads a file to Cloudinary and returns an Image with url + public id set.
     * Binary bytes are not persisted — only the CDN URL.
     */
    Image upload(MultipartFile file, String folder);

    /**
     * Registers an existing CDN URL as an Image (no upload).
     */
    Image fromUrl(String url, String name);
}
