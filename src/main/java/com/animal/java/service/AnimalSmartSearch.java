package com.animal.java.service;

import com.animal.java.model.Animal;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimalSmartSearch {

    public static Specification<Animal> smartSearch(String keyword){
        return (root, query, criteriaBuilder) -> {
            if(Objects.isNull(keyword) || ObjectUtils.isEmpty(keyword)){
                return criteriaBuilder.conjunction();
            }

            List<Predicate> searchPredicates = new ArrayList<>();
            searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("breed")), "%" + keyword.toLowerCase() + "%"));
            searchPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("earTag")), "%" + keyword.toLowerCase() + "%"));
            searchPredicates.add(criteriaBuilder.equal(root.get("age"), keyword));
            searchPredicates.add(criteriaBuilder.equal(root.get("weight"), keyword));

            return criteriaBuilder.or(searchPredicates.toArray(new Predicate[0]));
        };
    }
}
