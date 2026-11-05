package library.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import library.model.Lendable;

public class Library {
    private final List<Lendable> items = new ArrayList<>();

    public void addItem(Lendable item) { items.add(item); }
    public void removeItem(Lendable item) { items.remove(item); }

    public List<Lendable> getAvailableItems() {
        List<Lendable> availableItems = new ArrayList<>();
        for (Lendable item : items) {
            if (!item.isBorrowed()) {
                availableItems.add(item);
            }
        }
        return availableItems;
    }

    // L'appelant peut consulter le catalogue sans modifier la collection interne.
    public List<Lendable> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }
}
