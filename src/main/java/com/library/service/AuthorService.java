package com.library.service;

import com.library.dto.create.AuthorCreateDTO;
import com.library.dto.update.AuthorUpdateDTO;
import com.library.dto.response.AuthorResponseDTO;
import com.library.model.Author;
import com.library.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    public Page<AuthorResponseDTO> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    public Page<AuthorResponseDTO> searchAuthors(String firstName, String lastName, Pageable pageable) {
        if (firstName != null && firstName.isEmpty()) {
            firstName = null;
        }
        if (lastName != null && lastName.isEmpty()) {
            lastName = null;
        }
        System.out.println("🔍 Поиск авторов с параметрами:");
        System.out.println("  firstName: " + firstName);
        System.out.println("  lastName: " + lastName);
        return authorRepository.searchAuthors(firstName, lastName, pageable)
                .map(this::convertToResponseDTO);
    }

    public AuthorResponseDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));
        return convertToResponseDTO(author);
    }

    @Transactional
    public AuthorResponseDTO createAuthor(AuthorCreateDTO dto) {
        Author author = new Author();
        author.setFirstName(dto.getFirstName());
        author.setLastName(dto.getLastName());
        if (dto.getBirthDate() != null) {
            author.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        }
        author.setBiography(dto.getBiography());
        return convertToResponseDTO(authorRepository.save(author));
    }

    @Transactional
    public AuthorResponseDTO updateAuthor(Long id, AuthorUpdateDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));
        if (dto.getFirstName() != null) {
            author.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            author.setLastName(dto.getLastName());
        }
        if (dto.getBirthDate() != null) {
            author.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        }
        if (dto.getBiography() != null) {
            author.setBiography(dto.getBiography());
        }
        return convertToResponseDTO(authorRepository.save(author));
    }

    @Transactional
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorResponseDTO convertToResponseDTO(Author author) {
        AuthorResponseDTO dto = new AuthorResponseDTO();
        dto.setId(author.getId());
        dto.setFirstName(author.getFirstName());
        dto.setLastName(author.getLastName());
        dto.setFullName(author.getFullName());
        dto.setBirthDate(author.getBirthDate() != null ? author.getBirthDate().toString() : null);
        dto.setBiography(author.getBiography());
        return dto;
    }
}