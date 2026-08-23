package com.animal.java.controller;

import com.animal.java.dto.AnimalFacets;
import com.animal.java.dto.AnimalSearchRequest;
import com.animal.java.dto.PageResponse;
import com.animal.java.model.animal.Animal;
import com.animal.java.model.animal.Image;
import com.animal.java.model.enumConstants.*;
import com.animal.java.service.AnimalBulkImportService;
import com.animal.java.service.AnimalIndexService;
import com.animal.java.service.AnimalSearchService;
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
    private final AnimalSearchService animalSearchService;
    private final AnimalIndexService animalIndexService;

    @GetMapping
    public PageResponse<Animal> searchAnimals(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String letter,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String habitat,
            @RequestParam(required = false) String biome,
            @RequestParam(required = false) String climate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        return animalSearchService.search(AnimalSearchRequest.builder()
                .q(q)
                .letter(letter)
                .species(species)
                .habitat(habitat)
                .biome(biome)
                .climate(climate)
                .status(status)
                .page(page)
                .size(size)
                .sort(sort)
                .build());
    }

    @GetMapping("/facets")
    public AnimalFacets getFacets() {
        return animalSearchService.facets();
    }

    /** @deprecated Use {@code GET /api/v1/animals?page=&size=} */
    @Deprecated
    @GetMapping("/getAllAnimals")
    public PageResponse<Animal> getAllAnimals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        return animalSearchService.search(AnimalSearchRequest.builder()
                .page(page)
                .size(size)
                .sort(sort)
                .build());
    }

    /** @deprecated Use {@code GET /api/v1/animals} with filters */
    @Deprecated
    @GetMapping("/getBrowseAnimals")
    public PageResponse<Animal> getBrowseAnimals(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String letter,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String habitat,
            @RequestParam(required = false) String biome,
            @RequestParam(required = false) String climate,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        return searchAnimals(q, letter, species, habitat, biome, climate, status, page, size, sort);
    }

    @PostMapping("/admin/reindex")
    public ResponseEntity<?> reindex() {
        long indexed = animalIndexService.reindexAll();
        return ResponseEntity.ok(Map.of("indexed", indexed));
    }
//
    @PostMapping("/addAnimal")
    public Animal addAnimal(@RequestBody Animal animal){
        return animalService.insertAnimal(animal);
    }

    @PostMapping(value = {"/addCompleteAnimal", "/admin/addCompleteAnimal"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addAnimalComplete(
            @RequestPart("animal") Animal animal,
            @RequestParam(value = "animalImages", required = false) MultipartFile[] animalImages,
            @RequestParam(value = "distributionMap", required = false) MultipartFile distributionMap,
            @RequestParam(value = "appearanceImages", required = false) MultipartFile[] appearanceImages,
            @RequestParam(value = "image", required = false) MultipartFile legacyImage
    ) {
        MultipartFile[] files = animalImages;
        if ((files == null || files.length == 0) && legacyImage != null && !legacyImage.isEmpty()) {
            files = new MultipartFile[]{legacyImage};
        }
        Animal animalToSave = animalService.insertAnimal(animal, files, distributionMap, appearanceImages);
        return ResponseEntity.ok(animalToSave);
    }

    @PutMapping(value = "/admin/updateCompleteAnimal/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAnimalComplete(
            @PathVariable("id") UUID id,
            @RequestPart("animal") Animal animal,
            @RequestParam(value = "animalImages", required = false) MultipartFile[] animalImages,
            @RequestParam(value = "distributionMap", required = false) MultipartFile distributionMap,
            @RequestParam(value = "appearanceImages", required = false) MultipartFile[] appearanceImages,
            @RequestParam(value = "image", required = false) MultipartFile legacyImage
    ) {
        MultipartFile[] files = animalImages;
        if ((files == null || files.length == 0) && legacyImage != null && !legacyImage.isEmpty()) {
            files = new MultipartFile[]{legacyImage};
        }
        Animal updated = animalService.updateAnimal(id, animal, files, distributionMap, appearanceImages);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
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

    @GetMapping("/getById/{id}/related")
    public List<Animal> getRelatedAnimals(@PathVariable("id") UUID id){
        return animalService.getRelatedAnimals(id);
    }

    @GetMapping("/getByName/{name}")
    public List<Animal> getAnimalsByName(@PathVariable("name") String name){
        return animalService.getAnimalByName(name);
    }

    @GetMapping("/getConservationStatuses")
    public Map<String, Object> getConservationStatuses(){
        Map<String, Object> statuses = new LinkedHashMap<>();
        Arrays.stream(VulnerabilityStatus.values()).forEach(status -> {
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("code", status.getId());
            details.put("name", status.getName());
            details.put("iconKey", status.getIconKey());
            details.put("threatened", status.isThreatened());
            details.put("extinct", status.isExtinct());
            details.put("lowerRisk", status.isLowerRisk());
            statuses.put(status.getId(), details);
        });
        return statuses;
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
