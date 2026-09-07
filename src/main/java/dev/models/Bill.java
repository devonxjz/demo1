package dev.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", unique = true, nullable = false)
    private String billNumber;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @ToString.Exclude
    private Order order;

    @Column(name = "bill_date", nullable = false)
    @Builder.Default
    private LocalDateTime billDate = LocalDateTime.now();

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "payment_method", nullable = false)
    @Builder.Default
    private String paymentMethod = "Mock Payment";

    @Column(name = "payment_status", nullable = false)
    @Builder.Default
    private String paymentStatus = "PAID";

    public String getFormattedTotalAmount() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(totalAmount);
    }

    public String getFormattedBillDate() {
        if (billDate == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return billDate.format(formatter);
    }
}
