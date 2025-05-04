package org.greencodeinitiative.tools.exporter.infra;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import org.greencodeinitiative.tools.exporter.domain.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.Logger.Level.DEBUG;

public class MetadataWriter implements Runnable {
    private static final System.Logger LOGGER = System.getLogger(MetadataWriter.class.getName());

    private final Path sourceDir;
    private final Path targetDir;

    public MetadataWriter(Path sourceDir, Path targetDir) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    @Override
    public void run() {
        getResourcesToCopy().forEach(rule -> {
            mergeOrCopyJsonMetadata(rule.metadata(), rule.specificMetadata(), rule.getMetadataTargetPath(targetDir));
            copyFile(rule.htmlDescription(), rule.getHtmlDescriptionTargetPath(targetDir));
        });
    }

    private List<Rule> getResourcesToCopy() {
        try (Stream<Path> stream = Files.walk(sourceDir)) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Rule::createFromHtmlDescription)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void mergeOrCopyJsonMetadata(Path source, Path merge, Path target) {
        try {
            Files.createDirectories(target.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (Files.isRegularFile(merge)) {
            mergeJsonFile(source, merge, target);
        } else {
            copyFile(source, target);
        }
    }

    private void mergeJsonFile(Path source, Path merge, Path target) {
        LOGGER.log(DEBUG, "Merge: {0} and {1} -> {2}", source, merge, target);

        try (
                JsonReader sourceJsonReader = Json.createReader(Files.newBufferedReader(source));
                JsonReader mergeJsonReader = Json.createReader(Files.newBufferedReader(merge));
                JsonWriter resultJsonWriter = Json.createWriter(Files.newBufferedWriter(target));
        ) {
            Files.createDirectories(target.getParent());

            JsonObject sourceJson = sourceJsonReader.readObject();
            JsonObject mergeJson = mergeJsonReader.readObject();

            JsonMergePatch mergePatch = Json.createMergePatch(mergeJson);
            JsonValue result = mergePatch.apply(sourceJson);

            resultJsonWriter.write(result);
        } catch (IOException e) {
            throw new RuntimeException("cannot process source " + source, e);
        }
    }

    private void copyFile(Path source, Path target) {
        try {
            LOGGER.log(DEBUG, "Copy: {0} -> {1}", source, target);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("unable to copy '" + source + "' to '" + target + "'", e);
        }
    }
}
