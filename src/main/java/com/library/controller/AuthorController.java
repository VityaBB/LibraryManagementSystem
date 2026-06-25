package com.library.controller;

import com.library.dto.create.AuthorCreateDTO;
import com.library.dto.update.AuthorUpdateDTO;
import com.library.dto.response.AuthorResponseDTO;
import com.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorResponseDTO>> getAllAuthors(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {
        System.out.println("📥 Получен запрос с параметрами:");
        System.out.println("  firstName: " + firstName);
        System.out.println("  lastName: " + lastName);
        Page<AuthorResponseDTO> authors = authorService.searchAuthors(firstName, lastName, pageable);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorResponseDTO> createAuthor(@RequestBody AuthorCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authorService.createAuthor(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> updateAuthor(@PathVariable Long id, @RequestBody AuthorUpdateDTO dto) {
        return ResponseEntity.ok(authorService.updateAuthor(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}