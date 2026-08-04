package org.greencodeinitiative.bench.lazy;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ORDER_ITEMS")
public class OrderItem {

    @Id
    private Long id;

    private String label;

    private int quantity;

    // Kept LAZY on both sides: the rule under test targets collection associations only,
    // so the owning side must not introduce a difference between the two variants.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    protected OrderItem() {
        // required by JPA
    }

    public OrderItem(Long id, String label, int quantity, Order order) {
        this.id = id;
        this.label = label;
        this.quantity = quantity;
        this.order = order;
    }

    public Long getId() {
        return id;
    }
}
