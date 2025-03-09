package com.animal.java.dao;

import com.animal.java.annotations.JooqConfiguration;
import com.animal.java.annotations.TransactionRequired;
import com.animal.java.mapper.AnimalMapper;
import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.Image;
import com.animal.jooq.generated.tables.JAnimal;
import com.animal.jooq.generated.tables.daos.JAnimalDao;
import com.animal.jooq.generated.tables.daos.JImageDao;
import org.jooq.Record;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.animal.jooq.generated.tables.JImage.IMAGE;

@Component
@TransactionRequired
public class AnimalRepository {

    private final DefaultDSLContext dsl;

    private final JAnimalDao animalDao;

    private final JImageDao imageDao;

    private final AnimalMapper mapper;

    private final JAnimal ANIMAL_ = JAnimal.ANIMAL;

    private final ImageRepository imageRepository;

    public AnimalRepository(@JooqConfiguration DefaultConfiguration create, ImageRepository imageRepository, AnimalMapper mapper){
        dsl = new DefaultDSLContext(create);
        animalDao = new JAnimalDao(create);
        imageDao = new JImageDao(create);
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    public List<Animal> findAll(){
        return dsl.selectFrom(ANIMAL_).fetchInto(Animal.class);
    }

    public Animal getById(UUID id){
        List<Record> records = dsl.select()
                .from(ANIMAL_)
                .leftJoin(IMAGE).on(IMAGE.ANIMAL_ID.eq(ANIMAL_.ID))
                .where(ANIMAL_.ID.eq(id))
                .fetch();

        if(records.isEmpty()) return null;

        Record record = records.get(0);
        Animal animal = record.into(Animal.class);

        List<Image> images = records.stream()
                .filter(r -> r.get(IMAGE.ID) != null)
                .map(r -> r.into(Image.class))
                .collect(Collectors.toUnmodifiableList());

        animal.setAnimalImages(images);
        return animal;
    }

    public Animal addAnimal(Animal animal){
        animalDao.insert(mapper.toJooq(animal));
        return animal;
    }

    public void deleteAnimalById(UUID id){
        dsl.deleteFrom(ANIMAL_).where(ANIMAL_.ID.eq(id)).execute();
    }
}
