package com.ebi.assessment.person

import com.ebi.assessment.person.persistance.PersonEntity
import com.ebi.assessment.person.persistance.PersonRepository
import com.google.common.collect.ImmutableList
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.dao.DataIntegrityViolationException
import spock.lang.Specification

class PersonServiceSpec extends Specification {
    PersonService personSvc
    PersonRepository personRepository
    CacheManager cacheManager
    Cache personCache

    def setup() {
        personRepository = Mock()
        cacheManager = Mock()
        personCache = Mock()
        personSvc = new PersonService(personRepository, cacheManager)
        cacheManager.getCache('person') >> personCache
    }

    def 'findById works as expected when entity is found and not found'() {
        given:
        def personToTest = personEntityWithDefaultAge(1, 'Siva', 'Balachandran')

        when: 'Entity not found'
        def result = personSvc.findBy(1)

        then:
        1 * personRepository.findById(1) >> Optional.of(personToTest)

        and:
        result.firstName == personToTest.firstName
        result.lastName == personToTest.lastName
        result.age == personToTest.age
        result.favoriteColor == personToTest.favoriteColor
        result.id == personToTest.id

        when: 'Entity not found'
        result = personSvc.findBy(2)

        then:
        1 * personRepository.findById(2) >> Optional.empty()

        and:
        result == null
    }

    def 'findAll returns all entities in repository'() {
        given:
        def person1 = personEntityWithDefaultAge(1, 'Siva', 'Balachandran')
        def person2 = personEntityWithDefaultAge(2, 'user', 'test')

        when:
        def result = personSvc.findAll()

        then:
        1 * personRepository.findAll() >> ImmutableList.of(person1, person2)

        and:
        result.size() == 2
    }

    def 'Create record works as expected'() {
        given:
        def request = personEntityWithDefaultAge(null, 'Siva', 'Balachandran')
        def response = personEntityWithDefaultAge(1, 'Siva', 'Balachandran')

        when:
        def result = personSvc.create([request] as Set<PersonEntity>)

        then:
        1 * personRepository.save(request) >> response
        1 * personCache.put(response.id, response)

        and:
        result.size() == 1
    }

    def 'Create record failed with Exception'() {
        given:
        def request = personEntityWithDefaultAge(null, 'Siva', 'Balachandran')

        when:
        def result = personSvc.create([request] as Set<PersonEntity>)

        then:
        1 * personRepository.save(request) >> { throw new DataIntegrityViolationException('test') }
        0 * personCache.put(_, _)

        and:
        result == [] as Set
    }

    def 'Update record works as expected'() {
        given:
        def request = personEntityWithDefaultAge(1, 'Siva', 'Balachandran')

        when:
        def result = personSvc.update([request] as Set<PersonEntity>)

        then:
        1 * personRepository.save(request) >> request
        1 * personCache.put(request.id, request)

        and:
        result.size() == 1
    }

    def 'Delete record works as expected'() {
        given:
        def request = [1]

        when:
        def result = personSvc.delete(request as Set)

        then:
        1 * personRepository.deleteById(1)
        1 * personCache.evictIfPresent(1)

        and:
        result.size() == 1
    }

    static personEntityWithDefaultAge(Integer id, String firstName, String lastName) {
        PersonEntity.builder()
                .withId(id)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withAge(30)
                .withFavoriteColor("Blue")
                .build()
    }

}
