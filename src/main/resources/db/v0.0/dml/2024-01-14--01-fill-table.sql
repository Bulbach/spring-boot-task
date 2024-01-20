
INSERT INTO house (uuid, area, country, city, street, houseNumber, create_date)
VALUES
    ('b7d69c82-9833-4364-9c77-a0ecfc467c63', 120.5, 'Country1', 'City1', 'Street1', '123', NOW()),
    ('0d307e34-d7cb-4673-9ddd-92d64409995e', 150.0, 'Country2', 'City2', 'Street2', '456', NOW()),
    ('6d06634d-453d-4a8f-854b-04b1e32756a5', 200.2, 'Country3', 'City3', 'Street3', '789', NOW()),
    ('1f4c2b6c-379e-4163-bc69-f3dc6210869a', 180.0, 'Country4', 'City4', 'Street4', '987', NOW()),
    ('04e9eedb-92be-40fb-97b4-146266f8a321', 160.5, 'Country5', 'City5', 'Street5', '654', NOW());


INSERT INTO person (uuid, name, surname, sex, create_date, update_date, passportSeries, passportNumber, house_id)
VALUES
    ('35273621-b7a1-41c2-beaf-31c7cce3410f', 'John', 'Doe', 'Male', NOW(), NOW(), 'AB123', '123456', 1),
    ('df04c914-d23a-4408-b456-7b1686bb665e', 'Jane', 'Doe', 'Female', NOW(), NOW(), 'CD456', '789012', 2),
    ('fd668dac-ad7f-4a24-87a7-c49b435f74bb', 'Bob', 'Smith', 'Male', NOW(), NOW(), 'EF789', '345678', 3),
    ('7ab1945c-81e0-4ab0-b41f-e776d13ac6a3', 'Alice', 'Johnson', 'Female', NOW(), NOW(), 'GH012', '901234', 4),
    ('c8522e3e-d05f-4720-8cfc-fa3afebf9349', 'Charlie', 'Brown', 'Male', NOW(), NOW(), 'IJ345', '567890', 5),
    ('f787bb95-6cf9-48fc-9df6-1900c7bd41ff', 'Eva', 'Williams', 'Female', NOW(), NOW(), 'KL678', '234567', 1),
    ('328ab626-007e-441d-b98f-8ed6ce28f643', 'Tom', 'Smith', 'Male', NOW(), NOW(), 'MN901', '890123', 2),
    ('47a0119d-4e4b-4bd5-b029-b1af3a362025', 'Lucy', 'Johnson', 'Female', NOW(), NOW(), 'OP234', '456789', 3),
    ('c60ec4d9-d5db-439e-a6d8-fdd163c02d2e', 'Michael', 'Davis', 'Male', NOW(), NOW(), 'QR567', '123890', 4),
    ('255f6859-fcfc-423d-867f-b06d18d7007a', 'Olivia', 'Miller', 'Female', NOW(), NOW(), 'ST890', '567012', 5);