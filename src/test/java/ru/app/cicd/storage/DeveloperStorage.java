package ru.app.cicd.storage;

import ru.app.cicd.dto.DeveloperDto;
import ru.app.cicd.entity.Developer;
import ru.app.cicd.entity.Status;

public class DeveloperStorage {

    public static Developer getDeveloperTransient() {
        return Developer.builder()
                .firstName("Jully")
                .lastName("Nino")
                .email("haha@mail.ru")
                .specialty("java")
                .status(Status.ACTIVE)
                .build();
    }

    public static Developer getDeveloperHaloTransient() {
        return Developer.builder()
                .firstName("Halo")
                .lastName("Nono")
                .email("halo@mail.ru")
                .specialty("java")
                .status(Status.ACTIVE)
                .build();
    }

    public static Developer getDeveloperNinaTransient() {
        return Developer.builder()
                .firstName("Nina")
                .lastName("Rysef")
                .email("nina@mail.ru")
                .specialty("java")
                .status(Status.ACTIVE)
                .build();
    }

    public static Developer getDeveloperPaulPersisted() {
        return Developer.builder()
                .id(2)
                .firstName("Paul")
                .lastName("Rysef")
                .email("paul@mail.ru")
                .specialty("java")
                .status(Status.ACTIVE)
                .build();
    }

    public static Developer getDeveloperMiaPersisted() {
        return Developer.builder()
                .id(3)
                .firstName("Mia")
                .lastName("Milova")
                .email("mia@mail.ru")
                .specialty("php")
                .status(Status.DELETED)
                .build();
    }

    public static Developer getJohnDoeTransient() {
        return Developer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@mail.com")
                .specialty("Java")
                .status(Status.ACTIVE)
                .build();
    }

    public static Developer getJohnDoePersisted() {
        return Developer.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@mail.com")
                .specialty("Java")
                .status(Status.ACTIVE)
                .build();
    }

    public static DeveloperDto getJohnDoeDtoTransient() {
        return DeveloperDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@mail.com")
                .specialty("Java")
                .status(Status.ACTIVE)
                .build();
    }

    public static DeveloperDto getJohnDoeDtoPersisted() {
        return DeveloperDto.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@mail.com")
                .specialty("Java")
                .status(Status.ACTIVE)
                .build();
    }
}
