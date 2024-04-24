package com.paj.api.controllers;

import com.paj.api.entities.BookEntity;
import com.paj.api.services.BookService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;

import java.util.List;

@Path("/books")
public class BookResource {
    @EJB
    private BookService bookService;

    @GET
    public List<BookEntity> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GET
    @Path("/{id}")
    public BookEntity getBook(@PathParam("id") int id) {
        return bookService.getBookById(id);
    }

    @POST
    public BookEntity createBook(BookEntity bookEntity) {
        return bookService.createBook(bookEntity);
    }

    @DELETE
    @Path("/{id}")
    public void deleteBook(@PathParam("id") int id) {
        bookService.deleteBook(id);
    }

    @GET
    @Path("/user/{userId}")
    public List<BookEntity> getBooksByUserId(@PathParam("userId") int userId) {
        return bookService.getUserBooks(userId);
    }

    @POST
    @Path("/{userId}")
    public BookEntity addBookToUser(@PathParam("userId") int userId, BookEntity bookEntity) {
        return bookService.addBookToUser(userId, bookEntity);
    }

    @DELETE
    @Path("/{bookId}/{userId}")
    public void removeBookFromUser(@PathParam("userId") int userId, @PathParam("bookId") int bookId) {
        bookService.removeBookFromUser(userId, bookId);
    }
}
