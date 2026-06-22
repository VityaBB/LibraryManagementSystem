package com.library.service;

import com.library.dto.AuthorDTO;
import com.library.dto.BookDTO;
import com.library.dto.GenreDTO;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.Genre;
import com.library.model.Publisher;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.GenreRepository;
import com.library.repository.PublisherRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final PublisherRepository publisherRepository;

    public Page<BookDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::convertToDTO);
    }

    public Page<BookDTO> searchBooks(String title, Long authorId, Long genreId, Pageable pageable) {
        return bookRepository.searchBooks(title, authorId, genreId, pageable)
                .map(this::convertToDTO);
    }

    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        return convertToDTO(book);
    }

    @Transactional
    public BookDTO createBook(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublicationYear(dto.getPublicationYear());
        book.setTotalCopies(dto.getTotalCopies());
        book.setPageCount(dto.getPageCount());
        book.setDescription(dto.getDescription());

        if (dto.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                    .orElseThrow(() -> new RuntimeException("Издатель не найден"));
            book.setPublisher(publisher);
        }

        if (dto.getAuthors() != null) {
            for (AuthorDTO authorDTO : dto.getAuthors()) {
                Author author = authorRepository.findById(authorDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Автор не найден"));
                book.getAuthors().add(author);
            }
        }

        if (dto.getGenres() != null) {
            for (GenreDTO genreDTO : dto.getGenres()) {
                Genre genre = genreRepository.findById(genreDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Жанр не найден"));
                book.getGenres().add(genre);
            }
        }

        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublicationYear(dto.getPublicationYear());
        book.setTotalCopies(dto.getTotalCopies());
        book.setPageCount(dto.getPageCount());
        book.setDescription(dto.getDescription());

        if (dto.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                    .orElseThrow(() -> new RuntimeException("Издатель не найден"));
            book.setPublisher(publisher);
        }

        book.getAuthors().clear();
        if (dto.getAuthors() != null) {
            for (AuthorDTO authorDTO : dto.getAuthors()) {
                Author author = authorRepository.findById(authorDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Автор не найден"));
                book.getAuthors().add(author);
            }
        }

        book.getGenres().clear();
        if (dto.getGenres() != null) {
            for (GenreDTO genreDTO : dto.getGenres()) {
                Genre genre = genreRepository.findById(genreDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Жанр не найден"));
                book.getGenres().add(genre);
            }
        }

        return convertToDTO(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setPageCount(book.getPageCount());
        dto.setDescription(book.getDescription());

        if (book.getPublisher() != null) {
            dto.setPublisherId(book.getPublisher().getId());
            dto.setPublisherName(book.getPublisher().getName());
        }

        int availableCopies = book.getTotalCopies();
        if (book.getLoans() != null) {
            long activeLoans = book.getLoans().stream()
                    .filter(loan -> loan.getStatus().equals("ACTIVE") || loan.getStatus().equals("OVERDUE"))
                    .count();
            availableCopies = (int) (book.getTotalCopies() - activeLoans);
        }
        dto.setAvailableCopies(Math.max(availableCopies, 0));

        if (book.getAuthors() != null) {
            dto.setAuthors(book.getAuthors().stream()
                    .map(this::convertAuthorToDTO)
                    .collect(Collectors.toList()));
        }

        if (book.getGenres() != null) {
            dto.setGenres(book.getGenres().stream()
                    .map(this::convertGenreToDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private AuthorDTO convertAuthorToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setFullName(author.getFullName());
        dto.setBirthDate(author.getBirthDate() != null ? author.getBirthDate().toString() : null);
        dto.setBiography(author.getBiography());
        return dto;
    }

    private GenreDTO convertGenreToDTO(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setDescription(genre.getDescription());
        return dto;
    }
}