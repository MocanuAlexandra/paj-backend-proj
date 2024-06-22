package com.paj.api.init;

import com.paj.api.entities.RoleEntity;
import com.paj.api.entities.BookEntity;
import com.paj.api.entities.UserEntity;
import com.paj.api.services.BookService;
import com.paj.api.services.RoleService;
import com.paj.api.services.UserService;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.identitystore.Pbkdf2PasswordHash;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@WebListener
public class DatabaseInitializerServlet implements ServletContextListener {
    @EJB
    private RoleService roleService;
    @EJB
    private UserService userService;

    @EJB
    private BookService bookService;

    @Inject
    private Pbkdf2PasswordHash hasher;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Insert initial data here
        RoleEntity userRole = createRoleIfNotFound("User");

        UserEntity andrei = createUserIfNotFound("andrei@yahoo.com", "123456", "Andrei", "Biro", userRole);
        UserEntity dragos = createUserIfNotFound("dragos@yahoo.com", "123456", "Dragos", "Costache", userRole);

        createBook("The Great Gatsby", "F. Scott Fitzgerald", "Great book!", 5, 255, 351, LocalDate.of(2023, 3, 11), "drama", andrei);
        createBook("The Catcher in the Rye", "J.D. Salinger", "Boring plot..", 5, 255, 351, LocalDate.of(2023, 3, 11), "crime", dragos);
        createBook("To Kill a Mockingbird", "Harper Lee", "A timeless classic.", 4, 315, 421, LocalDate.of(2019, 7, 6), "fiction", andrei);
        createBook("1984", "George Orwell", "A dystopian masterpiece.", 5, 321, 432, LocalDate.of(2018, 12, 3), "science fiction", dragos);
        createBook("Pride and Prejudice", "Jane Austen", "Captivating romance.", 4, 279, 389, LocalDate.of(2022, 8, 20), "romance", andrei);
        createBook("The Hobbit", "J.R.R. Tolkien", "An epic adventure.", 5, 310, 426, LocalDate.of(2021, 5, 15), "fantasy", dragos);
        createBook("Moby-Dick", "Herman Melville", "A gripping tale of obsession.", 4, 401, 512, LocalDate.of(2020, 1, 30), "adventure", dragos);
        createBook("The Lord of the Rings", "J.R.R. Tolkien", "An epic fantasy trilogy.", 5, 118, 352, LocalDate.of(1954, 7, 29), "fantasy", andrei);
        createBook("Harry Potter and the Philosopher's Stone", "J.K. Rowling", "The beginning of a magical journey.", 5, 223, 135, LocalDate.of(1997, 6, 26), "fantasy", dragos);
        createBook("The Da Vinci Code", "Dan Brown", "A thrilling mystery.", 4, 189, 281, LocalDate.of(2003, 3, 18), "mystery", dragos);
        createBook("The Alchemist", "Paulo Coelho", "A philosophical novel about following dreams.", 4, 197, 197, LocalDate.of(1988, 1, 1), "fiction", andrei);
        createBook("Gone with the Wind", "Margaret Mitchell", "A sweeping historical romance.", 4, 1037, 4009, LocalDate.of(1936, 6, 30), "historical fiction", dragos);
        createBook("The Hunger Games", "Suzanne Collins", "A dystopian tale of survival.", 5, 374, 374, LocalDate.of(2008, 9, 14), "science fiction", dragos);
        createBook("The Road", "Cormac McCarthy", "A post-apocalyptic journey of a father and son.", 4, 287, 287, LocalDate.of(2006, 9, 26), "dystopian", andrei);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private RoleEntity createRoleIfNotFound(String roleName) {
        List<RoleEntity> roles = roleService.getAllRoles();
        boolean found = roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
        if (!found) {
            RoleEntity newRole = new RoleEntity();
            newRole.setName(roleName);
            roleService.createRole(newRole);
            return newRole;
        }
        return null;
    }

    private void createBook(String title, String author, String personalNotes, int rating, int currentPage, int totalPages, LocalDate dateStarted, String genre, UserEntity user) {
        List<BookEntity> books = bookService.getAllBooks();
        boolean found = books.stream().anyMatch(book -> book.getTitle().equalsIgnoreCase(title));
        if (!found) {
            BookEntity newBook = new BookEntity();
            newBook.setTitle(title);
            newBook.setAuthor(author);
            newBook.setPersonalNotes(personalNotes);
            newBook.setRating(rating);
            newBook.setCurrentPage(currentPage);
            newBook.setTotalPages(totalPages);
            newBook.setDateStarted(dateStarted);
            newBook.setGenre(genre);
            newBook.setUser(user);
            bookService.createBook(newBook);
        }
    }

    private UserEntity createUserIfNotFound(String email, String hashedPassword, String firstName, String lastName, RoleEntity role) {
        List<UserEntity> users = userService.getAllUsers();
        boolean found = users.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
        if (!found) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(email);
            newUser.setHashedPassword(hasher.generate(hashedPassword.toCharArray()));
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setRole(role);
            userService.createUser(newUser);

            //add the user to the role entity users list
            role.getUsers().add(newUser);

            return newUser;
        }
        return null;
    }
}
