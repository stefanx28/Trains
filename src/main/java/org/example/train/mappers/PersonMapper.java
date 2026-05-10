package org.example.train.mappers;

import lombok.RequiredArgsConstructor;
import org.example.train.dto.response.PersonResponse;
import org.example.train.model.Person;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonMapper {
    public PersonResponse toResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getRole()
        );
    }
}
