package ru.clevertec.ecl.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "person")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"ownedHouses", "houseHistories"})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne()
    @JoinColumn(name = "house_id")
    @ToString.Exclude
    private House house;

    @Transient
    private boolean owner;

    @ManyToMany(mappedBy = "owners", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<House> ownedHouses;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY, orphanRemoval = false, cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<HouseHistory> houseHistories;


    public enum Sex {
        Male, Female
    }

    public void addHouse(House house) {
        this.house = house;
        house.addResident(this);
    }

    public void addOwnedHouse(House house) {
        ownedHouses.add(house);
        house.getOwners().add(this);
    }

    public void removeOwnedHouse(House house) {
        ownedHouses.remove(house);
        house.getOwners().remove(this);
    }
}
