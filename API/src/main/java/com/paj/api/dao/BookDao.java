package com.paj.api.dao;

import com.paj.api.entities.BookEntity;

import java.util.List;

public interface BookDao extends GenericDao<BookEntity, Integer> {
    List<BookEntity> getBooksByUserId(int userId);

    BookEntity addBookToUser(int userId, BookEntity bookEntity);

    void removeBookFromUser(int userId, int bookId);
    void deleteById(int bookId);
    BookEntity update(BookEntity bookEntity);
}
