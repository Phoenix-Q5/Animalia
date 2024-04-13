package com.animal.java.dao;

import com.animal.java.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, String>, JpaSpecificationExecutor<Animal> {

    List<Animal> findAll();
}
