package ru.clevertec.ecl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "passport", uniqueConstraints = @UniqueConstraint(columnNames = {"passportseries", "passportnumber"}))
public class Passport {

    @Column(name = "passportseries", nullable = false)
    private String passportSeries;

    @Column(name = "passportnumber", nullable = false, unique = true)
    private String passportNumber;

}

