package com.library.service;

import com.library.dto.common.AuthorIdDTO;
import com.library.dto.common.GenreIdDTO;
import com.library.dto.create.BookCreateDTO;
import com.library.dto.update.BookUpdateDTO;
import com.library.dto.response.AuthorResponseDTO;
import com.library.dto.response.BookResponseDTO;
import com.library.dto.response.GenreResponseDTO;
import com.library.model.Author;
import com.library.model.Book;
import com.library.model.Genre;
import com.library.model.Publisher;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.GenreRepository;
import com.library.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final PublisherRepository publisherRepository;

    public Page<BookResponseDTO> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    public Page<BookResponseDTO> searchBooks(String title, Long authorId, Long genreId, Pageable pageable) {
        return bookRepository.searchBooks(title, authorId, genreId, pageable)
                .map(this::convertToResponseDTO);
    }

    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));
        return convertToResponseDTO(book);
    }

    @Transactional
    public BookResponseDTO createBook(BookCreateDTO dto) {
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
            for (AuthorIdDTO authorIdDTO : dto.getAuthors()) {
                Author author = authorRepository.findById(authorIdDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Автор не найден"));
                book.getAuthors().add(author);
            }
        }

        if (dto.getGenres() != null) {
            for (GenreIdDTO genreIdDTO : dto.getGenres()) {
                Genre genre = genreRepository.findById(genreIdDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Жанр не найден"));
                book.getGenres().add(genre);
            }
        }

        Book savedBook = bookRepository.save(book);
        return convertToResponseDTO(savedBook);
    }

    @Transactional
    public BookResponseDTO updateBook(Long id, BookUpdateDTO dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Книга не найдена"));

        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getIsbn() != null) {
            book.setIsbn(dto.getIsbn());
        }
        if (dto.getPublicationYear() != null) {
            book.setPublicationYear(dto.getPublicationYear());
        }
        if (dto.getTotalCopies() != null) {
            book.setTotalCopies(dto.getTotalCopies());
        }
        if (dto.getPageCount() != null) {
            book.setPageCount(dto.getPageCount());
        }
        if (dto.getDescription() != null) {
            book.setDescription(dto.getDescription());
        }

        if (dto.getPublisherId() != null) {
            Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                    .orElseThrow(() -> new RuntimeException("Издатель не найден"));
            book.setPublisher(publisher);
        }

        if (dto.getAuthors() != null) {
            book.getAuthors().clear();
            for (AuthorIdDTO authorIdDTO : dto.getAuthors()) {
                Author author = authorRepository.findById(authorIdDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Автор не найден"));
                book.getAuthors().add(author);
            }
        }

        if (dto.getGenres() != null) {
            book.getGenres().clear();
            for (GenreIdDTO genreIdDTO : dto.getGenres()) {
                Genre genre = genreRepository.findById(genreIdDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Жанр не найден"));
                book.getGenres().add(genre);
            }
        }

        return convertToResponseDTO(bookRepository.save(book));
    }

    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private BookResponseDTO convertToResponseDTO(Book book) {
        BookResponseDTO dto = new BookResponseDTO();
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
                    .map(this::convertAuthorToResponseDTO)
                    .collect(Collectors.toList()));
        }

        if (book.getGenres() != null) {
            dto.setGenres(book.getGenres().stream()
                    .map(this::convertGenreToResponseDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private AuthorResponseDTO convertAuthorToResponseDTO(Author author) {
        AuthorResponseDTO dto = new AuthorResponseDTO();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setFullName(author.getFullName());
        dto.setBirthDate(author.getBirthDate() != null ? author.getBirthDate().toString() : null);
        dto.setBiography(author.getBiography());
        return dto;
    }

    private GenreResponseDTO convertGenreToResponseDTO(Genre genre) {
        GenreResponseDTO dto = new GenreResponseDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setDescription(genre.getDescription());
        return dto;
    }
}