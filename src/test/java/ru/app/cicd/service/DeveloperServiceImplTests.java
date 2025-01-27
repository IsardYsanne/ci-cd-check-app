package ru.app.cicd.service;

import ru.app.cicd.entity.Developer;
import ru.app.cicd.exception.DeveloperNotFoundException;
import ru.app.cicd.exception.DeveloperWithDuplicateEmailException;
import ru.app.cicd.repository.DeveloperRepository;
import ru.app.cicd.storage.DeveloperStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Тестирование (unit) сервисного слоя при помощи Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class DeveloperServiceImplTests {

    @Mock
    private DeveloperRepository developerRepository;
    @InjectMocks
    private DeveloperServiceImpl developerService;

    @Test
    @DisplayName("Test save developer functionality")
    public void givenDeveloperToSave_whenSaveDeveloper_thenRepositoryIsCalled() {
        //given
        Developer developerToSave = DeveloperStorage.getDeveloperTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString())).willReturn(null);
        BDDMockito.given(developerRepository.save(any(Developer.class)))
                .willReturn(DeveloperStorage.getDeveloperPaulPersisted());
        //when
        Developer savedDeveloper = developerService.saveDeveloper(developerToSave);
        //then
        assertThat(savedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test save developer with duplicate email functionality")
    public void givenDeveloperToSaveWithDuplicateEmail_whenSaveDeveloper_thenExceptionIsThrown() {
        //given
        Developer developerToSave = DeveloperStorage.getDeveloperTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString())).willReturn(DeveloperStorage.getDeveloperTransient());
        //when
        assertThrows(DeveloperWithDuplicateEmailException.class, () -> developerService.saveDeveloper(developerToSave));
        //then
        verify(developerRepository, never()).save((any(Developer.class)));
    }

    @Test
    @DisplayName("Test update developer functionality")
    public void givenDeveloperToUpdate_whenUpdateDeveloper_thenRepositoryIsCalled() {
        //given
        Developer developerToUpdate = DeveloperStorage.getDeveloperMiaPersisted();
        BDDMockito.given(developerRepository.existsById(anyInt())).willReturn(true);
        BDDMockito.given(developerRepository.save(any(Developer.class))).willReturn(developerToUpdate);
        //when
        Developer updatedDeveloper = developerService.updateDeveloper(developerToUpdate);
        //then
        assertThat(updatedDeveloper).isNotNull();
        verify(developerRepository, times(1)).save((any(Developer.class)));
    }

    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    public void givenDeveloperToUpdateWithIncorrectId_whenUpdateDeveloper_thenExceptionIsThrown() {
        //given
        Developer developerToUpdate = DeveloperStorage.getDeveloperMiaPersisted();
        BDDMockito.given(developerRepository.existsById(anyInt())).willReturn(false);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> developerService.updateDeveloper(developerToUpdate));
        //then
        verify(developerRepository, never()).save((any(Developer.class)));
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenId_whenGetById_thenDeveloperIsReturned() {
        // given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.of(DeveloperStorage.getDeveloperMiaPersisted()));
        // when
        Developer developer = developerService.getDeveloperById(1);
        // then
        assertThat(developer).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenIncorrectId_whenGetById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> developerService.getDeveloperById(1));
        //then
    }

    @Test
    @DisplayName("Test get developer by email functionality")
    public void givenEmail_whenGetDeveloperByEmail_thenDeveloperIsReturned() {
        //given
        String updatedEmail = "paul@mail.ru";
        BDDMockito.given(developerRepository.findByEmail(anyString())).willReturn(DeveloperStorage.getDeveloperPaulPersisted());
        //when
        Developer developer = developerService.getDeveloperByEmail(updatedEmail);
        //then
        assertThat(developer).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by email functionality")
    public void givenIncorrectEmail_whenGetDeveloperByEmail_thenExceptionIsThrown() {
        //given
        String updatedEmail = "paul@mail.ru";
        BDDMockito.given(developerRepository.findByEmail(anyString())).willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> developerService.getDeveloperByEmail(updatedEmail));
        //then
    }

    @Test
    @DisplayName("Test get all developers functionality")
    public void givenThreeDeveloper_whenGetAll_thenOnlyActiveAreReturned() {
        //given
        Developer developer1 = DeveloperStorage.getDeveloperTransient();
        Developer developer2 = DeveloperStorage.getDeveloperHaloTransient();
        Developer developer3 = DeveloperStorage.getDeveloperNinaTransient();

        List<Developer> developerEntities = List.of(developer1, developer2, developer3);
        BDDMockito.given(developerRepository.findAll()).willReturn(developerEntities);
        //whe
        List<Developer> obtainedDevelopers = developerService.getAllDevelopers();
        //then
        assertThat(obtainedDevelopers.isEmpty()).isFalse();
        assertThat(obtainedDevelopers.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Test get all active by specialty functionality")
    public void givenThreeDevelopersAndTwoActive_whenGetAllActiveBySpecialty_thenDevelopersAreReturned() {
        //given
        Developer developer2 = DeveloperStorage.getDeveloperPaulPersisted();
        Developer developer3 = DeveloperStorage.getDeveloperMiaPersisted();

        List<Developer> developerEntities = List.of(developer2, developer3);
        BDDMockito.given(developerRepository.findAllActiveBySpecialty(anyString())).willReturn(developerEntities);
        //when
        List<Developer> obtainedDevelopers = developerService.getAllActiveBySpeciality("java34343");
        //then
        assertThat(obtainedDevelopers.isEmpty()).isFalse();
        assertThat(obtainedDevelopers.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test soft delete by id functionality")
    public void givenId_whenSoftDeleteById_thenRepositorySaveMethodIsCalled() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.of(DeveloperStorage.getDeveloperMiaPersisted()));
        //when
        developerService.softDeleteById(1);
        //then
        verify(developerRepository, times(1)).save((any(Developer.class)));
        verify(developerRepository, never()).deleteById((anyInt()));
    }

    @Test
    @DisplayName("Test soft delete by id functionality")
    public void givenIncorrectId_whenSoftDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class, () -> developerService.softDeleteById(1));
        //then
        verify(developerRepository, never()).save((any(Developer.class)));
    }

    @Test
    @DisplayName("Test hard delete by id functionality")
    public void givenCorrectId_whenHardDeleteById_thenDeleteRepoMethodIsCalled() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.of(DeveloperStorage.getDeveloperMiaPersisted()));
        //when
        developerService.hardDeleteById(1);
        //then
        verify(developerRepository, times(1)).deleteById((anyInt()));
    }

    @Test
    @DisplayName("Test hard delete by id functionality")
    public void givenIncorrectId_whenHardDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt())).willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class, () -> developerService.hardDeleteById(1));
        //then
        verify(developerRepository, never()).deleteById((anyInt()));
    }
}