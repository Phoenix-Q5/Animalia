package com.animal.java.dao;

import com.animal.java.annotations.JooqConfiguration;
import com.animal.java.annotations.TransactionRequired;
import com.animal.java.mapper.AnimalMapper;
import com.animal.java.model.animal.Image;
import com.animal.jooq.generated.tables.JImage;
import com.animal.jooq.generated.tables.daos.JImageDao;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@TransactionRequired
public class ImageRepository {

    private final DefaultDSLContext dsl;

    private final JImage IMAGE = JImage.IMAGE;

    private final AnimalMapper mapper;

    private final JImageDao imageDao;

    public ImageRepository(@JooqConfiguration DefaultConfiguration create, AnimalMapper mapper){
        dsl = new DefaultDSLContext(create);
        imageDao = new JImageDao(create);
        this.mapper = mapper;
    }

    public Image getById(UUID id){
        return dsl.selectFrom(IMAGE).where(IMAGE.ID.eq(id)).fetchOneInto(Image.class);
    }

    public List<Image> getAllImages(){
        return dsl.selectFrom(IMAGE).fetchInto(Image.class);
    }

    public List<Image> getByType(String type){
        return dsl.selectFrom(IMAGE).where(IMAGE.TYPE.eq(type)).fetchInto(Image.class);
    }

    public Image addImage(Image image){
        imageDao.insert(mapper.toJooqImage(image));
        return image;
    }

    public List<Image> addImages(List<Image> images){
        imageDao.insert(images.stream().map(mapper::toJooqImage).toList());
        return images;
    }

    public Image updateImage(UUID id, Image image){
        imageDao.update(mapper.toJooqImage(image));
        return image;
    }

    public List<Image> updateImages(List<Image> images){
        imageDao.update(images.stream().map(mapper::toJooqImage).toList());
        return images;
    }

    public void deleteImage(UUID id){
        imageDao.deleteById(id);
    }

    public void deleteImages(List<UUID> ids){
        imageDao.deleteById(ids);
    }

}
