package com.animal.java.controller;

import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.Image;
import com.animal.java.model.enumConstants.AnimalClass;
import com.animal.java.model.enumConstants.Family;
import com.animal.java.model.enumConstants.Kingdom;
import com.animal.java.model.enumConstants.Phylum;
import com.animal.java.service.AnimalService;
import com.animal.java.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/animals")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class AnimalController {

    private final AnimalService animalService;

    private final ImageService imageService;


    @GetMapping("/getAllAnimals")
    public List<Animal> getAllAnimals(){
        return animalService.getAllAnimals();
    }

    @PostMapping("/admin/addAnimal")
    public Animal addAnimal(@RequestBody Animal animal){
        return animalService.addAnimal(animal);
    }

    @PostMapping(value = "/admin/addCompleteAnimal", consumes = "multipart/form-data")
    public ResponseEntity<?> addAnimalComplete(@RequestPart("animal") Animal animal, @RequestParam("image") MultipartFile file){
//        Image animalImage = imageService.uploadImage(file);
//        animal.setAnimalImages(List.of(animalImage));
        Animal animalToSave = animalService.addAnimal(animal);
        return ResponseEntity.ok(animalToSave);
    }

    @GetMapping("/getImage/{id}")
    public ResponseEntity<?> getImageByName(@PathVariable("id") Long id){
        byte[] image = imageService.getImage(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(image);
    }

    @GetMapping("/getById/{id}")
    public Animal getAnimalById(@PathVariable("id") String id){
        return animalService.getAnimalById(id);
    }

    @GetMapping("/getByName/{name}")
    public List<Animal> getAnimalsByName(@PathVariable("name") String name){
        return animalService.getAnimalByName(name);
    }

    @PostMapping("/addImage")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        Image response = imageService.uploadImage(file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
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
}
