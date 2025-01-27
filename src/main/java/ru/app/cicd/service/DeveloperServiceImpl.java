package ru.app.cicd.service;

import lombok.RequiredArgsConstructor;
import ru.app.cicd.entity.Developer;
import ru.app.cicd.entity.Status;
import ru.app.cicd.exception.DeveloperNotFoundException;
import ru.app.cicd.exception.DeveloperWithDuplicateEmailException;
import ru.app.cicd.repository.DeveloperRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeveloperServiceImpl implements DeveloperService {

    private final DeveloperRepository developerRepository;

    @Override
    public Developer saveDeveloper(Developer developer) {
        Developer duplicateCandidate = developerRepository.findByEmail(developer.getEmail());

        if (Objects.nonNull(duplicateCandidate)) {
            throw new DeveloperWithDuplicateEmailException("Developer with defined email is already exists");
        }

        developer.setStatus(Status.ACTIVE);
        return developerRepository.save(developer);
    }

    @Override
    public Developer updateDeveloper(Developer developer) {
        boolean isExists = developerRepository.existsById(developer.getId());

        if (!isExists) {
            throw new DeveloperNotFoundException("Developer not found");
        }
        return developerRepository.save(developer);
    }

    @Override
    public Developer getDeveloperById(Integer id) {
        return developerRepository.findById(id).orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));
    }

    @Override
    public Developer getDeveloperByEmail(String email) {
        Developer obtainedDeveloper = developerRepository.findByEmail(email);

        if (Objects.isNull(obtainedDeveloper)) {
            throw new DeveloperNotFoundException("Developer not found");
        }
        return obtainedDeveloper;
    }

    @Override
    public List<Developer> getAllDevelopers() {
        return developerRepository.findAll().stream().filter(d -> {
            return d.getStatus().equals(Status.ACTIVE);
        }).collect(Collectors.toList());
    }

    @Override
    public List<Developer> getAllActiveBySpeciality(String specialty) {
        return developerRepository.findAllActiveBySpecialty(specialty);
    }

    @Override
    public void softDeleteById(Integer id) {
        Developer obtainedDeveloper = developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));

        obtainedDeveloper.setStatus(Status.DELETED);
        developerRepository.save(obtainedDeveloper);
    }

    @Override
    public void hardDeleteById(Integer id) {
        Developer obtainedDeveloper = developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));
        developerRepository.deleteById(obtainedDeveloper.getId());
    }
}