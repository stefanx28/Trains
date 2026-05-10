package org.example.train.service;

import lombok.RequiredArgsConstructor;
import org.example.train.model.Person;
import org.example.train.model.enums.Role;
import org.example.train.repository.PersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public Person register(String name, String email, String password, Role role) {
        if (personRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }
        Person person = new Person();
        person.setName(name);
        person.setEmail(email);
        person.setPassword(passwordEncoder.encode(password));
        person.setRole(role);
        return personRepository.save(person);
    }

    public Person findById(UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Person not found: " + id));
    }

    public Person findByEmail(String email) {
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Person not found: " + email));
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person update(UUID id, String name, String email) {
        Person person = findById(id);
        person.setName(name);
        person.setEmail(email);
        return personRepository.save(person);
    }

    public void delete(UUID id) {
        personRepository.deleteById(id);
    }
}