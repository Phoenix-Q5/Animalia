package com.animal.java.controller;

import com.animal.java.common.ApiErrorResponse;
import com.animal.java.exception.EntityNotFoundException;
import com.animal.java.model.animal.Image;
import com.animal.java.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/images")
@AllArgsConstructor
@Slf4j
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/{id}")
    public ResponseEntity<Image> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(imageService.getById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Image>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(imageService.getByType(type));
    }

    @GetMapping
    public ResponseEntity<List<Image>> getAllImages(){
        return ResponseEntity.ok(imageService.getAllImages());
    }

    @PostMapping
    public ResponseEntity<Image> addImage(@RequestBody Image image){
        return ResponseEntity.ok(imageService.addImage(image));
    }

    @PostMapping("/batch-insert")
    public ResponseEntity<List<Image>> addImages(@RequestBody List<Image> images){
        return ResponseEntity.ok(imageService.addImages(images));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Image> updateImage(@PathVariable UUID id, @RequestBody Image image){
        return ResponseEntity.ok(imageService.updateImage(id, image));
    }

    @PutMapping("/batch-update")
    public ResponseEntity<List<Image>> updateImages(@RequestBody List<Image> images){
        return ResponseEntity.ok(imageService.updateImages(images));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID id){
        imageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch-delete")
    public ResponseEntity<Void> deleteImages(@RequestBody List<UUID> ids){
        imageService.deleteImages(ids);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiErrorResponse processEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found", ex);
        return new ApiErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase());
    }
}
