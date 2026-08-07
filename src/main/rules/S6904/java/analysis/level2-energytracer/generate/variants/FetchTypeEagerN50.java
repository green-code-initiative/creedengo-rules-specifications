import java.util.HashSet;
import java.util.Set;

/**
 * FetchType LAZY - variant WITH the code smell: the child collection is fetched eagerly.
 *
 * <p>Models the client side of a JPA read where a parent entity is loaded together with a
 * collection association mapped with {@code FetchType.EAGER}, while the application never
 * accesses that collection.
 *
 * <p>This file is measured by EnergyTracer, whose Java runner compiles with a bare
 * {@code javac <file>} and runs with {@code java -cp <tmpdir> <Class>}: no third-party
 * library is reachable, hence the hand-rolled row source instead of JDBC/Hibernate.
 * See README.md for what this does and does not measure.
 *
 * <p>The only difference with the compliant variant is the body of {@code loadOrder}.
 */
public class FetchTypeEagerN50 {

    /** Number of child rows per parent: the collection size under test. */
    private static final int CHILD_COUNT = 50;

    /** Parent reads performed by one JVM run. Calibrated so a run lasts ~300-500 ms,
     *  so that JVM startup (~100-400 ms, once per iteration) does not drown the signal. */
    private static final int PARENT_READS = 100000;

    /** Distinct parent rows in the simulated table. */
    private static final int PARENT_ROWS = 200;

    private static final int CHILD_FIELDS = 3;

    /** Simulated persistence context. Keeps hydrated entities reachable, as a real
     *  provider does - and, incidentally, prevents the JIT from eliding the work. */
    private static final int CONTEXT_SIZE = 1024;

    /** Simulated wire buffer for parent rows: id, code. */
    private static final long[] PARENT_WIRE = new long[PARENT_ROWS * 2];

    /** Simulated wire buffer for child rows: id, parentId, quantity. */
    private static final long[] CHILD_WIRE = new long[PARENT_ROWS * CHILD_COUNT * CHILD_FIELDS];

    private static final Object[] CONTEXT = new Object[CONTEXT_SIZE];

    /** Consumes the computed value so it cannot be optimised away. */
    static volatile long sink;

    static {
        for (int i = 0; i < PARENT_ROWS; i++) {
            PARENT_WIRE[i * 2] = i;
            PARENT_WIRE[i * 2 + 1] = 1000L + i;
        }
        for (int i = 0; i < PARENT_ROWS * CHILD_COUNT; i++) {
            int o = i * CHILD_FIELDS;
            CHILD_WIRE[o] = i;
            CHILD_WIRE[o + 1] = i / CHILD_COUNT;
            CHILD_WIRE[o + 2] = 1 + (i % 7);
        }
    }

    static final class OrderItem {
        final long id;
        final long orderId;
        final long quantity;
        final String label;

        OrderItem(long id, long orderId, long quantity, String label) {
            this.id = id;
            this.orderId = orderId;
            this.quantity = quantity;
            this.label = label;
        }
    }

    static final class Order {
        final long id;
        final String reference;
        final Set<OrderItem> items;

        Order(long id, String reference, Set<OrderItem> items) {
            this.id = id;
            this.reference = reference;
            this.items = items;
        }
    }

    public static void main(String[] args) {
        long acc = 0;
        for (int r = 0; r < PARENT_READS; r++) {
            Order order = loadOrder(r % PARENT_ROWS);
            CONTEXT[r & (CONTEXT_SIZE - 1)] = order;
            // The application only needs the parent. The collection is never accessed.
            acc += order.id + order.reference.length();
        }
        sink = acc;
        if (sink == Long.MIN_VALUE) {
            throw new IllegalStateException();
        }
    }

    private static Order loadOrder(int p) {
        long id = PARENT_WIRE[p * 2];
        String reference = "ORD-" + PARENT_WIRE[p * 2 + 1];
        // EAGER: the provider hydrates the whole collection now, used or not.
        Set<OrderItem> items = hydrateItems(p);
        return new Order(id, reference, items);
    }

    private static Set<OrderItem> hydrateItems(int p) {
        Set<OrderItem> items = new HashSet<>(Math.max(16, CHILD_COUNT * 2));
        int base = p * CHILD_COUNT * CHILD_FIELDS;
        for (int i = 0; i < CHILD_COUNT; i++) {
            int o = base + i * CHILD_FIELDS;
            items.add(new OrderItem(
                    CHILD_WIRE[o],
                    CHILD_WIRE[o + 1],
                    CHILD_WIRE[o + 2],
                    "ITEM-" + CHILD_WIRE[o]));
        }
        return items;
    }
}
