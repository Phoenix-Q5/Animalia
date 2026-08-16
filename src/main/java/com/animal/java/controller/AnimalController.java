package com.animal.java.controller;

import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.Image;
import com.animal.java.model.enumConstants.*;
import com.animal.java.service.AnimalBulkImportService;
import com.animal.java.service.AnimalService;
import com.animal.java.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AnimalController {

    private final AnimalService animalService;

    private final ImageService imageService;

    private final AnimalBulkImportService animalBulkImportService;


    @GetMapping("/getAllAnimals")
    public List<Animal> getAllAnimals(){
        return animalService.getAllAnimals();
    }
//
    @PostMapping("/addAnimal")
    public Animal addAnimal(@RequestBody Animal animal){
        return animalService.insertAnimal(animal);
    }

    @PostMapping(value = {"/addCompleteAnimal", "/admin/addCompleteAnimal"}, consumes = "multipart/form-data")
    public ResponseEntity<?> addAnimalComplete(@RequestPart("animal") Animal animal, @RequestParam("image") MultipartFile file){
        Animal animalToSave = animalService.insertAnimal(animal, file);
        return ResponseEntity.ok(animalToSave);
    }

    @PostMapping(value = "/admin/bulkImport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkImport(@RequestParam("file") MultipartFile file){
        return ResponseEntity.ok(animalBulkImportService.importAnimals(file));
    }

    @GetMapping("/admin/bulkTemplate")
    public ResponseEntity<byte[]> bulkTemplate(){
        byte[] template = animalBulkImportService.canonicalTemplate();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"animalia-bulk-template.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(template);
    }

    @GetMapping("/getImage/{id}")
    public ResponseEntity<?> getImageByName(@PathVariable("id") UUID id){
        Image image = imageService.getById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image.getImageArray());
    }

    @GetMapping("/getById/{id}")
    public Animal getAnimalById(@PathVariable("id") UUID id){
        return animalService.getAnimalById(id);
    }

    @GetMapping("/getByName/{name}")
    public List<Animal> getAnimalsByName(@PathVariable("name") String name){
        return animalService.getAnimalByName(name);
    }

    @PostMapping("/addImage")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
//        Image response = imageService.uploadImage(file.);
//
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(response);
        return null;
    }

    @GetMapping("/getPhylum")
    public Set<String> phylumList(){
        return Arrays.stream(Phylum.values()).map(Phylum::getDescription).collect(Collectors.toSet());
    }

    @GetMapping("/getFamily")
    public Set<String> familyList(){
        return Arrays.stream(Family.values()).map(Family::getId).collect(Collectors.toSet());
    }

    @GetMapping("/getClass")
    public Set<String> classList(){
        return Arrays.stream(AnimalClass.values()).map(AnimalClass::getDescription).collect(Collectors.toSet());
    }

    @GetMapping("/getKingdoms")
    public Set<String> kingdomList(){
        return Arrays.stream(Kingdom.values()).map(Kingdom::getId).collect(Collectors.toSet());
    }


//    @GetMapping("/searchAnimals")
//    public List<Animal> getAnimals(@RequestParam(value = "search", required = false) String smartSearch){
//        return animalService.smartAnimalSearch(smartSearch);
//    }

    @DeleteMapping
    public ResponseEntity<?> deleteAnimal(@RequestParam("id") UUID id){
        animalService.deleteAnimal(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getAnimalClass")
    public Map<String, String> getAnimalClass(){
        Map<String, String> animalClass = new HashMap<>();
        Arrays.stream(AnimalClass.values()).forEach(animal -> animalClass.put(animal.getId(), animal.getDescription()));
        return animalClass;
    }

    @GetMapping("/getAnimalCollections")
    public Map<String, String> getAnimalCollections(){
        Map<String, String> animalCollections = new HashMap<>();
        Arrays.stream(Family.values()).forEach(animal -> animalCollections.put(animal.getId(), animal.getExample()));
        return animalCollections;
    }

    @GetMapping("/getAnimalClassifications")
    public Map<String, String> getAnimalClassifications(){
        Map<String, String> animalClassifications = new HashMap<>();
        Arrays.stream(Classification.values()).forEach(animal -> animalClassifications.put(animal.getId(), animal.getDescription()));
        return animalClassifications;
    }

    @GetMapping("/getAnimalBiomes")
    public Map<String, String> getAnimalBiomes(){
        Map<String, String> animalBiomes = new HashMap<>();
        Arrays.stream(BiomeId.values()).forEach(animal -> animalBiomes.put(animal.getId(), animal.getDescription()));
        return animalBiomes;
    }

    @GetMapping("/getAnimalClimateZones")
    public Map<String, String> getAnimalClimateZones(){
        Map<String, String> animalClimateZones = new HashMap<>();
        Arrays.stream(ClimateZone.values()).forEach(animal -> animalClimateZones.put(animal.getId(), animal.getDescription()));
        return animalClimateZones;
    }

}
