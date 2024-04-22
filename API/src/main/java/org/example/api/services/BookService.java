package org.example.api.services;

import jakarta.ejb.Local;
import org.example.api.entities.BookEntity;

import java.util.List;

@Local
public interface BookService {
    BookEntity getBook(int id);

    List<BookEntity> getAllBooks();

    BookEntity createBook(BookEntity book);

    void deleteBook(int id);

    //Handle books for user
    List<BookEntity> getUserBooks(int userId);

    BookEntity addBookToUser(int userId, BookEntity bookEntity);

    void removeBookFromUser(int userId, int bookId);
}
