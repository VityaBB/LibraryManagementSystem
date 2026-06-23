package com.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDTO {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private Long publisherId;
    private String publisherName;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer pageCount;
    private String description;
    private List<AuthorResponseDTO> authors;
    private List<GenreResponseDTO> genres;
}