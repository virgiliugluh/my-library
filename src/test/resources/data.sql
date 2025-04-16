CREATE TABLE IF NOT EXISTS books
(
    id bigint auto_increment NOT NULL,
    title character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    isbn character varying(255) NOT NULL,
    is_loaned boolean NOT NULL DEFAULT false,
    CONSTRAINT books_pkey PRIMARY KEY (id),
    CONSTRAINT books_isbn_key UNIQUE (isbn)
    );
INSERT INTO books(id, title, author, isbn, is_loaned) VALUES (1000, 'Patterns of Enterprise Application Architecture', 'Fowler Martin', 'B008OHVDFM', false);
INSERT INTO books(id, title, author, isbn, is_loaned) VALUES (2000, 'To Delete', 'To Delete', 'To Delete', false);