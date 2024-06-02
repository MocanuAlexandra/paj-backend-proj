package com.paj.api.dao;

import com.paj.api.entities.BookEntity;
import com.paj.api.entities.UserEntity;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class BookDaoImpl implements BookDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BookEntity find(Integer integer) {
        return entityManager.find(BookEntity.class, integer);
    }

    @Override
    public List<BookEntity> findAll() {
        return entityManager.createQuery("SELECT b FROM BookEntity b", BookEntity.class).getResultList();
    }

    @Override
    public BookEntity save(BookEntity bookEntity) {
        if (bookEntity.getBookId() == 0) {
            entityManager.persist(bookEntity);
        } else {
            entityManager.merge(bookEntity);
        }
        return bookEntity;
    }
    @Override
    public BookEntity update(BookEntity bookEntity) {
        return entityManager.merge(bookEntity);
    }

    @Override
    public void delete(BookEntity bookEntity) {
        entityManager.remove(entityManager.contains(bookEntity) ? bookEntity : entityManager.merge(bookEntity));
    }

    @Override
    public void deleteById(int bookId) {
        BookEntity book = entityManager.find(BookEntity.class, bookId);
        if (book != null) {
            entityManager.remove(book);
        }
    }

    @Override
    public List<BookEntity> getBooksByUserId(int userId) {
        return entityManager.createQuery("SELECT b FROM BookEntity b WHERE b.user.userId = :userId", BookEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public BookEntity addBookToUser(int userId, BookEntity bookEntity) {
        UserEntity user = entityManager.find(UserEntity.class, userId);
        if (user != null) {
            List<BookEntity> books = user.getBooks();
            books.add(bookEntity);
            user.setBooks(books);
            entityManager.merge(user);
            return bookEntity;
        }
        return null;
    }

    @Override
    public void removeBookFromUser(int userId, int bookId) {
        UserEntity user = entityManager.find(UserEntity.class, userId);
        if (user != null) {
            List<BookEntity> books = user.getBooks();
            BookEntity book = entityManager.find(BookEntity.class, bookId);
            if (book != null) {
                books.remove(book);
                user.setBooks(books);
                entityManager.merge(user);
            }
        }
    }
}
