create TABLE IF NOT EXISTS house_history (
    id SERIAL PRIMARY KEY,
    house_id INTEGER REFERENCES house(id),
    person_id INTEGER REFERENCES person(id),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    type VARCHAR(10) CHECK (type IN ('OWNER', 'TENANT')) NOT NULL
);