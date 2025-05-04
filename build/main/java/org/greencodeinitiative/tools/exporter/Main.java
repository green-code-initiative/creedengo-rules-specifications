package org.greencodeinitiative.tools.exporter;

import org.greencodeinitiative.tools.exporter.infra.MetadataWriter;

import java.nio.file.Path;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public class Main implements Runnable {
    private final List<String> args;

    public Main(String[] args) {
        this.args = ofNullable(args).map(List::of).orElse(emptyList());
    }

    public static void main(String... args) {
        new Main(args).run();
    }

    @Override
    public void run() {
        new MetadataWriter(
                getPath(0, "sourceDir"),
                getPath(1, "targetDir")
        ).run();

    }

    private Path getPath(int index, String description) {
        if (args.size() <= index) {
            throw new IllegalArgumentException("argument " + (index + 1) + " is required: " + description);
        }
        return Path.of(args.get(index));
    }
}
