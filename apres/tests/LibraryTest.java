import java.util.List;
import library.model.Book;
import library.model.DigitalBook;
import library.model.Lendable;
import library.service.Customer;
import library.service.Library;

public class LibraryTest {
    private static int passed;

    private static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
        passed++;
        System.out.println("OK - " + label);
    }

    private static void rejects(Runnable action, Class<? extends RuntimeException> type, String label) {
        try {
            action.run();
        } catch (RuntimeException e) {
            check(type.isInstance(e), label);
            return;
        }
        throw new AssertionError(label);
    }

    public static void main(String[] args) {
        Library library = new Library();
        Book book = new Book("Titre", "Auteur");
        DigitalBook digital = new DigitalBook("Numerique", "Auteur", 5);
        Customer alice = new Customer("Alice");
        Customer bob = new Customer("Bob");
        check(library.getAvailableItems().isEmpty(), "catalogue vide");
        library.addItem(book);
        library.addItem(digital);
        check(library.getAvailableItems().size() == 2, "deux livres disponibles");
        check(book.getTitle().equals("Titre") && book.getAuthor().equals("Auteur"), "metadonnees conservees");
        check(digital.getFileSizeMB() == 5, "taille du livre numerique");
        check(book.getDescription().equals("Titre by Auteur"), "description du livre");
        alice.checkOutItem(book);
        check(book.isBorrowed(), "emprunt effectif");
        check(!library.getAvailableItems().contains(book), "livre emprunte exclu des disponibilites");
        alice.checkOutItem(book);
        check(book.isBorrowed(), "demande repetee du meme client sans doublon");
        rejects(() -> bob.checkOutItem(book), IllegalStateException.class, "second client refuse");
        bob.returnItem(book);
        check(book.isBorrowed(), "un autre client ne peut pas rendre le livre");
        alice.returnItem(book);
        check(!book.isBorrowed() && library.getAvailableItems().contains(book), "retour et disponibilite retablie");
        alice.returnItem(book);
        check(!book.isBorrowed(), "retour repete du client sans effet");
        bob.checkOutItem(book);
        check(book.isBorrowed(), "second client autorise apres retour");
        bob.returnItem(book);
        rejects(() -> book.returnItem(), IllegalStateException.class, "retour direct sans emprunt refuse");
        digital.borrow();
        check(digital.isBorrowed() && !library.getAvailableItems().contains(digital), "emprunt du livre numerique");
        digital.returnItem();
        List<Lendable> snapshot = library.getItems();
        rejects(() -> snapshot.clear(), UnsupportedOperationException.class, "catalogue non modifiable");
        library.removeItem(book);
        check(library.getItems().size() == 1 && snapshot.size() == 2, "copie du catalogue independante");
        library.getAvailableItems().clear();
        check(library.getItems().size() == 1, "liste des disponibilites independante");
        Lendable magazine = new Lendable() {
            private boolean borrowed;
            public void borrow() { borrowed = true; }
            public void returnItem() { borrowed = false; }
            public boolean isBorrowed() { return borrowed; }
            public String getDescription() { return "Magazine"; }
        };
        library.addItem(magazine);
        check(library.getAvailableItems().contains(magazine), "type empruntable autre que Book accepte");
        magazine.borrow();
        check(!library.getAvailableItems().contains(magazine), "filtrage polymorphe sans conversion vers Book");
        System.out.println("RESULTAT : " + passed + " verifications reussies.");
    }
}
