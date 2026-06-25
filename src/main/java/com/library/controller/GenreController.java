package com.library.controller;

import com.library.dto.create.GenreCreateDTO;
import com.library.dto.update.GenreUpdateDTO;
import com.library.dto.response.GenreResponseDTO;
import com.library.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<Page<GenreResponseDTO>> getAllGenres(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) String name) {
        System.out.println("📥 Получен запрос с параметрами:");
        System.out.println("  name: " + name);
        Page<GenreResponseDTO> genres = genreService.searchGenres(name, pageable);
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponseDTO> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    @PostMapping
    public ResponseEntity<GenreResponseDTO> createGenre(@RequestBody GenreCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(genreService.createGenre(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResponseDTO> updateGenre(@PathVariable Long id, @RequestBody GenreUpdateDTO dto) {
        return ResponseEntity.ok(genreService.updateGenre(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}