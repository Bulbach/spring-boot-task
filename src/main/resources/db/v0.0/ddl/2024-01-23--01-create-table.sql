create TABLE house_history (
    id SERIAL PRIMARY KEY,
    house_id INTEGER ,
    person_id INTEGER ,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    type VARCHAR(10) CHECK (type IN ('OWNER', 'TENANT')) NOT NULL
);
