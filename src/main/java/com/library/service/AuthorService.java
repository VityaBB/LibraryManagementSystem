package com.library.service;

import com.library.dto.AuthorDTO;
import com.library.model.Author;
import com.library.repository.AuthorRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    public Page<AuthorDTO> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable).map(this::convertToDTO);
    }

    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));
        return convertToDTO(author);
    }

    @Transactional
    public AuthorDTO createAuthor(AuthorDTO dto) {
        Author author = new Author();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        if (dto.getBirthDate() != null) {
            author.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        }
        author.setBiography(dto.getBiography());
        return convertToDTO(authorRepository.save(author));
    }

    @Transactional
    public AuthorDTO updateAuthor(Long id, AuthorDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        if (dto.getBirthDate() != null) {
            author.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        }
        author.setBiography(dto.getBiography());
        return convertToDTO(authorRepository.save(author));
    }

    @Transactional
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDTO convertToDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setFullName(author.getFullName());
        dto.setBirthDate(author.getBirthDate() != null ? author.getBirthDate().toString() : null);
        dto.setBiography(author.getBiography());
        return dto;
    }
}