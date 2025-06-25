INSERT INTO author (name) VALUES ('J.K. Rowling');
INSERT INTO author (name) VALUES ('George Orwell');
INSERT INTO author (name) VALUES ('Jane Austen');

-- Pretpostavimo da su id-jevi generisani kao 1, 2, 3 automatski
INSERT INTO book (title, author_id) VALUES ('Harry Potter and the Philosopher''s Stone', 1);
INSERT INTO book (title, author_id) VALUES ('1984', 2);
INSERT INTO book (title, author_id) VALUES ('Pride and Prejudice', 3);

