package org.greencodeinitiative.mavenbuild.ruleexporter.util;

import jakarta.json.*;
import org.greencodeinitiative.mavenbuild.ruleexporter.infra.ProcessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static java.lang.System.Logger.Level.DEBUG;

public class FileHelper {

    private static final System.Logger LOGGER = System.getLogger("FileHelper");

    private FileHelper() {
        // utility class
    }

    public static void mergeOrCopyJsonMetadata(Path source, Path merge, Path target) {
        try {
            Files.createDirectories(target.getParent());
        } catch (IOException e) {
            throw new ProcessException(e);
        }
        if (Files.isRegularFile(merge)) {
            mergeJsonFile(source, merge, target);
        } else {
            copyFile(source, target);
        }
    }

    public static void mergeJsonFile(Path source, Path merge, Path target) {
        LOGGER.log(DEBUG, "Merge: {0} and {1} -> {2}", source, merge, target);

        try (
                JsonReader sourceJsonReader = Json.createReader(Files.newBufferedReader(source));
                JsonReader mergeJsonReader = Json.createReader(Files.newBufferedReader(merge));
                JsonWriter resultJsonWriter = Json.createWriter(Files.newBufferedWriter(target))
        ) {
            Files.createDirectories(target.getParent());

            JsonObject sourceJson = sourceJsonReader.readObject();
            JsonObject mergeJson = mergeJsonReader.readObject();

            JsonMergePatch mergePatch = Json.createMergePatch(mergeJson);
            JsonValue result = mergePatch.apply(sourceJson);

            resultJsonWriter.write(result);
        } catch (IOException e) {
            throw new ProcessException("cannot process source " + source, e);
        }
    }

    public static void copyFile(Path source, Path target) {
        try {
            LOGGER.log(DEBUG, "Copy: {0} -> {1}", source, target);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ProcessException("unable to copy '" + source + "' to '" + target + "'", e);
        }
    }
}
