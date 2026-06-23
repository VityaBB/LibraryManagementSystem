package com.library.service;

import com.library.dto.create.GenreCreateDTO;
import com.library.dto.update.GenreUpdateDTO;
import com.library.dto.response.GenreResponseDTO;
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

    public Page<GenreResponseDTO> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    public GenreResponseDTO getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        return convertToResponseDTO(genre);
    }

    @Transactional
    public GenreResponseDTO createGenre(GenreCreateDTO dto) {
        Genre genre = new Genre();
        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        return convertToResponseDTO(genreRepository.save(genre));
    }

    @Transactional
    public GenreResponseDTO updateGenre(Long id, GenreUpdateDTO dto) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Жанр не найден"));
        if (dto.getName() != null) {
            genre.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            genre.setDescription(dto.getDescription());
        }
        return convertToResponseDTO(genreRepository.save(genre));
    }

    @Transactional
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }

    private GenreResponseDTO convertToResponseDTO(Genre genre) {
        GenreResponseDTO dto = new GenreResponseDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        dto.setDescription(genre.getDescription());
        return dto;
    }
}