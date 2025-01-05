package com.animal.java.dao;

import com.animal.java.annotations.JooqConfiguration;
import com.animal.java.annotations.TransactionRequired;
import com.animal.java.mapper.AnimalMapper;
import com.animal.java.model.Animal;
import com.animal.java.model.Image;
import com.animal.jooq.generated.tables.daos.JAnimalDao;
import com.animal.jooq.generated.tables.daos.JImageDao;
import com.animal.jooq.generated.tables.pojos.JAnimal;
import com.animal.jooq.generated.tables.pojos.JImage;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.animal.java.utils.ByteUtils.longToBytes;
import static com.animal.jooq.generated.tables.JAnimal.ANIMAL_;
import static com.animal.jooq.generated.tables.JImage.IMAGE;

@Component
@TransactionRequired
public class AnimalRepository {

    private final DefaultDSLContext dsl;

    private final JAnimalDao animalDao;

    private final JImageDao imageDao;

    private final AnimalMapper mapper;

    private final ImageRepository imageRepository;

    public AnimalRepository(@JooqConfiguration DefaultConfiguration create, ImageRepository imageRepository, AnimalMapper mapper){
        dsl = new DefaultDSLContext(create);
        animalDao = new JAnimalDao(create);
        imageDao = new JImageDao(create);
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    public List<Animal> findAll(){
       return mapper.fullAnimal(dsl.select(ANIMAL_.fields()).from(ANIMAL_).fetchInto(JAnimal.class),
                dsl.select(IMAGE.fields()).from(IMAGE).fetchInto(JImage.class));
    }


    public Animal findById(String keyword){
        JAnimal a = animalDao.findById(keyword);
        JImage i = imageDao.findById(a.getImageId());
        return mapper.fullAnimal(a,i);
    }

    public Animal save(Animal animal){
        animalDao.insert(mapper.toJooq(animal));
        imageDao.insert(mapper.toJooqImage(animal.getAnimalImage()));
        return animal;
    }
}
