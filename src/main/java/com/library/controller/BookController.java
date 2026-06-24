package com.library.controller;

import com.library.dto.create.BookCreateDTO;
import com.library.dto.update.BookUpdateDTO;
import com.library.dto.response.BookResponseDTO;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<BookResponseDTO>> getAllBooks(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer publicationYear) {
        
        Page<BookResponseDTO> books;
        if (title != null || authorId != null || genreId != null || publicationYear != null) {
            books = bookService.searchBooks(title, authorId, genreId, publicationYear, pageable);
        } else {
            books = bookService.getAllBooks(pageable);
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookResponseDTO> createBook(@RequestBody BookCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @RequestBody BookUpdateDTO dto) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}