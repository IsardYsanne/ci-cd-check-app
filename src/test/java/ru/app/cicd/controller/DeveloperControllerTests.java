package ru.app.cicd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.app.cicd.dto.DeveloperDto;
import ru.app.cicd.entity.Developer;
import ru.app.cicd.exception.DeveloperNotFoundException;
import ru.app.cicd.exception.DeveloperWithDuplicateEmailException;
import ru.app.cicd.service.DeveloperService;
import ru.app.cicd.storage.DeveloperStorage;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.hamcrest.CoreMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
public class DeveloperControllerTests {

    @MockBean
    private DeveloperService developerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test create developer functionality")
    public void givenDeveloperDto_whenCreateDeveloper_thenSuccessResponse() throws Exception {
        //given
        DeveloperDto dto = DeveloperStorage.getJohnDoeDtoTransient();
        Developer entity = DeveloperStorage.getJohnDoePersisted();
        BDDMockito.given(developerService.saveDeveloper(any(Developer.class))).willReturn(entity);
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
        DeveloperDto dto = DeveloperStorage.getJohnDoeDtoTransient();
        BDDMockito.given(developerService.saveDeveloper(any(Developer.class)))
                .willThrow(new DeveloperWithDuplicateEmailException("Developer with defined email is already exists"));
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
                        CoreMatchers.is("Developer with defined email is already exists")));
    }

    @Test
    @DisplayName("Test update developer functionality")
    public void givenDeveloperDto_whenUpdateDeveloper_thenSuccessResponse() throws Exception {
        //given
        DeveloperDto dto = DeveloperStorage.getJohnDoeDtoPersisted();
        Developer entity = DeveloperStorage.getJohnDoePersisted();
        BDDMockito.given(developerService.updateDeveloper(any(Developer.class))).willReturn(entity);
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
        DeveloperDto dto = DeveloperStorage.getJohnDoeDtoTransient();
        BDDMockito.given(developerService.updateDeveloper(any(Developer.class)))
                .willThrow(new DeveloperNotFoundException("Developer not found"));
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
        BDDMockito.given(developerService.getDeveloperById(anyInt())).willReturn(DeveloperStorage.getJohnDoePersisted());
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/developers/1").contentType(MediaType.APPLICATION_JSON));
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
        BDDMockito.given(developerService.getDeveloperById(anyInt()))
                .willThrow(new DeveloperNotFoundException("Developer not found"));
        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found"))); // тут
    }

    @Test
    @DisplayName("Test soft delete by id functionality")
    public void givenId_whenSoftDelete_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.doNothing().when(developerService).softDeleteById(anyInt());
        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        verify(developerService, times(1)).softDeleteById(anyInt());
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test soft delete by incorrect id functionality")
    public void givenIncorrectId_whenSoftDelete_thenErrorResponse() throws Exception {
        //given
        BDDMockito.doThrow(new DeveloperNotFoundException("Developer not found"))
                .when(developerService).softDeleteById(anyInt());
        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        verify(developerService, times(1)).softDeleteById(anyInt());
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
        BDDMockito.doNothing().when(developerService).hardDeleteById(anyInt());
        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/1?isHard=true")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        verify(developerService, times(1)).hardDeleteById(anyInt());
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test hard delete by incorrect id functionality")
    public void givenIncorrectId_whenHardDelete_thenErrorResponse() throws Exception {
        //given
        BDDMockito.doThrow(new DeveloperNotFoundException("Developer not found"))
                .when(developerService).hardDeleteById(anyInt());
        //when
        ResultActions resultActions = mockMvc.perform(delete("/api/v1/developers/1?isHard=true")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        verify(developerService, times(1)).hardDeleteById(anyInt());
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Developer not found")));
    }
}
