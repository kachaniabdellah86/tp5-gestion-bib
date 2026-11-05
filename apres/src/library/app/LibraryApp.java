package library.app;

import library.model.Book;
import library.model.DigitalBook;
import library.model.Lendable;
import library.service.Library;

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
        for (Lendable item : library.getItems()) {
            System.out.println(item.getDescription());
        }
        printNumberExample(10);
    }

    // Exemple du TP conserve, mais retire du modele metier Book.
    private static void printNumberExample(int n) {
        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                System.out.println(i + " is even");
            } else if (i % 3 == 0) {
                System.out.println(i + " divisible by 3");
            } else {
                System.out.println(i + " something else");
            }
        }
    }
}
