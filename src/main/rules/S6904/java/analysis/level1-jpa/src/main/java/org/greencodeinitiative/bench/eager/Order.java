package org.greencodeinitiative.bench.eager;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * NONCOMPLIANT mapping: the collection is fetched eagerly.
 *
 * <p>Strictly identical to {@code org.greencodeinitiative.bench.lazy.Order} except for the
 * {@code fetch} attribute below. Both are mapped onto the same tables and are loaded through
 * two separate SessionFactories.
 */
@Entity
@Table(name = "ORDERS")
public class Order {

    @Id
    private Long id;

    private String reference;

    // The one line under test.
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<OrderItem> items = new HashSet<>();

    protected Order() {
        // required by JPA
    }

    public Order(Long id, String reference) {
        this.id = id;
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public Set<OrderItem> getItems() {
        return items;
    }
}
