package library.model;

public interface Lendable {
    void borrow();
    void returnItem();
    boolean isBorrowed();
    String getDescription();
}
