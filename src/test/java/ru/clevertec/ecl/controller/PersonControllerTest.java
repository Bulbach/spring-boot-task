package ru.clevertec.ecl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.entity.Passport;
import ru.clevertec.ecl.service.impl.PersonServiceImpl;
import ru.clevertec.ecl.util.PersonTestBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private PersonServiceImpl personServiceImpl;

    @Test
    void getAllPersons_success() throws Exception {
        // when
        int size = 15;
        Mockito.when(personServiceImpl.getAllPersons(size))
                .thenReturn(PersonTestBuilder.builder().build().dtoPersons());

        // then
        mockMvc.perform(get("/persons?size=" + size)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getById_success() throws Exception {
        // when
        ResponseDtoPerson responseDtoPerson = PersonTestBuilder.builder().build().responseDtoPerson();
        UUID personId = UUID.fromString(responseDtoPerson.uuid());
        Mockito.when(personServiceImpl.getById(personId))
                .thenReturn(responseDtoPerson);

        // then
        mockMvc.perform(get("/persons/{id}", personId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(personId.toString()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createPerson_success() throws Exception {

        // when
        RequestDtoPerson createPerson = PersonTestBuilder.builder().build().requestCreateDtoPerson();
        System.out.println(createPerson);
        ResponseDtoPerson createdPerson = PersonTestBuilder.builder().build().responseDtoPerson();
        System.out.println(createdPerson);

        Mockito.when(personServiceImpl.create(createPerson)).thenReturn(createdPerson);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createPerson))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is(createdPerson.name())))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    void updatePerson_success() throws Exception {
        RequestDtoPerson requestDtoPerson = PersonTestBuilder.builder().build().requestDtoPerson();
        ResponseDtoPerson responseDtoPerson = PersonTestBuilder.builder().build().responseDtoPerson();

        RequestDtoPerson updateRequestDtoPerson = new RequestDtoPerson(requestDtoPerson.uuid()
                , "Update"
                , "Updatavich"
                , requestDtoPerson.sex()
                , requestDtoPerson.createDate()
                , requestDtoPerson.updateDate()
                , requestDtoPerson.passport()
                , requestDtoPerson.house()
                , false
        );
        ResponseDtoPerson updateResponseDtoPerson = new ResponseDtoPerson(responseDtoPerson.uuid()
                , "Update"
                , "Updatavich"
                , responseDtoPerson.sex()
                , responseDtoPerson.createDate()
                , responseDtoPerson.updateDate()
                , responseDtoPerson.passport()
        );

        Mockito.when(personServiceImpl.getById(UUID.fromString(requestDtoPerson.uuid()))).thenReturn(responseDtoPerson);
        Mockito.when(personServiceImpl.update(UUID.fromString(requestDtoPerson.uuid()), requestDtoPerson)).thenReturn(updateResponseDtoPerson);


        MockHttpServletRequestBuilder mockRequest = put("/persons/{id}", requestDtoPerson.uuid())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDtoPerson));

        // then
        mockMvc.perform(mockRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.uuid", is(responseDtoPerson.uuid())))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void deletePerson_success() throws Exception {

        // when
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String personId = "ada7d136-a1ba-4a3d-bc04-95872e9324f3";
        LocalDateTime create = LocalDateTime.parse("2024-02-01 23:43:51.524", formatter);
        LocalDateTime update = LocalDateTime.parse("2024-02-01 23:43:51.524", formatter);

        ResponseDtoPerson deleteResponseDtoPerson = new ResponseDtoPerson(
                personId
                , "Mark"
                , "Cucumber"
                , "Male"
                , create
                , update
                , new Passport("0000f", "490-f")

        );

        Mockito.when(personServiceImpl.getById(UUID.fromString(personId))).thenReturn(deleteResponseDtoPerson);

        // then
        mockMvc.perform(delete("/persons/{id}", personId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void addOwnerToHouse() throws Exception {
        // when
        UUID houseId = UUID.fromString("b7d69c82-9833-4364-9c77-a0ecfc467c63");
        UUID personId = UUID.fromString("fd668dac-ad7f-4a24-87a7-c49b435f74bb");

        // given
        Mockito.doNothing().when(personServiceImpl).addOwnerToHouse(personId, houseId);

        // then
        mockMvc.perform(put("/persons/{personId}/houses/{houseId}", personId, houseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    void getHousesByPersonId_success() throws Exception {

        // when
        ResponseDtoPerson responseDtoPerson = PersonTestBuilder.builder().build().responseDtoPerson();
        UUID personId = UUID.fromString(responseDtoPerson.uuid());
        int size = PersonTestBuilder.builder().build().getDtoHouses().size();

        Mockito.when(personServiceImpl.getById(personId)).thenReturn(responseDtoPerson);
        Mockito.when(personServiceImpl.getHousesByPersonId(personId))
                .thenReturn(PersonTestBuilder.builder().build().getDtoHouses());

        // then
        mockMvc.perform(get("/persons/{personId}/houses", personId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$",hasSize(size)));
    }
    /*

      */

    @Test
    void getOwnedHousesByPersonId() throws Exception {
        // when
        UUID personId = UUID.fromString("fd668dac-ad7f-4a24-87a7-c49b435f74bb");
        int size = PersonTestBuilder.builder().build().getDtoHouses().size();
        Mockito.when(personServiceImpl.getOwnedHousesByPersonId(personId))
                .thenReturn(PersonTestBuilder.builder().build().getDtoHouses());

        // then
        mockMvc.perform(get("/persons/{id}/owned-houses", personId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(size)))
                .andDo(MockMvcResultHandlers.print());
    }

}