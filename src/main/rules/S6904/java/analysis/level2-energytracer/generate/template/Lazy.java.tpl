import java.util.HashSet;
import java.util.Set;

/**
 * FetchType LAZY - variant WITHOUT the code smell: the child collection is fetched lazily.
 *
 * <p>Strictly identical to the noncompliant variant except for the body of
 * {@code loadOrder}: no call to {@code hydrateItems}. The empty {@code HashSet}
 * stands for the lazy collection wrapper a real provider allocates, so the
 * comparison stays conservative (the compliant variant is not free).
 *
 * <p>See README.md for what this does and does not measure.
 */
public class __CLASS__ {

    /** Number of child rows per parent: the collection size under test. */
    private static final int CHILD_COUNT = __CHILD_COUNT__;

    /** Parent reads performed by one JVM run. Must be identical to the eager variant. */
    private static final int PARENT_READS = __PARENT_READS__;

    /** Distinct parent rows in the simulated table. */
    private static final int PARENT_ROWS = 200;

    private static final int CHILD_FIELDS = 3;

    /** Simulated persistence context. Keeps hydrated entities reachable, as a real
     *  provider does - and, incidentally, prevents the JIT from eliding the work. */
    private static final int CONTEXT_SIZE = 1024;

    /** Simulated wire buffer for parent rows: id, code. */
    private static final long[] PARENT_WIRE = new long[PARENT_ROWS * 2];

    /** Simulated wire buffer for child rows: id, parentId, quantity.
     *  Allocated in both variants so the two share the same memory footprint. */
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
        // LAZY: only the collection wrapper is allocated. Rows are never fetched
        // because the application never touches the collection.
        Set<OrderItem> items = new HashSet<>();
        return new Order(id, reference, items);
    }
}
