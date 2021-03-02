package com.ebi.assessment.person;

import com.ebi.assessment.person.persistance.PersonEntity;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
@Api("Operations for Creating, Retrieving, Deleting, Updating Person Entities")
public class PersonController {

    private final PersonService personService;

    @GetMapping("/persons/{id}")
    public ResponseEntity<PersonDto> findBy(@PathVariable(value = "id", required = true) Integer id) {

        final PersonEntity personEntity = personService.findBy(id);
        if (personEntity != null) {
            var personDto = this.convertToDto(personEntity);
            return new ResponseEntity<>(personDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/persons")
    public ResponseEntity<Set<PersonDto>> findAll() {
        final var personEntities = personService.findAll();
        return new ResponseEntity<>(convertEntities(personEntities), HttpStatus.OK);
    }

    @PostMapping("/persons")
    public ResponseEntity<Set<PersonDto>> create(@RequestBody List<PersonDto> persons) {
        Assert.notEmpty(persons, "persons collection cannot be empty with create API");
        var personEntities = persons.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toUnmodifiableSet());

        var succeededEntries = personService.create(personEntities);
        if (personEntities.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(convertEntities(succeededEntries), HttpStatus.OK);
        }
    }

    @DeleteMapping("/persons")
    public ResponseEntity<Set<Integer>> delete(@RequestBody Set<Integer> idsToDelete) {
        Assert.notEmpty(idsToDelete, "entity id cannot be empty with delete API");
        var succeededEntries = personService.delete(idsToDelete);
        if (succeededEntries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(succeededEntries, HttpStatus.OK);
        }
    }

    @PutMapping("/persons")
    public ResponseEntity<Set<PersonDto>> update(@RequestBody List<PersonDto> persons) {
        Assert.notEmpty(persons, "persons collection cannot be empty with update API");
        var personEntities = persons.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toUnmodifiableSet());

        var succeededEntries = personService.update(personEntities);
        if (succeededEntries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(convertEntities(succeededEntries), HttpStatus.OK);
        }
    }

    private Set<PersonDto> convertEntities(final Set<PersonEntity> entities) {
        return Optional.ofNullable(entities)
                .orElse(Collections.emptySet())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toUnmodifiableSet());
    }

    private PersonEntity convertToEntity(final PersonDto personDto) {

        return PersonEntity.builder()
                .withId(personDto.getId())
                .withFirstName(personDto.getFirstName())
                .withLastName(personDto.getLastName())
                .withAge(personDto.getAge())
                .withFavoriteColor(personDto.getFavoriteColor())
                .build();
    }

    private PersonDto convertToDto(final PersonEntity personEntity) {

        return PersonDto.builder()
                .withId(personEntity.getId())
                .withFirstName(personEntity.getFirstName())
                .withLastName(personEntity.getLastName())
                .withAge(personEntity.getAge())
                .withFavoriteColor(personEntity.getFavoriteColor())
                .build();
    }
}
