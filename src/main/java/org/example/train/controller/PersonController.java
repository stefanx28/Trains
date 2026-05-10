package org.example.train.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.train.dto.request.RegisterRequest;
import org.example.train.dto.response.PersonResponse;
import org.example.train.mappers.PersonMapper;
import org.example.train.model.Person;
import org.example.train.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PersonController {

    private final PersonService personService;
    private final PersonMapper personMapper;

    // Admin only
    @GetMapping
    public ResponseEntity<List<PersonResponse>> findAll() {
        List<PersonResponse> persons = personService.findAll().stream()
                .map(personMapper::toResponse)
                .toList();
        return ResponseEntity.ok(persons);
    }

    // Get specific user profile
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(personMapper.toResponse(personService.findById(id)));
    }

    // Admin only
    @PostMapping
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody RegisterRequest request) {
        Person person = personService.register(
                request.name(),
                request.email(),
                request.password(),
                request.role()
        );
        return ResponseEntity.ok(personMapper.toResponse(person));
    }

    // Update user details
    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable UUID id,
                                                 @Valid @RequestBody RegisterRequest request) {
        Person person = personService.update(id, request.name(), request.email());
        return ResponseEntity.ok(personMapper.toResponse(person));
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

}