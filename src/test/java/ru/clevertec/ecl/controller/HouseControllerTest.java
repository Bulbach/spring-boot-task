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
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.service.service.impl.HouseService;
import ru.clevertec.ecl.util.HouseTestBuilder;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HouseController.class)
public class HouseControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private HouseService<ResponseDtoHouse, RequestDtoHouse> houseServiceImpl;

    @Test
    void getAll() throws Exception {

        // when
        List<ResponseDtoHouse> responseDtoHouses = HouseTestBuilder.builder().build().dtoHouses();
        Mockito.when(houseServiceImpl.getAll(3))
                .thenReturn(responseDtoHouses);

        // then
        mockMvc.perform(get("/houses?size=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
        ;
    }

    @Test
    void getHouseById() throws Exception {

        // when
        UUID houseId = HouseTestBuilder.testHouse().getUuid();
        Mockito.when(this.houseServiceImpl.getById(houseId)).thenReturn(HouseTestBuilder.builder().build().buildResponseDtoHouse());

        // then
        mockMvc.perform(get("/houses/{id}", houseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(houseId.toString()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createHouse_success() throws Exception {

        // when
        RequestDtoHouse createHouse = HouseTestBuilder.builder().build().buildCreateRequestDtoHouse();
        ResponseDtoHouse createdHouse = HouseTestBuilder.builder().build().buildResponseDtoHouse();

        // given
        Mockito.when(houseServiceImpl.create(createHouse)).thenReturn(createdHouse);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/houses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createHouse));

        // then
        mockMvc.perform(mockRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.country", is("Burundia")));
    }

    @Test
    public void updateHouse_success() throws Exception {

        // when
        RequestDtoHouse requestDtoHouse = HouseTestBuilder.builder().build().buildRequestDtoHouse();
        ResponseDtoHouse responseDtoHouse = HouseTestBuilder.builder().build().buildResponseDtoHouse();
        RequestDtoHouse updateRequestDtoHouse =
                new RequestDtoHouse(requestDtoHouse.uuid()
                        , 201.3
                        , requestDtoHouse.country()
                        , requestDtoHouse.city()
                        , "DeepWhole"
                        , "15"
                        , requestDtoHouse.createDate());
        ResponseDtoHouse updateResponseDtoHouse = new ResponseDtoHouse(requestDtoHouse.uuid()
                , 201.3
                , requestDtoHouse.country()
                , requestDtoHouse.city()
                , "DeepWhole"
                , "15"
                , requestDtoHouse.createDate());

        // given
        Mockito.when(houseServiceImpl.getById(UUID.fromString(requestDtoHouse.uuid()))).thenReturn(responseDtoHouse);
        Mockito.when(houseServiceImpl.create(updateRequestDtoHouse)).thenReturn(updateResponseDtoHouse);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/houses")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updateRequestDtoHouse));

        // then
        mockMvc.perform(mockRequest)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.street", is("DeepWhole")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteHouseById_success() throws Exception {

        // when
        RequestDtoHouse requestDtoHouse = HouseTestBuilder.builder().build().buildRequestDtoHouse();
        ResponseDtoHouse responseDtoHouse = HouseTestBuilder.builder().build().buildResponseDtoHouse();
        Mockito.when(houseServiceImpl.getById(UUID.fromString(requestDtoHouse.uuid()))).thenReturn(responseDtoHouse);

        // then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/houses/{id}", requestDtoHouse.uuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void getResidentsByHouseId_success() throws Exception {

        // when
        RequestDtoHouse requestDtoHouse = HouseTestBuilder.builder().build().buildRequestDtoHouse();
        ResponseDtoHouse responseDtoHouse = HouseTestBuilder.builder().build().buildResponseDtoHouse();
        Mockito.when(houseServiceImpl.getById(UUID.fromString(requestDtoHouse.uuid()))).thenReturn(responseDtoHouse);
        Mockito.when(houseServiceImpl.getResidents(UUID.fromString(requestDtoHouse.uuid())))
                .thenReturn(HouseTestBuilder.builder().build().buildDtoResidents());

        // given

        // then
        mockMvc.perform(get("/houses/{houseId}/residents", requestDtoHouse.uuid())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }
    @Test
    public void getOwnersByHouseId_success() throws Exception {

        // when
        RequestDtoHouse requestDtoHouse = HouseTestBuilder.builder().build().buildRequestDtoHouse();
        ResponseDtoHouse responseDtoHouse = HouseTestBuilder.builder().build().buildResponseDtoHouse();
        UUID houseId = UUID.fromString(requestDtoHouse.uuid());
        Mockito.when(houseServiceImpl.getById(UUID.fromString(requestDtoHouse.uuid()))).thenReturn(responseDtoHouse);
        Mockito.when(houseServiceImpl.getOwnersByHouseId(houseId)).thenReturn(HouseTestBuilder.builder().build().buildDtoOwners());

        // then
        mockMvc.perform(get("/houses//{houseId}/owners", houseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getTenantsByHouseId_success() throws Exception{

        // when
        RequestDtoHouse requestDtoHouse = HouseTestBuilder.builder().build().buildRequestDtoHouse();
        ResponseDtoHouse responseDtoHouse = HouseTestBuilder.builder().build().buildResponseDtoHouse();
        UUID houseId = UUID.fromString(requestDtoHouse.uuid());
        Mockito.when(houseServiceImpl.getById(UUID.fromString(requestDtoHouse.uuid()))).thenReturn(responseDtoHouse);
        Mockito.when(houseServiceImpl.getTenantsByHouseId(houseId)).thenReturn(HouseTestBuilder.builder().build().buildDtoTenants());

        // then
        mockMvc.perform(get("/houses/{houseId}/tenants", houseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

}

