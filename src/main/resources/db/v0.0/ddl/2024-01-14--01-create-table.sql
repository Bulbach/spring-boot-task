create TABLE house (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    area DOUBLE PRECISION NOT NULL,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    houseNumber VARCHAR(255) NOT NULL,
    create_date TIMESTAMP NOT NULL
);

create TABLE person (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    sex VARCHAR(255) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    passportSeries VARCHAR(255) NOT NULL,
    passportNumber VARCHAR(255) UNIQUE NOT NULL,
    house_id BIGINT,
    FOREIGN KEY (house_id) REFERENCES house(id)
);

-- Связь для жильцов
create TABLE house_residents (
    house_id BIGINT,
    person_id BIGINT,
    PRIMARY KEY (house_id, person_id),
    FOREIGN KEY (house_id) REFERENCES house(id),
    FOREIGN KEY (person_id) REFERENCES person(id)
);

-- Связь для владельцев
create TABLE house_owners (
    house_id BIGINT,
    person_id BIGINT,
    PRIMARY KEY (house_id, person_id),
    FOREIGN KEY (house_id) REFERENCES house(id),
    FOREIGN KEY (person_id) REFERENCES person(id)
);