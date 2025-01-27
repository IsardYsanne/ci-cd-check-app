package ru.app.cicd.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.app.cicd.dto.DeveloperDto;
import ru.app.cicd.entity.Developer;
import ru.app.cicd.entity.Status;
import ru.app.cicd.repository.DeveloperRepository;
import ru.app.cicd.storage.DeveloperStorage;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

/**
 * Интеграционный тест с использованием TestContainers.
 */
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItDeveloperRestControllerWithTestContainersTests extends AbstractRestControllerBaseTest {

    @Autowired
    private DeveloperRepository developerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        developerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create developer functionality")
    public void givenDeveloperDto_whenCreateDeveloper_thenSuccessResponse() throws Exception {
        //given
        DeveloperDto dto = DeveloperStorage.getJohnDoeDtoTransient();
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test create developer with duplicate email functionality")
    public void givenDeveloperDtoWithDuplicateEmail_whenCreateDeveloper_thenErrorResponse() throws Exception {
        //given
        String duplicateEmail = "duplicate@mail.com";
        Developer developer = DeveloperStorage.getJohnDoeTransient();
        developer.setEmail(duplicateEmail);
        developerRepository.save(developer);

        DeveloperDto dto = DeveloperStorage.getJohnDoeDtoTransient();
        dto.setEmail(duplicateEmail);
        //when
        ResultActions resultActions = mockMvc.perform(post("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Developer with defined email is already exists"))); // тут
    }

    @Test
    @DisplayName("Test update developer functionality")
    public void givenDeveloperDto_whenUpdateDeveloper_thenSuccessResponse() throws Exception {
        //given
        Developer developer = DeveloperStorage.getJohnDoeTransient();
        developerRepository.save(developer);

        String updatedEmail = "updated@mail.com";
        DeveloperDto dto = DeveloperDto.toDeveloperDto(developer);
        dto.setEmail(updatedEmail);

        //when
        ResultActions resultActions = mockMvc.perform(put("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test update developer with incorrect id")
    public void givenDeveloperDtoWithIncorrectId_whenUpdateDeveloper_thenErrorResponse() throws Exception {
        //given
        int incorrectId = 333;
        DeveloperDto dto = DeveloperStorage.getJohnDoeDtoPersisted();
        dto.setId(incorrectId);

        //when
        ResultActions resultActions = mockMvc.perform(put("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                        CoreMatchers.is("Developer not found"))); // тут
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenId_whenGetById_thenSuccessResponse() throws Exception {
        //given
        Developer developer = DeveloperStorage.getJohnDoeTransient();
        developerRepository.save(developer);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/developers/" + developer.getId())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test get developer by incorrect id functionality")
    public void givenIncorrectId_whenGetById_thenErrorResponse() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/developers/" + 666)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }

    @Test
    @DisplayName("Test soft delete by id functionality")
    public void givenId_whenSoftDelete_thenSuccessResponse() throws Exception {
        //given
        Developer developer = DeveloperStorage.getJohnDoeTransient();
        developerRepository.save(developer);

        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/" + developer.getId())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        Developer obtainedDev = developerRepository.findById(developer.getId()).orElse(null);
        assertThat(obtainedDev).isNotNull();
        assertThat(obtainedDev.getStatus()).isEqualTo(Status.DELETED);
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test soft delete by incorrect id functionality")
    public void givenIncorrectId_whenSoftDelete_thenErrorResponse() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        Developer obtainedDev = developerRepository.findById(1).orElse(null);
        assertThat(obtainedDev).isNull();
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }

    @Test
    @DisplayName("Test hard delete by id functionality")
    public void givenId_whenHardDelete_thenSuccessResponse() throws Exception {
        //given
        Developer developer = DeveloperStorage.getJohnDoeTransient();
        developerRepository.save(developer);

        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/" + developer.getId() + "?isHard=true")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        Developer obtainedDev = developerRepository.findById(developer.getId()).orElse(null);
        assertThat(obtainedDev).isNull();
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test hard delete by incorrect id functionality")
    public void givenIncorrectId_whenHardDelete_thenErrorResponse() throws Exception {
        //given

        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/1?isHard=true")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        Developer obtainedDev = developerRepository.findById(1).orElse(null);
        assertThat(obtainedDev).isNull();
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }
}
