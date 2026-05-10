package org.example.train.service;


import lombok.RequiredArgsConstructor;
import org.example.train.dto.request.LoginRequest;
import org.example.train.dto.request.RegisterRequest;
import org.example.train.dto.response.LoginResponse;
import org.example.train.dto.response.PersonResponse;
import org.example.train.model.Person;
import org.example.train.model.enums.Role;
import org.example.train.repository.PersonRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Person person = personRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), person.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(person);
        return new LoginResponse(person.getId(), person.getName(),
                person.getEmail(), person.getRole(), token);
    }

    public PersonResponse register(RegisterRequest request) {
        if (personRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Person person = new Person();
        person.setName(request.name());
        person.setEmail(request.email());
        person.setPassword(passwordEncoder.encode(request.password()));
        person.setRole(Role.CUSTOMER);
        personRepository.save(person);
        return new PersonResponse(person.getId(), person.getName(),
                person.getEmail(), person.getRole());
    }
}
