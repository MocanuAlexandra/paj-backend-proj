package com.paj.api.services;

import com.paj.api.entities.BookEntity;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface BookService {
    BookEntity getBookById(int id);

    List<BookEntity> getAllBooks();

    BookEntity createBook(BookEntity book);

    void deleteBook(int id);

    //Handle books for user
    List<BookEntity> getUserBooks(int userId);

    BookEntity addBookToUser(int userId, BookEntity bookEntity);

    void removeBookFromUser(int userId, int bookId);
}
