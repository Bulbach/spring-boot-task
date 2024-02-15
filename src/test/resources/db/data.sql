insert into house (id,uuid, area, country, city, street, houseNumber, create_date)
values
    (1,'b7d69c82-9833-4364-9c77-a0ecfc467c63', 120.5, 'Country1', 'City1', 'Street1', '123', now()),
    (2,'0d307e34-d7cb-4673-9ddd-92d64409995e', 150.0, 'Country2', 'City2', 'Street2', '456', now()),
    (3,'6d06634d-453d-4a8f-854b-04b1e32756a5', 200.2, 'Country3', 'City3', 'Street3', '789', now()),
    (4,'1f4c2b6c-379e-4163-bc69-f3dc6210869a', 180.0, 'Country4', 'City4', 'Street4', '987', now()),
    (5,'04e9eedb-92be-40fb-97b4-146266f8a321', 160.5, 'Country5', 'City5', 'Street5', '654', now());


insert into person (id,uuid, name, surname, sex, create_date, update_date, passportSeries, passportNumber, house_id)
values
    (1,'35273621-b7a1-41c2-beaf-31c7cce3410f', 'John', 'Doe', 'Male', now(), now(), 'AB123', '123456', 1),
    (2,'df04c914-d23a-4408-b456-7b1686bb665e', 'Jane', 'Doe', 'Female', now(), now(), 'CD456', '789012', 2),
    (3,'fd668dac-ad7f-4a24-87a7-c49b435f74bb', 'Bob', 'Smith', 'Male', now(), now(), 'EF789', '345678', 3),
    (4,'7ab1945c-81e0-4ab0-b41f-e776d13ac6a3', 'Alice', 'Johnson', 'Female', now(), now(), 'GH012', '901234', 4),
    (5,'c8522e3e-d05f-4720-8cfc-fa3afebf9349', 'Charlie', 'Brown', 'Male', now(), now(), 'IJ345', '567890', 5),
    (6,'f787bb95-6cf9-48fc-9df6-1900c7bd41ff', 'Eva', 'Williams', 'Female', now(), now(), 'KL678', '234567', 1),
    (7,'328ab626-007e-441d-b98f-8ed6ce28f643', 'Tom', 'Smith', 'Male', now(), now(), 'MN901', '890123', 2),
    (8,'47a0119d-4e4b-4bd5-b029-b1af3a362025', 'Lucy', 'Johnson', 'Female', now(), now(), 'OP234', '456789', 3),
    (9,'c60ec4d9-d5db-439e-a6d8-fdd163c02d2e', 'Michael', 'Davis', 'Male', now(), now(), 'QR567', '123890', 4),
    (10,'255f6859-fcfc-423d-867f-b06d18d7007a', 'Olivia', 'Miller', 'Female', now(), now(), 'ST890', '567012', 5);

insert into house_owners (house_id, person_id) values
                                                   (1, 6), (2, 7), (3, 8), (4, 9), (5, 10);