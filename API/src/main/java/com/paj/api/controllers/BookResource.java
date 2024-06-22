package com.paj.api.controllers;

import com.paj.api.dto.BookDto;
import com.paj.api.entities.BookEntity;
import com.paj.api.entities.UserEntity;
import com.paj.api.services.BookService;

import com.paj.api.services.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.security.enterprise.SecurityContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @RolesAllowed({"User", "Guest"})
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

    @GET
    @Path("/guest")
    @RolesAllowed("Guest")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BookEntity> getBooksForGuest() {
        // For the guest user, return all books
        return bookService.getAllBooks();
    }

    @POST
    @Path("/create")
    @RolesAllowed("User")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createBook(BookDto bookDto) {
        var newBook = new BookEntity();

        newBook.setUser(userService.getUserByEmail(securityContext.getCallerPrincipal().getName()));
        newBook.setTitle(bookDto.getTitle());
        newBook.setAuthor(bookDto.getAuthor());
        newBook.setPersonalNotes(bookDto.getPersonalNotes());
        newBook.setRating(bookDto.getRating());
        newBook.setCurrentPage(bookDto.getCurrentPage());
        newBook.setTotalPages(bookDto.getTotalPages());
        newBook.setDateStarted(LocalDate.parse(bookDto.getDateStarted(), DateTimeFormatter.ISO_LOCAL_DATE));
        newBook.setGenre(bookDto.getGenre());

        BookEntity createdBook = bookService.createBook(newBook);

        BookDto bookData = new BookDto(
                String.valueOf(createdBook.getBookId()),
                String.valueOf(createdBook.getUser().getUserId()),
                createdBook.getTitle(),
                createdBook.getAuthor(),
                createdBook.getPersonalNotes(),
                createdBook.getRating(),
                createdBook.getCurrentPage(),
                createdBook.getTotalPages(),
                createdBook.getDateStarted().toString(),
                createdBook.getGenre()
        );

        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig())) {
            return jsonb.toJson(bookData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PUT
    @Path("/update")
    @RolesAllowed("User")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(BookDto bookDto) {
        try {
            var updatedBook = new BookEntity();
            updatedBook.setBookId(Integer.parseInt(bookDto.getBookId()));
            updatedBook.setUser(userService.getUserByEmail(securityContext.getCallerPrincipal().getName()));
            updatedBook.setTitle(bookDto.getTitle());
            updatedBook.setAuthor(bookDto.getAuthor());
            updatedBook.setPersonalNotes(bookDto.getPersonalNotes());
            updatedBook.setRating(bookDto.getRating());
            updatedBook.setCurrentPage(bookDto.getCurrentPage());
            updatedBook.setTotalPages(bookDto.getTotalPages());
            updatedBook.setDateStarted(LocalDate.parse(bookDto.getDateStarted(), DateTimeFormatter.ISO_LOCAL_DATE));
            updatedBook.setGenre(bookDto.getGenre());

            bookService.updateBook(updatedBook);

            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/delete/{bookId}")
    @RolesAllowed("User")
    public Response deleteBook(@PathParam("bookId") int bookId) {
        try {
            bookService.deleteBook(bookId);
            return Response.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
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
