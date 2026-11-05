package library.service;

import java.util.ArrayList;
import java.util.List;
import library.model.Lendable;

public class Customer {
    private final String name;
    private final List<Lendable> checkedOutItems;

    public Customer(String name) {
        this.name = name;
        checkedOutItems = new ArrayList<>();
    }

    public void checkOutItem(Lendable item) {
        if (!checkedOutItems.contains(item)) {
            // Un emprunt refuse ne doit pas etre enregistre chez le client.
            item.borrow();
            checkedOutItems.add(item);
        }
    }

    public void returnItem(Lendable item) {
        if (checkedOutItems.contains(item)) {
            item.returnItem();
            checkedOutItems.remove(item);
        }
    }
}
