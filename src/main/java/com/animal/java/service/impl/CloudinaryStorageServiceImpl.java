package com.animal.java.service.impl;

import com.animal.java.exception.AnimaliaException;
import com.animal.java.model.animal.Image;
import com.animal.java.service.CloudinaryStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryStorageServiceImpl implements CloudinaryStorageService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:animalia}")
    private String defaultFolder;

    @Override
    public Image upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new AnimaliaException("Image file is empty");
        }
        assertConfigured();
        String targetFolder = StringUtils.defaultIfBlank(folder, defaultFolder);
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", targetFolder,
                            "resource_type", "image",
                            "use_filename", true,
                            "unique_filename", true,
                            "overwrite", false
                    )
            );
            String url = stringVal(result.get("secure_url"));
            if (StringUtils.isBlank(url)) {
                url = stringVal(result.get("url"));
            }
            String publicId = stringVal(result.get("public_id"));
            String format = stringVal(result.get("format"));
            String contentType = file.getContentType();
            if (StringUtils.isBlank(contentType) && StringUtils.isNotBlank(format)) {
                contentType = "image/" + format;
            }

            return Image.builder()
                    .id(UUID.randomUUID())
                    .name(StringUtils.defaultIfBlank(file.getOriginalFilename(), publicId))
                    .type(StringUtils.defaultIfBlank(contentType, "image/jpeg"))
                    .url(url)
                    .cloudinaryPublicId(publicId)
                    .uploadedAt(ZonedDateTime.now())
                    .build();
        } catch (IOException e) {
            throw new AnimaliaException("Failed to upload image to Cloudinary", e);
        }
    }

    @Override
    public Image fromUrl(String url, String name) {
        if (StringUtils.isBlank(url)) {
            throw new AnimaliaException("Image URL is required");
        }
        String resolvedName = StringUtils.defaultIfBlank(name, extractName(url));
        return Image.builder()
                .id(UUID.randomUUID())
                .name(resolvedName)
                .type(guessType(url))
                .url(url.trim())
                .uploadedAt(ZonedDateTime.now())
                .build();
    }

    private void assertConfigured() {
        Object cloudName = cloudinary.config.cloudName;
        if (cloudName == null || String.valueOf(cloudName).isBlank()) {
            throw new AnimaliaException(
                    "Cloudinary is not configured. Set CLOUDINARY_URL or cloudinary.url "
                            + "(e.g. in application-local.properties)");
        }
    }

    private static String extractName(String url) {
        int slash = url.lastIndexOf('/');
        if (slash >= 0 && slash < url.length() - 1) {
            String tail = url.substring(slash + 1);
            int q = tail.indexOf('?');
            return q >= 0 ? tail.substring(0, q) : tail;
        }
        return "cdn-image";
    }

    private static String guessType(String url) {
        String lower = url.toLowerCase();
        if (lower.contains(".png")) {
            return "image/png";
        }
        if (lower.contains(".webp")) {
            return "image/webp";
        }
        if (lower.contains(".gif")) {
            return "image/gif";
        }
        return "image/jpeg";
    }

    private static String stringVal(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
