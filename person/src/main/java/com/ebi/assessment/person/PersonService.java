package com.ebi.assessment.person;

import com.ebi.assessment.person.persistance.PersonEntity;
import com.ebi.assessment.person.persistance.PersonRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "person")
public class PersonService {

    private final PersonRepository personRepository;
    private final CacheManager cacheManager;

    @Cacheable
    public PersonEntity findBy(final Integer id) {
        return personRepository.findById(id).orElse(null);
    }

    public Set<PersonEntity> findAll() {
        return new HashSet<>(personRepository.findAll());
    }

    public Set<PersonEntity> create(final Set<PersonEntity> persons) {
        Assert.notEmpty(persons, "persons collection cannot be empty with create API");

        final Set<PersonEntity> succeededEntries = new HashSet<>();
        for (final PersonEntity person : persons) {
            try {
                final PersonEntity savedEntity = personRepository.save(person);
                succeededEntries.add(savedEntity);
                personCache().put(savedEntity.getId(), savedEntity);
            } catch (final DataAccessException dae) {
                log.error("Error Saving entity - {} with error - {}", person, dae);
            }
        }

        return succeededEntries;
    }

    public Integer deleteById(final Integer id) {
        try {
            personRepository.deleteById(id);
            personCache().evictIfPresent(id);
            return id;
        } catch (final DataAccessException dae) {
            log.error("Error removing entity with Id- {}", id);
        }
        return null;
    }

    public Integer updateById(final Integer id, final Map<String, Object> changes) {
        try {
            final Optional<PersonEntity> entityFromDB = personRepository.findById(id);
            if (entityFromDB.isPresent()) {
                final var entity = entityFromDB.get();
                final var updatedEntity = updateEntity(entity, changes);
                final PersonEntity savedEntity = personRepository.save(updatedEntity);
                personCache().put(savedEntity.getId(), savedEntity);
                return id;
            }
        } catch (final DataAccessException dae) {
            log.error("Error updating entity - {}", id);
        }
        return null;
    }

    private PersonEntity updateEntity(final PersonEntity entityFromDB, final Map<String, Object> changes) {
        final var builder = entityFromDB.toBuilder();
        changes.forEach(
                (change, value) -> {
                    switch (change) {
                        case "firstName":
                            builder.withFirstName(String.valueOf(value));
                            break;
                        case "lastName":
                            builder.withLastName(String.valueOf(value));
                            break;
                        case "age":
                            builder.withAge((int) value);
                            break;
                        case "favoriteColor":
                            builder.withFavoriteColor(String.valueOf(value));
                            break;
                    }
                }
        );
        return builder.build();
    }

    private Cache personCache() {
        return cacheManager.getCache("person");
    }
}
