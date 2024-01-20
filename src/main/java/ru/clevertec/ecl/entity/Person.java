package ru.clevertec.ecl.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Data
@Entity
@Table(name = "person")
@EqualsAndHashCode(exclude = {"house", "ownedHouses"})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "uuid", unique = true, nullable = false, columnDefinition = "UUID")
    private UUID uuid;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Size(max = 120)
    @Column(name = "surname", nullable = false)
    private String surname;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = false)
    private Sex sex;

    @NotNull
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @NotNull
    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;

    @Embedded
    private Passport passport;
    @Transient
    private boolean isOwner;

    @ManyToOne
    @JoinColumn(name = "house_id")
    private House house;

    @ManyToMany(mappedBy = "owners", cascade = CascadeType.ALL)
    private Set<House> ownedHouses;

    public enum Sex {
        Male, Female
    }
}
