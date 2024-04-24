package org.example.api.dao;

import org.example.api.entities.BookEntity;

import java.util.List;

public interface BookDao extends GenericDao<BookEntity, Integer> {
    List<BookEntity> getBooksByUserId(int userId);

    BookEntity addBookToUser(int userId, BookEntity bookEntity);

    void removeBookFromUser(int userId, int bookId);
}
