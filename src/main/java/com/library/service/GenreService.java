package com.library.service;

import com.library.dto.GenreDTO;
import com.library.model.Genre;
import com.library.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Page<GenreDTO> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable).map(this::convertToDTO);
    }

    public GenreDTO getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        return convertToDTO(genre);
    }

    @Transactional
    public GenreDTO createGenre(GenreDTO dto) {
        Genre genre = new Genre();
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        return convertToDTO(genreRepository.save(genre));
    }

    @Transactional
    public GenreDTO updateGenre(Long id, GenreDTO dto) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        return convertToDTO(genreRepository.save(genre));
    }

    @Transactional
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }

    private GenreDTO convertToDTO(Genre genre) {
        GenreDTO dto = new GenreDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setDescription(genre.getDescription());
        return dto;
    }
}