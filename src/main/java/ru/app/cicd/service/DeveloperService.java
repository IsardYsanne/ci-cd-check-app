package ru.app.cicd.service;

import ru.app.cicd.entity.Developer;

import java.util.List;

public interface DeveloperService {

    Developer saveDeveloper(Developer developer);

    Developer updateDeveloper(Developer developer);

    Developer getDeveloperById(Integer id);

    Developer getDeveloperByEmail(String email);

    List<Developer> getAllDevelopers();

    List<Developer> getAllActiveBySpeciality(String speciality);

    void softDeleteById(Integer id);

    void hardDeleteById(Integer id);
}
