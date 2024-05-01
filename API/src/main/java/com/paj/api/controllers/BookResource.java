package com.paj.api.controllers;

import com.paj.api.entities.BookEntity;
import com.paj.api.entities.UserEntity;
import com.paj.api.services.BookService;

import com.paj.api.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/books")
public class BookResource {
    @EJB
    private BookService bookService;

    @EJB
    private UserService userService;

    @Inject
    SecurityContext securityContext;

    @GET
    public List<BookEntity> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GET
    @Path("/{id}")
    public BookEntity getBookById(@PathParam("id") int id) {
        return bookService.getBookById(id);
    }

    @GET
    @Path("/mine")
    @RolesAllowed("User")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BookEntity> getBooksByUserId() {
        // Get the userId of currently logged-in user, then use it to get the books
        UserEntity user = userService.getUserByEmail(securityContext.getCallerPrincipal().getName());
        return bookService.getUserBooks(user.getUserId());
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
