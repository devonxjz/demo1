package dev.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Product product;
    private int quantity;

    public double getTotal() {
        if (product == null) {
            return 0.0;
        }
        return product.getPrice() * quantity;
    }

    public String getFormattedTotal() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(getTotal());
    }
}
