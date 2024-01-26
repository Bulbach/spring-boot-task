package ru.clevertec.ecl.controller;

import lombok.RequiredArgsConstructor;
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
import ru.clevertec.ecl.dto.requestDto.RequestDtoPerson;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoHouse;
import ru.clevertec.ecl.dto.responseDto.ResponseDtoPerson;
import ru.clevertec.ecl.service.service.impl.PersonService;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<Collection<ResponseDtoPerson>> getAllPersons(@RequestParam(defaultValue = "15") int size) {

        Collection<ResponseDtoPerson> persons = personService.getAllPersons(size);
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDtoPerson> getById(@PathVariable UUID id) {

        ResponseDtoPerson person = personService.getById(id);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }
//    @GetMapping("/{personId}/own-houses")
//    public ResponseEntity<Collection<ResponseDtoHouse>> getOwnedHouses(@PathVariable UUID personId) {
//
//        Collection<ResponseDtoHouse> ownedHouses = personService.getOwnedHouses(personId);
//        return new ResponseEntity<>(ownedHouses, HttpStatus.OK);
//    }

    @PostMapping
    public ResponseEntity<ResponseDtoPerson> createPerson(@RequestBody RequestDtoPerson requestDtoPerson) {

        ResponseDtoPerson createdPerson = personService.create(requestDtoPerson);
        return new ResponseEntity<>(createdPerson, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDtoPerson> updatePerson(@PathVariable UUID id, @RequestBody RequestDtoPerson requestDtoPerson) {

        ResponseDtoPerson updatedPerson = personService.update(id, requestDtoPerson);
        return new ResponseEntity<>(updatedPerson, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable UUID id) {

        personService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{personId}/houses")
    public ResponseEntity<Collection<ResponseDtoHouse>> getHousesByPersonId(@PathVariable UUID personId) {
        List<ResponseDtoHouse> housesByPersonId = personService.getHousesByPersonId(personId);
        return new ResponseEntity<>(housesByPersonId, HttpStatus.OK);
    }

    @GetMapping("/{personId}/owned-houses")
    public ResponseEntity<Collection<ResponseDtoHouse>> getOwnedHousesByPersonId(@PathVariable UUID personId) {
        List<ResponseDtoHouse> ownedHousesByPersonId = personService.getOwnedHousesByPersonId(personId);
        return new ResponseEntity<>(ownedHousesByPersonId, HttpStatus.OK);
    }
}
