package com.paj.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private String bookId;
    private String userId;
    private String title;
    private String author;
    private String personalNotes;
    private int rating;
    private int currentPage;
    private int totalPages;
    private String dateStarted;
    private String genre;
}
