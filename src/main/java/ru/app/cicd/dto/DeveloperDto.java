package ru.app.cicd.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.app.cicd.entity.Developer;
import ru.app.cicd.entity.Status;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeveloperDto {

    private Integer id;

    private String firstName;

    private String lastName;
    private String specialty;

    private String email;

    private Status status;

    public Developer toDeveloperEntity() {
        return Developer.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .specialty(specialty)
                .email(email)
                .status(status)
                .build();
    }

    public static DeveloperDto toDeveloperDto(Developer developer) {
        return DeveloperDto.builder()
                .id(developer.getId())
                .firstName(developer.getFirstName())
                .lastName(developer.getLastName())
                .email(developer.getEmail())
                .specialty(developer.getSpecialty())
                .status(developer.getStatus())
                .build();
    }
}