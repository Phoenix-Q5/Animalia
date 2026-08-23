package com.animal.java.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "#{@animalIndexName}")
@Setting(shards = 1, replicas = 0)
public class AnimalDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Keyword)
    private String nameKeyword;

    @Field(type = FieldType.Keyword)
    private String nameLetter;

    @Field(type = FieldType.Text)
    private String scientificName;

    @Field(type = FieldType.Keyword)
    private String scientificNameKeyword;

    @Field(type = FieldType.Text)
    private String phylum;

    @Field(type = FieldType.Text)
    private String family;

    @Field(type = FieldType.Text)
    private String genus;

    @Field(type = FieldType.Text)
    private String species;

    @Field(type = FieldType.Keyword)
    private String speciesKeyword;

    @Field(type = FieldType.Keyword)
    private String kingdom;

    @Field(type = FieldType.Keyword)
    private String animalClass;

    @Field(type = FieldType.Keyword)
    private String order;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String diet;

    @Field(type = FieldType.Text)
    private String habitat;

    @Field(type = FieldType.Keyword)
    private String habitatKeyword;

    @Field(type = FieldType.Text)
    private String behavior;

    @Builder.Default
    @Field(type = FieldType.Keyword)
    private List<String> biomes = new ArrayList<>();

    @Builder.Default
    @Field(type = FieldType.Keyword)
    private List<String> climateZones = new ArrayList<>();

    @Field(type = FieldType.Keyword)
    private String conservationStatus;

    @Field(type = FieldType.Keyword)
    private String populationTrend;

    @Field(type = FieldType.Keyword)
    private String imageUrl;

    @Field(type = FieldType.Keyword)
    private String imageName;

    @Field(type = FieldType.Keyword)
    private String imageType;
}
