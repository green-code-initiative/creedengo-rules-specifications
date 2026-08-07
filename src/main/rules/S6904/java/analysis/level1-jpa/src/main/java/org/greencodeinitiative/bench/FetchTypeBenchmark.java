package org.greencodeinitiative.bench;

import com.sun.management.ThreadMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.ToLongFunction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;

/**
 * FetchType LAZY - level 1 benchmark: measures the cost of {@code FetchType.EAGER} versus
 * {@code FetchType.LAZY} on a JPA collection association, on a real Hibernate + H2 stack,
 * for the case the rule targets: the application loads the parent and never touches
 * the collection.
 *
 * <p>What is measured, per parent read:
 * <ul>
 *   <li>JDBC statements prepared</li>
 *   <li>entities hydrated and collections fetched (Hibernate statistics)</li>
 *   <li>wall time</li>
 *   <li>bytes allocated on the heap</li>
 * </ul>
 *
 * <p>These are functional proxies, not energy. Energy is measured separately at level 2
 * with EnergyTracer. Both are needed: this one shows how much useless work is done,
 * the other shows what that work costs in joules.
 *
 * <p>A fresh session is opened for every read, so the first-level cache never turns the
 * second read into a no-op. That mirrors one session per request in a real application.
 *
 * <p>Usage: {@code java -jar fetchtype-benchmark.jar [size,size,...] [parentRows]}
 */
public final class FetchTypeBenchmark {

    private static final int DEFAULT_PARENT_ROWS = 200;
    private static final double WARMUP_RATIO = 0.25;

    private FetchTypeBenchmark() {
    }

    public static void main(String[] args) {
        int[] sizes = args.length > 0 ? parseSizes(args[0]) : new int[] {10, 100, 1000};
        int parentRows = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PARENT_ROWS;

        System.out.println("FetchType LAZY on JPA collections - benchmark");
        System.out.println("java=" + System.getProperty("java.version")
                + " vm=" + System.getProperty("java.vm.name")
                + " os=" + System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        System.out.println("parent rows=" + parentRows);
        System.out.println();

        List<Result> results = new ArrayList<>();
        for (int size : sizes) {
            int reads = Math.max(100, 20_000 / size);
            results.add(run("EAGER", size, parentRows, reads,
                    org.greencodeinitiative.bench.eager.Order.class,
                    o -> o.getId() + o.getReference().length()));
            results.add(run("LAZY", size, parentRows, reads,
                    org.greencodeinitiative.bench.lazy.Order.class,
                    o -> o.getId() + o.getReference().length()));
        }

        printTable(results);
        printCsv(results);
    }

    private static <T> Result run(String strategy,
                                  int childCount,
                                  int parentRows,
                                  int reads,
                                  Class<T> orderType,
                                  ToLongFunction<T> probe) {

        String dbName = "fetchtype_" + strategy.toLowerCase() + "_" + childCount;
        Class<?> itemType = "EAGER".equals(strategy)
                ? org.greencodeinitiative.bench.eager.OrderItem.class
                : org.greencodeinitiative.bench.lazy.OrderItem.class;

        try (SessionFactory sf = sessionFactory(dbName, orderType, itemType)) {
            seed(sf, strategy, childCount, parentRows);

            int warmup = Math.max(10, (int) (reads * WARMUP_RATIO));
            readLoop(sf, orderType, probe, warmup, parentRows);

            System.gc();
            sleepQuietly(200);

            Statistics stats = sf.getStatistics();
            stats.clear();
            ThreadMXBean threads = (ThreadMXBean) ManagementFactory.getThreadMXBean();
            long allocBefore = threads.getCurrentThreadAllocatedBytes();
            long start = System.nanoTime();

            readLoop(sf, orderType, probe, reads, parentRows);

            long elapsedNanos = System.nanoTime() - start;
            long allocated = threads.getCurrentThreadAllocatedBytes() - allocBefore;

            return new Result(
                    strategy,
                    childCount,
                    reads,
                    stats.getPrepareStatementCount(),
                    stats.getEntityLoadCount(),
                    stats.getCollectionFetchCount(),
                    elapsedNanos,
                    allocated);
        }
    }

    private static <T> void readLoop(SessionFactory sf,
                                     Class<T> orderType,
                                     ToLongFunction<T> probe,
                                     int iterations,
                                     int parentRows) {
        long sink = 0;
        for (int i = 0; i < iterations; i++) {
            long id = (i % parentRows) + 1L;
            try (Session session = sf.openSession()) {
                T order = session.get(orderType, id);
                // Only the parent is used. The collection is deliberately never accessed.
                sink += probe.applyAsLong(order);
            }
        }
        if (sink == Long.MIN_VALUE) {
            throw new IllegalStateException();
        }
    }

    private static SessionFactory sessionFactory(String dbName, Class<?>... entities) {
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        cfg.setProperty("hibernate.connection.url",
                "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        cfg.setProperty("hibernate.connection.username", "sa");
        cfg.setProperty("hibernate.connection.password", "");
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.generate_statistics", "true");
        cfg.setProperty("hibernate.show_sql", "false");
        cfg.setProperty("hibernate.format_sql", "false");
        cfg.setProperty("hibernate.cache.use_second_level_cache", "false");
        cfg.setProperty("hibernate.cache.use_query_cache", "false");
        // Seeding only: keeps the fixture build from dominating the run. Not measured.
        cfg.setProperty("hibernate.jdbc.batch_size", "100");
        cfg.setProperty("hibernate.order_inserts", "true");
        for (Class<?> entity : entities) {
            cfg.addAnnotatedClass(entity);
        }
        return cfg.buildSessionFactory();
    }

    private static void seed(SessionFactory sf, String strategy, int childCount, int parentRows) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            long itemId = 1L;
            for (int p = 1; p <= parentRows; p++) {
                if ("EAGER".equals(strategy)) {
                    var order = new org.greencodeinitiative.bench.eager.Order((long) p, "ORD-" + p);
                    for (int c = 0; c < childCount; c++) {
                        order.getItems().add(new org.greencodeinitiative.bench.eager.OrderItem(
                                itemId, "ITEM-" + itemId, 1 + (int) (itemId % 7), order));
                        itemId++;
                    }
                    session.persist(order);
                } else {
                    var order = new org.greencodeinitiative.bench.lazy.Order((long) p, "ORD-" + p);
                    for (int c = 0; c < childCount; c++) {
                        order.getItems().add(new org.greencodeinitiative.bench.lazy.OrderItem(
                                itemId, "ITEM-" + itemId, 1 + (int) (itemId % 7), order));
                        itemId++;
                    }
                    session.persist(order);
                }
                if (p % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
            session.getTransaction().commit();
        }
    }

    private static void printTable(List<Result> results) {
        // Locale.ROOT throughout: printf's %f is locale-sensitive by default (a JVM
        // running under a French locale prints "1,81" instead of "1.81"), which silently
        // breaks anything downstream that parses this output as plain numbers.
        System.out.printf(Locale.ROOT, "%-7s %6s %7s %11s %11s %11s %12s %12s%n",
                "fetch", "N", "reads", "stmt/read", "ent/read", "coll/read", "us/read", "KiB/read");
        System.out.println("-".repeat(88));
        for (Result r : results) {
            System.out.printf(Locale.ROOT, "%-7s %6d %7d %11.2f %11.2f %11.2f %12.1f %12.1f%n",
                    r.strategy, r.childCount, r.reads,
                    r.statements / (double) r.reads,
                    r.entityLoads / (double) r.reads,
                    r.collectionFetches / (double) r.reads,
                    r.elapsedNanos / 1_000.0 / r.reads,
                    r.allocatedBytes / 1024.0 / r.reads);
        }
        System.out.println();

        System.out.println("Overhead of EAGER over LAZY, per parent read:");
        for (int i = 0; i + 1 < results.size(); i += 2) {
            Result eager = results.get(i);
            Result lazy = results.get(i + 1);
            System.out.printf(Locale.ROOT, "  N=%-5d time x%.2f   allocation x%.2f   rows hydrated +%d%n",
                    eager.childCount,
                    ratio(eager.elapsedNanos, lazy.elapsedNanos),
                    ratio(eager.allocatedBytes, lazy.allocatedBytes),
                    (eager.entityLoads - lazy.entityLoads) / eager.reads);
        }
        System.out.println();
    }

    private static void printCsv(List<Result> results) {
        System.out.println("--- CSV ---");
        System.out.println("strategy,child_count,reads,statements,entity_loads,"
                + "collection_fetches,elapsed_ns,allocated_bytes");
        for (Result r : results) {
            System.out.printf("%s,%d,%d,%d,%d,%d,%d,%d%n",
                    r.strategy, r.childCount, r.reads, r.statements, r.entityLoads,
                    r.collectionFetches, r.elapsedNanos, r.allocatedBytes);
        }
    }

    private static double ratio(long a, long b) {
        return b == 0 ? Double.NaN : a / (double) b;
    }

    private static int[] parseSizes(String arg) {
        String[] parts = arg.split(",");
        int[] sizes = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            sizes[i] = Integer.parseInt(parts[i].trim());
        }
        return sizes;
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private record Result(String strategy,
                          int childCount,
                          int reads,
                          long statements,
                          long entityLoads,
                          long collectionFetches,
                          long elapsedNanos,
                          long allocatedBytes) {
    }
}
