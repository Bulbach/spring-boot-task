package ru.clevertec.ecl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.ecl.dto.requestDto.RequestDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.service.service.impl.HouseService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/houses")
public class HouseController {

    @Autowired
    private final HouseService houseService;

    @GetMapping
    public ResponseEntity<Collection<ResponseDtoHouse>> getAll(@RequestParam(defaultValue = "15") int size) {

        Collection<ResponseDtoHouse> houses = houseService.getAll(size);
        return new ResponseEntity<>(houses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDtoHouse> getById(@PathVariable("id") UUID id) {

        ResponseDtoHouse house = houseService.getById(id);
        return new ResponseEntity<>(house, HttpStatus.OK);
    }

    @GetMapping("/{houseId}/residents")
    public ResponseEntity<Collection<ResponseDtoPerson>> getResidents(@PathVariable UUID houseId) {

        Collection<ResponseDtoPerson> residents = houseService.getResidents(houseId);
        return new ResponseEntity<>(residents, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResponseDtoHouse> create(@RequestBody RequestDtoHouse requestDtoHouse) {

        ResponseDtoHouse createdHouse = houseService.create(requestDtoHouse);
        return new ResponseEntity<>(createdHouse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDtoHouse> update(@PathVariable("id") UUID id, @RequestBody RequestDtoHouse requestDtoHouse) {

        ResponseDtoHouse updatedHouse = houseService.update(id, requestDtoHouse);
        return new ResponseEntity<>(updatedHouse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {

        houseService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
