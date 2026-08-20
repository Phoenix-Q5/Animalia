package com.animal.java.dao;

import com.animal.java.annotations.JooqConfiguration;
import com.animal.java.annotations.TransactionRequired;
import com.animal.java.mapper.AnimalMapper;
import com.animal.java.model.animal.Image;
import com.animal.jooq.generated.tables.JImage;
import com.animal.jooq.generated.tables.daos.JImageDao;
import org.jooq.Condition;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.animal.java.dao.schema.AnimalDetailTables.IMAGE_CLOUDINARY_PUBLIC_ID;
import static com.animal.java.dao.schema.AnimalDetailTables.IMAGE_URL;

@Component
@TransactionRequired
public class ImageRepository {

    private final DefaultDSLContext dsl;
    private final JImage IMAGE = JImage.IMAGE;
    private final AnimalMapper mapper;
    private final JImageDao imageDao;

    public ImageRepository(@JooqConfiguration DefaultConfiguration create, AnimalMapper mapper) {
        dsl = new DefaultDSLContext(create);
        imageDao = new JImageDao(create);
        this.mapper = mapper;
    }

    public Image getById(UUID id) {
        return fetchImages(IMAGE.ID.eq(id)).stream().findFirst().orElse(null);
    }

    public List<Image> getAllImages() {
        return fetchImages(null);
    }

    public List<Image> getByType(String type) {
        return fetchImages(IMAGE.TYPE.eq(type));
    }

    public List<Image> getByAnimalId(UUID animalId) {
        return fetchImages(IMAGE.ANIMAL_ID.eq(animalId));
    }

    public Image addImage(Image image) {
        persist(image);
        return image;
    }

    public Image addImageByAnimalId(UUID animalId, Image image) {
        image.setAnimalId(animalId);
        return addImage(image);
    }

    public List<Image> addImages(List<Image> images) {
        if (images == null) {
            return List.of();
        }
        images.forEach(this::persist);
        return images;
    }

    public Image updateImage(UUID id, Image image) {
        image.setId(id);
        imageDao.update(mapper.toJooqImage(image));
        dsl.update(IMAGE)
                .set(IMAGE_URL, image.getUrl())
                .set(IMAGE_CLOUDINARY_PUBLIC_ID, image.getCloudinaryPublicId())
                .where(IMAGE.ID.eq(id))
                .execute();
        return image;
    }

    public List<Image> updateImages(List<Image> images) {
        if (images == null) {
            return List.of();
        }
        images.forEach(image -> updateImage(image.getId(), image));
        return images;
    }

    public void deleteImage(UUID id) {
        imageDao.deleteById(id);
    }

    public void deleteImages(List<UUID> ids) {
        imageDao.deleteById(ids);
    }

    private List<Image> fetchImages(Condition condition) {
        // Explicitly include CDN columns (not present on generated JImage table yet).
        var query = dsl.select(
                        IMAGE.ID,
                        IMAGE.NAME,
                        IMAGE.TYPE,
                        IMAGE.IMAGEARRAY,
                        IMAGE.UPLOADEDAT,
                        IMAGE.ANIMAL_ID,
                        IMAGE_URL,
                        IMAGE_CLOUDINARY_PUBLIC_ID
                )
                .from(IMAGE);
        if (condition != null) {
            return query.where(condition).fetch(mapper::fromImageRecord);
        }
        return query.fetch(mapper::fromImageRecord);
    }

    private void persist(Image image) {
        if (image.getId() == null) {
            image.setId(UUID.randomUUID());
        }
        if (image.getUploadedAt() == null) {
            image.setUploadedAt(ZonedDateTime.now());
        }
        if (image.getType() == null || image.getType().isBlank()) {
            image.setType("image/jpeg");
        }
        if (image.getName() == null || image.getName().isBlank()) {
            image.setName(image.hasCdnUrl() ? "cdn-image" : "image");
        }

        Timestamp uploadedAt = mapper.toJooqImage(image).getUploadedat();
        dsl.insertInto(IMAGE)
                .columns(
                        IMAGE.ID,
                        IMAGE.NAME,
                        IMAGE.TYPE,
                        IMAGE.IMAGEARRAY,
                        IMAGE.UPLOADEDAT,
                        IMAGE.ANIMAL_ID,
                        IMAGE_URL,
                        IMAGE_CLOUDINARY_PUBLIC_ID
                )
                .values(
                        image.getId(),
                        image.getName(),
                        image.getType(),
                        image.getImageArray(),
                        uploadedAt,
                        image.getAnimalId(),
                        image.getUrl(),
                        image.getCloudinaryPublicId()
                )
                .execute();
    }
}
