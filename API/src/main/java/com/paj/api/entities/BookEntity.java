package com.paj.api.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "book")
@Getter
@Setter
public class BookEntity {

    public BookEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;
    @Column(name = "book_title")
    private String title;
    @Column(name = "book_author")
    private String author;
    @Lob
    @Column(name = "book_personal_notes")
    private String personalNotes;
    @Column(name = "book_rating")
    private Integer rating;
    @Column(name = "book_current_page")
    private Integer currentPage;
    @Column(name = "book_total_pages")
    private Integer totalPages;
    @Column(name = "book_date_started")
    private LocalDate dateStarted;
    @Column(name = "book_genre")
    private String genre;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity user;
}
