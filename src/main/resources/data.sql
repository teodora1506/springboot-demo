-- Problem je bio u fajlu data.sql, gde su ID-jevi za test podatke bili eksplicitno postavljeni.
-- Pošto je Hibernate koristio strategiju IDENTITY za automatsko generisanje ID-jeva, nije bio svestan da treba da nastavi od ID-a 3.
-- Zbog toga je pokušavao da koristi ID 1 ili 2, što je dovodilo do konflikta.

INSERT INTO author (name) VALUES ('J.K. Rowling');
INSERT INTO author (name) VALUES ('George Orwell');

INSERT INTO book (title, author_id) VALUES ('Harry Potter and the Sorcerer''s Stone', 1);
INSERT INTO book (title, author_id) VALUES ('1984', 2);