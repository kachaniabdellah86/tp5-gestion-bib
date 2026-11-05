package library.app;

import library.model.*;
import library.service.*;

public class LibraryApp {
    public static void main(String[] args) {
        Library library = new Library();

        Book book1 = new Book("The Catcher in the Rye", "J.D. Salinger");
        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee");
        DigitalBook book3 = new DigitalBook("Digital Fortress", "Dan Brown", 5);

        library.addItem(book1);
        library.addItem(book2);
        library.addItem(book3);

        System.out.println("List of Books in the Library:");
        library.listAllItems();

        // Méthode pour TP SonarQube (complexité)
        book1.complexMethodExample(10);
    }
}
