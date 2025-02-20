package com.animal.java.dao;

import com.animal.java.annotations.JooqConfiguration;
import com.animal.java.annotations.TransactionRequired;
import com.animal.java.mapper.AnimalMapper;
import com.animal.java.model.animal.Image;
import com.animal.jooq.generated.tables.daos.JImageDao;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Component;

@Component
@TransactionRequired
public class ImageRepository {

    private final DefaultDSLContext dsl;

    private final JImageDao imageDao;

    private final AnimalMapper mapper;

    public ImageRepository(@JooqConfiguration DefaultConfiguration create, AnimalMapper mapper){
        dsl = new DefaultDSLContext(create);
        imageDao = new JImageDao(create);
        this.mapper = mapper;
    }

    public Image save(Image image){
        return image;
    }

    public Image findById(Long id){
       return mapper.fromJooqImage(imageDao.findById(id));
    }

}
