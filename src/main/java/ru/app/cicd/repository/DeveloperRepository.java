package ru.app.cicd.repository;

import ru.app.cicd.entity.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeveloperRepository extends JpaRepository<Developer, Integer> {

    Developer findByEmail(String email);

    @Query("SELECT d FROM Developer d WHERE d.status = 'ACTIVE' AND d.specialty = ?1")
    List<Developer> findAllActiveBySpecialty(String specialty);
}
