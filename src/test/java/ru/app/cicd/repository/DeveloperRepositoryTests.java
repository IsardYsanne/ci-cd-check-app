package ru.app.cicd.repository;

import ru.app.cicd.entity.Developer;
import ru.app.cicd.storage.DeveloperStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тестируем методы репозитория.
 */
@DataJpaTest
public class DeveloperRepositoryTests {

    @Autowired
    private DeveloperRepository developerRepository;

    @BeforeEach
    public void setUp() {
        developerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save developer functionality")
    public void givenDeveloperObject_whenSave_thenDeveloperIsCreated() {
        //given
        Developer developer = DeveloperStorage.getDeveloperTransient();
        //when
        Developer savedDeveloper = developerRepository.save(developer);
        //then
        assertThat(savedDeveloper).isNotNull();
        assertThat(savedDeveloper.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test update developer functionality")
    public void givenDeveloperToUpdate_whenUpdate_thenEmailIsChanged() {
        //given
        String updatedEmail = "updated@mail.com";
        Developer developer = DeveloperStorage.getDeveloperTransient();
        developerRepository.save(developer);
        //when
        Developer developerToUpdate = developerRepository.findById(developer.getId()).orElse(null);
        developerToUpdate.setEmail(updatedEmail);
        Developer updatedDeveloper = developerRepository.save(developerToUpdate);
        //then
        assertThat(updatedDeveloper).isNotNull();
        assertThat(updatedDeveloper.getEmail()).isEqualTo(updatedEmail);
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenDeveloperCreated_whenGetById_thenDeveloperIsReturned() {
        //given
        Developer developer = DeveloperStorage.getDeveloperTransient();
        developerRepository.save(developer);
        //when
        Developer obtainedDev = developerRepository.findById(developer.getId()).orElse(null);
        //then
        assertThat(obtainedDev).isNotNull();
        assertThat(obtainedDev.getEmail()).isEqualTo("haha@mail.ru");
    }

    @Test
    @DisplayName("Test developer not found functionality")
    public void givenDeveloperNotCreated_whenGetById_thenOptionalIsEmpty() {
        //given
        //when
        Developer obtainedDev = developerRepository.findById(1)
                .orElse(null);
        //then
        assertThat(obtainedDev).isNull();
    }

    @Test
    @DisplayName("Test get all developers functionality")
    public void givenDeveloperAreStored_whenFindAll_thenAllDeveloperAreReturned() {
        //given
        Developer developer1 = DeveloperStorage.getDeveloperTransient();
        Developer developer2 = DeveloperStorage.getDeveloperHaloTransient();

        developerRepository.saveAll(List.of(developer1, developer2));
        //when
        List<Developer> developerEntities = developerRepository.findAll();
        //then
        assertThat(developerEntities.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("Test get developer By Email functionality")
    public void givenDeveloperSaved_whenGetByEmail_thenDeveloperIsReturned() {
        //given
        Developer developer = DeveloperStorage.getDeveloperTransient();
        developerRepository.save(developer);
        //when
        Developer obtainedDev = developerRepository.findByEmail(developer.getEmail());
        //then
        assertThat(obtainedDev.getEmail()).isEqualTo(developer.getEmail());
    }

    @Test
    @DisplayName("Test get developer with status ACTIVE functionality")
    public void givenThreeDevelopersActive_whenFindAllActiveBySpeciality_thenReturnedDevelopers() {
        //given
        Developer developer1 = DeveloperStorage.getDeveloperTransient();
        Developer developer2 = DeveloperStorage.getDeveloperHaloTransient();
        Developer developer3 = DeveloperStorage.getDeveloperNinaTransient();
        developerRepository.saveAll(List.of(developer1, developer2, developer3));
        //when
        List<Developer> developerEntities = developerRepository.findAllActiveBySpecialty("java");
        //then
        assertThat(developerEntities.isEmpty()).isFalse();
        assertThat(developerEntities.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Test delete developer by id functionality")
    public void givenDeveloperSaved_whenDeleteById_thenDevelopersAreRemovedFromDB() {
        //given
        Developer developer = DeveloperStorage.getDeveloperTransient();
        developerRepository.save(developer);
        //when
        developerRepository.deleteById(developer.getId());
        //then
        Developer obtainedDev = developerRepository.findById(developer.getId()).orElse(null);
        assertThat(obtainedDev).isNull();
    }
}
