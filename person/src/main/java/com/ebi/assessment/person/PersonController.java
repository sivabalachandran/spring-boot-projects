package com.ebi.assessment.person;

import com.ebi.assessment.person.persistance.PersonEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
@Api("Operations for Creating, Retrieving, Deleting, Updating Person Entities")
public class PersonController {

    private final PersonService personService;

    @ApiOperation(value = "Retrieve a person by Id",
            response = PersonDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "Enity not found"),
    })
    @GetMapping("/persons/{id}")
    public ResponseEntity<PersonDto> findBy(@PathVariable(value = "id", required = true) Integer id) {

        final PersonEntity personEntity = personService.findBy(id);
        if (personEntity != null) {
            var personDto = this.convertToDto(personEntity);
            return new ResponseEntity<>(personDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Retrieve all available entities",
            response = PersonDto.class,
            responseContainer = "Set")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
    })
    @GetMapping("/persons")
    public ResponseEntity<Set<PersonDto>> findAll() {
        final var personEntities = personService.findAll();
        return new ResponseEntity<>(convertEntities(personEntities), HttpStatus.OK);
    }

    @ApiOperation(value = "Insert list of person entities",
            notes = "Takes list of person objects and insert it and returns the successful persisted entity values",
            response = PersonDto.class,
            responseContainer = "Set")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Invalid server error"),
    })
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

    @ApiOperation(value = "Delete list of person entities",
            notes = "Takes list of person Ids to be deleted and deletes it and returns the successful deleted entity ids",
            response = Integer.class,
            responseContainer = "Set")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Invalid server error"),
    })
    @DeleteMapping("/persons")
    public ResponseEntity<Set<Integer>> delete(@RequestBody Set<Integer> idsToDelete) {
        Assert.notEmpty(idsToDelete, "entity id cannot be empty with delete API");
        var succeededEntries = idsToDelete
                .stream()
                .map(personService::deleteById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (succeededEntries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(succeededEntries, HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Update a person by Id",
            notes = "Takes a map of field name and value to be updated. Api does not discriminate between null and new value. Returns Id if successful",
            response = Integer.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Invalid server error"),
    })
    @PatchMapping("/persons/{id}")
    public ResponseEntity<Integer> patch(@PathVariable final Integer id, @RequestBody Map<String, Object> changes) {
        Assert.notEmpty(changes, "changes collection cannot be empty with update API");
        var succeededEntry = personService.updateById(id, changes);
        if (succeededEntry != null) {
            return new ResponseEntity<>(succeededEntry, HttpStatus.OK);
        } else {
            // keeping it simple here to throw 500 when the patch fails. This can be amended based on business rules.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Updated list of person entities",
            notes = "Takes a map of Id and a map of field name and value to be updated. Api does not discriminate between null and new value. Returns Id if successful",
            response = Integer.class,
            responseContainer = "Set")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Invalid server error"),
    })
    @PatchMapping("/persons/")
    public ResponseEntity<Set<Integer>> patch(@RequestBody Map<Integer, Map<String, Object>> changes) {
        Assert.notEmpty(changes, "changes collection cannot be empty with update API");
        var succeededEntries = changes.entrySet()
                .stream()
                .map(entry -> personService.updateById(entry.getKey(), entry.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (succeededEntries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<>(succeededEntries, HttpStatus.OK);
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
