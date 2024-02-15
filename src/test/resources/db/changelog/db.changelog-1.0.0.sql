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

-- Связь для владельцев
create TABLE house_owners (
                              house_id BIGINT,
                              person_id BIGINT,
                              PRIMARY KEY (house_id, person_id),
                              FOREIGN KEY (house_id) REFERENCES house(id),
                              FOREIGN KEY (person_id) REFERENCES person(id)
);

create TABLE IF NOT EXISTS house_history (
                                             id SERIAL PRIMARY KEY,
                                             house_id INTEGER REFERENCES house(id),
    person_id INTEGER REFERENCES person(id),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    type VARCHAR(10) CHECK (type IN ('OWNER', 'TENANT')) NOT NULL
    );

CREATE OR REPLACE FUNCTION person_house_change_trigger()
    RETURNS TRIGGER
AS $$
BEGIN
    IF (TG_OP = 'INSERT' OR NEW.house_id IS DISTINCT FROM OLD.house_id) THEN
        INSERT INTO house_history (house_id, person_id, date, type)
        VALUES (NEW.house_id, NEW.id, current_timestamp, 'TENANT');
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER person_house_change
    AFTER INSERT OR UPDATE ON person
                        FOR EACH ROW
                        EXECUTE FUNCTION person_house_change_trigger();

CREATE
OR REPLACE FUNCTION house_owner_change_trigger()
RETURNS TRIGGER
AS $$
BEGIN
    IF
(TG_OP = 'INSERT' OR NEW.person_id IS DISTINCT FROM OLD.person_id) THEN
        INSERT INTO house_history (house_id, person_id, date, type)
        VALUES (NEW.house_id, NEW.person_id, current_timestamp, 'OWNER');
END IF;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER house_owner_change
    AFTER INSERT OR
UPDATE ON house_owners
    FOR EACH ROW
    EXECUTE FUNCTION house_owner_change_trigger();