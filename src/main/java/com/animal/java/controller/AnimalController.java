package com.animal.java.controller;

import com.animal.java.model.Animal;
import com.animal.java.model.Image;
import com.animal.java.service.AnimalService;
import com.animal.java.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/animals")
@AllArgsConstructor
@CrossOrigin("http://localhost:3000")
public class AnimalController {

    private AnimalService animalService;

    private ImageService imageService;

    @GetMapping("/getAllAnimals")
    public List<Animal> getAllAnimals(){
        return animalService.getAllAnimals();
    }

    @PostMapping("/addAnimal")
    public Animal addAnimal(@RequestBody Animal animal){
        return animalService.addAnimal(animal);
    }

    @PostMapping(value = "/addCompleteAnimal", consumes = "multipart/form-data")
    public ResponseEntity<?> addAnimalComplete(@RequestPart("animal") Animal animal, @RequestParam("image") MultipartFile file){
        Image animalImage = imageService.uploadImage(file);
        animal.setAnimalImage(animalImage);
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

    @PostMapping("/addImage")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        Image response = imageService.uploadImage(file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/searchAnimals")
    public List<Animal> getAnimals(@RequestParam(value = "search", required = false) String smartSearch){
        return animalService.smartAnimalSearch(smartSearch);
    }
}
