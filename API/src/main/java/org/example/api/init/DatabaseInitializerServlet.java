package org.example.api.init;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.example.api.entities.BookEntity;
import org.example.api.entities.RoleEntity;
import org.example.api.entities.UserEntity;
import org.example.api.services.BookService;
import org.example.api.services.RoleService;
import org.example.api.services.UserService;

import java.util.Calendar;
import java.util.Date;
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

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Insert initial data here
        RoleEntity adminRole = createRoleIfNotFound("Admin");
        RoleEntity userRole = createRoleIfNotFound("User");

        UserEntity raluca = createUserIfNotFound("raluca@yahoo.com", "123546", "Raluca", "Muntean", adminRole);
        UserEntity alexandra = createUserIfNotFound("alexandra@yahoo.com", "123546", "Alexandra", "Mocanu", adminRole);
        UserEntity andrei = createUserIfNotFound("andrei@yahoo.com", "123546", "Andrei", "Biro", userRole);
        UserEntity dragos = createUserIfNotFound("dragos@yahoo.com", "123546", "Dragos", "Costache", userRole);

        createBook("The Great Gatsby", "F. Scott Fitzgerald", "Great book!", 5, 255, 351, new GregorianCalendar(2020, Calendar.FEBRUARY, 11).getTime(), "drama", andrei);
        createBook("The Catcher in the Rye", "J.D. Salinger", "Boring plot..", 5, 255, 351, new GregorianCalendar(2023, Calendar.MARCH, 11).getTime(), "crime", dragos);

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

    private void createBook(String title, String author, String personalNotes, int rating, int currentPage, int totalPages, Date dateStarted, String genre, UserEntity user) {
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
            newUser.setHashedPassword(hashedPassword);
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
