package library.model;

public class Book implements Lendable {
    private final String title;
    private final String author;
    private boolean borrowed;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    @Override
    public void borrow() {
        if (borrowed) {
            throw new IllegalStateException("This book is already borrowed.");
        }
        borrowed = true;
    }

    @Override
    public void returnItem() {
        if (!borrowed) {
            throw new IllegalStateException("This book was not borrowed.");
        }
        borrowed = false;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }

    @Override
    public boolean isBorrowed() { return borrowed; }

    @Override
    public String getDescription() {
        return title + " by " + author;
    }
}
