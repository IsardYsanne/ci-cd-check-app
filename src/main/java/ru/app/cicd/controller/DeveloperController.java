package ru.app.cicd.controller;

import lombok.RequiredArgsConstructor;
import ru.app.cicd.dto.DeveloperDto;
import ru.app.cicd.dto.ErrorDto;
import ru.app.cicd.entity.Developer;
import ru.app.cicd.exception.DeveloperNotFoundException;
import ru.app.cicd.exception.DeveloperWithDuplicateEmailException;
import ru.app.cicd.service.DeveloperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/developers")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeveloperById(@PathVariable("id") Integer id) {
        try {
            Developer entity = developerService.getDeveloperById(id);
            DeveloperDto result = DeveloperDto.toDeveloperDto(entity);
            return ResponseEntity.ok(result);
        } catch (DeveloperNotFoundException e) {
            return ResponseEntity.status(404).body(ErrorDto.builder().status(404).message(e.getMessage()).build());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllDevelopers() {
        List<Developer> entities = developerService.getAllDevelopers();
        List<DeveloperDto> dtos = entities.stream().map(DeveloperDto::toDeveloperDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<?> getAllDevelopersBySpecialty(@PathVariable("specialty") String specialty) {
        List<Developer> entities = developerService.getAllActiveBySpeciality(specialty);
        List<DeveloperDto> dtos = entities.stream().map(DeveloperDto::toDeveloperDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    public ResponseEntity<?> createDeveloper(@RequestBody DeveloperDto dto) {
        try {
            Developer entity = dto.toDeveloperEntity();
            Developer createdDeveloper = developerService.saveDeveloper(entity);
            DeveloperDto result = DeveloperDto.toDeveloperDto(createdDeveloper);
            return ResponseEntity.ok(result);
        } catch (DeveloperWithDuplicateEmailException e) {
            return ResponseEntity.badRequest().body(ErrorDto.builder().status(400).message(e.getMessage()).build());
        }
    }

    @PutMapping
    public ResponseEntity<?> updateDeveloper(@RequestBody DeveloperDto dto) {
        try {
            Developer entity = dto.toDeveloperEntity();
            Developer updatedEntity = developerService.updateDeveloper(entity);
            DeveloperDto result = DeveloperDto.toDeveloperDto(updatedEntity);
            return ResponseEntity.ok(result);
        } catch (DeveloperNotFoundException e) {
            return ResponseEntity.badRequest().body(ErrorDto.builder().status(400).message(e.getMessage()).build());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeveloperById(@PathVariable("id") Integer id,
                                                 @RequestParam(value = "isHard", defaultValue = "false") boolean isHard) {
        try {
            if (isHard) {
                developerService.hardDeleteById(id);
            } else {
                developerService.softDeleteById(id);
            }
            return ResponseEntity.ok().build();
        } catch (DeveloperNotFoundException e) {
            return ResponseEntity.badRequest().body(ErrorDto.builder().status(400).message(e.getMessage()).build());
        }
    }
}
