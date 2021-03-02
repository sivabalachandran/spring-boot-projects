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

    public Set<Integer> delete(final Set<Integer> idsToDelete) {
        Assert.notEmpty(idsToDelete, "entity id cannot be empty with delete API");
        final Set<Integer> succeededEntries = new HashSet<>();
        for (final Integer id : idsToDelete) {
            try {
                personRepository.deleteById(id);
                succeededEntries.add(id);
                personCache().evictIfPresent(id);
            } catch (final DataAccessException dae) {
                log.error("Error removing entity with Id- {}", id);
            }
        }

        return succeededEntries;
    }

    public Set<PersonEntity> update(final Set<PersonEntity> entities) {
        Assert.notEmpty(entities, "persons collection cannot be empty with update API");
        final Set<PersonEntity> succeededEntries = new HashSet<>();
        for (final PersonEntity person : entities) {
            try {
                final PersonEntity savedEntity = personRepository.save(person);
                succeededEntries.add(savedEntity);
                personCache().put(savedEntity.getId(), savedEntity);
            } catch (final DataAccessException dae) {
                log.error("Error updating entity - {}", person);
            }
        }
        
        return succeededEntries;
    }

    private Cache personCache() {
        return cacheManager.getCache("person");
    }
}
