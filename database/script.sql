CREATE TABLE IF NOT EXISTS authors (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE,
    biography TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS publishers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    publication_year INTEGER CHECK (publication_year > 0),
    publisher_id INTEGER REFERENCES publishers(id) ON DELETE SET NULL,
    total_copies INTEGER DEFAULT 1 CHECK (total_copies >= 0),
    available_copies INTEGER DEFAULT 1 CHECK (available_copies >= 0),
    page_count INTEGER CHECK (page_count > 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS genres (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS book_authors (
    book_id INTEGER REFERENCES books(id) ON DELETE CASCADE,
    author_id INTEGER REFERENCES authors(id) ON DELETE CASCADE,
    author_order INTEGER DEFAULT 1,
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE IF NOT EXISTS book_genres (
    book_id INTEGER REFERENCES books(id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE DEFAULT CURRENT_DATE,
    is_active BOOLEAN DEFAULT TRUE,
    role VARCHAR(20) DEFAULT 'READER' CHECK (role IN ('READER', 'LIBRARIAN', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loans (
    id SERIAL PRIMARY KEY,
    book_id INTEGER REFERENCES books(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    loan_date DATE NOT NULL DEFAULT CURRENT_DATE,
    due_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'RETURNED', 'OVERDUE', 'LOST')),
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reviews (
    id SERIAL PRIMARY KEY,
    book_id INTEGER REFERENCES books(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    rating INTEGER CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    review_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (book_id, user_id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    book_id INTEGER REFERENCES books(id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    reservation_date DATE DEFAULT CURRENT_DATE,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'FULFILLED', 'CANCELLED', 'EXPIRED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (book_id, user_id, status)
);

CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_publisher_id ON books(publisher_id);
CREATE INDEX IF NOT EXISTS idx_loans_book_id ON loans(book_id);
CREATE INDEX IF NOT EXISTS idx_loans_user_id ON loans(user_id);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(status);
CREATE INDEX IF NOT EXISTS idx_loans_due_date ON loans(due_date);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_reviews_book_id ON reviews(book_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reservations_book_id ON reservations(book_id);
CREATE INDEX IF NOT EXISTS idx_reservations_user_id ON reservations(user_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON reservations(status);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_authors_updated_at ON authors;
CREATE TRIGGER update_authors_updated_at
    BEFORE UPDATE ON authors
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_publishers_updated_at ON publishers;
CREATE TRIGGER update_publishers_updated_at
    BEFORE UPDATE ON publishers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_books_updated_at ON books;
CREATE TRIGGER update_books_updated_at
    BEFORE UPDATE ON books
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_genres_updated_at ON genres;
CREATE TRIGGER update_genres_updated_at
    BEFORE UPDATE ON genres
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_loans_updated_at ON loans;
CREATE TRIGGER update_loans_updated_at
    BEFORE UPDATE ON loans
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_reviews_updated_at ON reviews;
CREATE TRIGGER update_reviews_updated_at
    BEFORE UPDATE ON reviews
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_reservations_updated_at ON reservations;
CREATE TRIGGER update_reservations_updated_at
    BEFORE UPDATE ON reservations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE OR REPLACE FUNCTION update_book_availability()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.status = 'ACTIVE' THEN
        UPDATE books SET available_copies = available_copies - 1
        WHERE id = NEW.book_id AND available_copies > 0;
    ELSIF TG_OP = 'UPDATE' AND OLD.status = 'ACTIVE' AND NEW.status = 'RETURNED' THEN
        UPDATE books SET available_copies = available_copies + 1
        WHERE id = NEW.book_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS update_book_availability_on_loan ON loans;
CREATE TRIGGER update_book_availability_on_loan
    AFTER INSERT OR UPDATE ON loans
    FOR EACH ROW EXECUTE FUNCTION update_book_availability();

INSERT INTO authors (first_name, last_name, birth_date, biography) VALUES
('Лев', 'Толстой', '1828-09-09', 'Великий русский писатель, мыслитель.'),
('Фёдор', 'Достоевский', '1821-11-11', 'Один из самых значительных русских писателей.'),
('Джордж', 'Оруэлл', '1903-06-25', 'Английский писатель и публицист.'),
('Антуан', 'Сент-Экзюпери', '1900-06-29', 'Французский писатель, поэт и профессиональный лётчик.');

INSERT INTO publishers (name, address, phone, email, website) VALUES
('Эксмо', 'Москва, ул. Октябрьская, 14', '+7(495)123-45-67', 'info@exmo.ru', 'https://exmo.ru'),
('АСТ', 'Москва, ул. Пятницкая, 12', '+7(495)234-56-78', 'info@ast.ru', 'https://ast.ru'),
('Азбука', 'Санкт-Петербург, Невский пр., 100', '+7(812)345-67-89', 'info@azbuka.ru', 'https://azbuka.ru');

INSERT INTO genres (name, description) VALUES
('Роман', 'Литературный жанр, повествующий о жизни и судьбе героев.'),
('Повесть', 'Средний эпический жанр, меньше романа, но больше рассказа.'),
('Детектив', 'Жанр, повествующий о расследовании преступлений.'),
('Фантастика', 'Жанр, основанный на фантастическом допущении.'),
('Поэзия', 'Стихотворные произведения.');

INSERT INTO books (title, isbn, publication_year, publisher_id, total_copies, available_copies, page_count, description) VALUES
('Война и мир', '978-5-04-123456-7', 1869, 1, 5, 5, 1300, 'Грандиозный роман-эпопея о России в эпоху Наполеоновских войн.'),
('Преступление и наказание', '978-5-17-987654-3', 1866, 2, 3, 3, 680, 'Социально-психологический роман о студенте Раскольникове.'),
('1984', '978-0-452-28423-4', 1949, 3, 4, 4, 328, 'Роман-антиутопия о тоталитарном обществе.'),
('Маленький принц', '978-5-389-12345-6', 1943, 1, 6, 6, 120, 'Философская сказка о дружбе и любви.');

INSERT INTO book_authors (book_id, author_id, author_order) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 1),
(4, 4, 1);

INSERT INTO book_genres (book_id, genre_id) VALUES
(1, 1), (1, 2),
(2, 1), (2, 3),
(3, 1), (3, 4),
(4, 2), (4, 4);

INSERT INTO users (email, password_hash, first_name, last_name, phone, address, role) VALUES
('ivanov@mail.ru', 'hash_123456', 'Иван', 'Иванов', '+7(912)111-22-33', 'Москва, ул. Мира, 1', 'READER'),
('petrov@mail.ru', 'hash_789012', 'Пётр', 'Петров', '+7(912)444-55-66', 'Москва, ул. Пушкина, 2', 'READER'),
('admin@library.ru', 'hash_admin', 'Админ', 'Админов', '+7(912)777-88-99', 'Москва, ул. Ленина, 3', 'ADMIN'),
('librarian@library.ru', 'hash_lib', 'Библиотекарь', 'Библиотекаров', '+7(912)000-11-22', 'Москва, ул. Центральная, 4', 'LIBRARIAN');

INSERT INTO loans (book_id, user_id, loan_date, due_date, status) VALUES
(1, 1, '2026-01-15', '2026-02-15', 'ACTIVE'),
(2, 2, '2026-01-20', '2026-02-20', 'ACTIVE');

INSERT INTO reviews (book_id, user_id, rating, comment) VALUES
(1, 1, 5, 'Шедевр! Одна из лучших книг, которые я читал.'),
(2, 2, 4, 'Очень глубокая и пронзительная книга.'),
(3, 1, 5, 'Актуально до сих пор.'),
(4, 2, 5, 'Тёплая и трогательная история.');

SELECT 
    b.title,
    STRING_AGG(DISTINCT a.last_name, ', ') AS authors,
    STRING_AGG(DISTINCT g.name, ', ') AS genres,
    p.name AS publisher,
    b.total_copies,
    b.available_copies
FROM books b
LEFT JOIN book_authors ba ON b.id = ba.book_id
LEFT JOIN authors a ON ba.author_id = a.id
LEFT JOIN book_genres bg ON b.id = bg.book_id
LEFT JOIN genres g ON bg.genre_id = g.id
LEFT JOIN publishers p ON b.publisher_id = p.id
GROUP BY b.id, p.name
ORDER BY b.title;

SELECT 
    l.id,
    u.first_name || ' ' || u.last_name AS reader,
    b.title AS book_title,
    l.loan_date,
    l.due_date,
    l.status,
    l.fine_amount
FROM loans l
JOIN users u ON l.user_id = u.id
JOIN books b ON l.book_id = b.id
WHERE l.status = 'ACTIVE'
ORDER BY l.due_date;