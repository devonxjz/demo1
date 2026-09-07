package dev.models;

import lombok.Data;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<LineItem> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(LineItem item) {
        if (item == null || item.getProduct() == null) {
            return;
        }

        String code = item.getProduct().getCode();
        int quantity = item.getQuantity();

        for (LineItem lineItem : items) {
            if (lineItem.getProduct().getCode().equalsIgnoreCase(code)) {
                lineItem.setQuantity(lineItem.getQuantity() + quantity);
                return;
            }
        }
        items.add(item);
    }

    public void updateItem(String code, int quantity) {
        if (code == null) {
            return;
        }

        if (quantity <= 0) {
            removeItem(code);
            return;
        }

        for (LineItem lineItem : items) {
            if (lineItem.getProduct().getCode().equalsIgnoreCase(code)) {
                lineItem.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeItem(String code) {
        if (code == null) {
            return;
        }
        items.removeIf(lineItem -> lineItem.getProduct().getCode().equalsIgnoreCase(code));
    }

    public int getCount() {
        int count = 0;
        for (LineItem lineItem : items) {
            count += lineItem.getQuantity();
        }
        return count;
    }

    public double getTotalAmount() {
        double total = 0.0;
        for (LineItem lineItem : items) {
            total += lineItem.getTotal();
        }
        return total;
    }

    public String getFormattedTotalAmount() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(getTotalAmount());
    }

    public void clear() {
        items.clear();
    }
}
