package com.paj.api.services;

import com.paj.api.dao.BookDao;
import com.paj.api.entities.BookEntity;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class BookServiceImpl implements BookService {

    @EJB
    private BookDao bookDao;

    @Override
    public BookEntity getBookById(int id) {
        return bookDao.find(id);
    }

    @Override
    public List<BookEntity> getAllBooks() {
        return bookDao.findAll();
    }

    @Override
    public BookEntity createBook(BookEntity bookEntity) {
        return bookDao.save(bookEntity);
    }

    @Override
    public void deleteBook(int id) {
        BookEntity bookEntity = bookDao.find(id);
        if (bookEntity != null) {
            bookDao.delete(bookEntity);
        }
    }

    // Handle books for user
    @Override
    public List<BookEntity> getUserBooks(int userId) {
        return bookDao.getBooksByUserId(userId);
    }

    @Override
    public BookEntity addBookToUser(int userId, BookEntity book) {
        return bookDao.addBookToUser(userId, book);
    }

    @Override
    public void removeBookFromUser(int userId, int bookId) {
        bookDao.removeBookFromUser(userId, bookId);
    }
}
