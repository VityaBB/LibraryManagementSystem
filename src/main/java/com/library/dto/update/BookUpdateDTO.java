package com.library.dto.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.library.dto.common.AuthorIdDTO;
import com.library.dto.common.GenreIdDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateDTO {
    private String title;
    private String isbn;
    private Integer publicationYear;
    private Long publisherId;
    private Integer totalCopies;
    private Integer pageCount;
    private String description;
    private List<AuthorIdDTO> authors;
    private List<GenreIdDTO> genres;
}