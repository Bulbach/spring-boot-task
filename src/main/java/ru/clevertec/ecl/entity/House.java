package ru.clevertec.ecl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "house")
@EqualsAndHashCode(exclude = {"residents","owners"})
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "uuid", unique = true, nullable = false, columnDefinition = "UUID")
    private UUID uuid;

    @NotNull
    @Column(name = "area", nullable = false)
    private Double area;

    @NotBlank
    @Size(max = 120)
    @Column(name = "country", nullable = false)
    private String country;

    @NotBlank
    @Size(max = 120)
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank
    @Size(max = 120)
    @Column(name = "street", nullable = false)
    private String street;

    @NotBlank
    @Size(max = 120)
    @Column(name = "houseNumber", nullable = false)
    private String houseNumber;

    @NotNull
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "house")
    private Set<Person> residents;

    @ManyToMany
    @JoinTable(
            name = "house_owners",
            joinColumns = @JoinColumn(name = "house_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id")
    )
    private Set<Person> owners;
}
