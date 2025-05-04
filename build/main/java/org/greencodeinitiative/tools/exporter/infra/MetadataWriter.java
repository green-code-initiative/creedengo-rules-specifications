package org.greencodeinitiative.tools.exporter.infra;

import jakarta.json.Json;
import jakarta.json.JsonMergePatch;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.JsonWriter;
import org.greencodeinitiative.tools.exporter.domain.Rule;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.Logger.Level.DEBUG;

public class MetadataWriter implements Runnable {
    private static final System.Logger LOGGER = System.getLogger("MetadataWriter");

    private final Path sourceDir;
    private final Path targetDir;
    private final Path indexFilepath;
    private final Map<String, String> specificationInfo;
    private final int minTermLength;

    public MetadataWriter(
            Path sourceDir,
            Path targetDir,
            Map<String, String> specificationInfo,
            Path indexFilepath,
            Integer minTermLength
    ) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        this.indexFilepath = indexFilepath;
        this.specificationInfo = specificationInfo;
        this.minTermLength = minTermLength;
    }

    @Override
    public void run() {
        var rulesMap = new TreeMap<String, JsonObjectBuilder>();

        getResourcesToCopy().forEach(rule -> {
            var rulesByLanguage = rulesMap.computeIfAbsent(rule.ruleKey(), k -> Json.createObjectBuilder());
            var resultMetadata = mergeOrCopyJsonMetadata(rule.metadata(), rule.specificMetadata(), rule.getMetadataTargetPath(targetDir));

            var htmlDescriptionRelativePath = this.indexFilepath.getParent()
                    .relativize(rule.getHtmlDescriptionTargetPath(targetDir))
                    .toString();
            var resultMetadataBuilder = Json.createObjectBuilder(resultMetadata);
            resultMetadataBuilder.add("key", rule.ruleKey());
            resultMetadataBuilder.add("language", rule.language());
            resultMetadataBuilder.add("severity", resultMetadata.getString("defaultSeverity").toUpperCase(Locale.ENGLISH));
            resultMetadataBuilder.add("htmlDescription", htmlDescriptionRelativePath);
            resultMetadataBuilder.add("terms", extractTermsFromHtmlFile(rule.htmlDescription()));

            rulesByLanguage.add(rule.language(), resultMetadataBuilder);
            copyFile(rule.htmlDescription(), rule.getHtmlDescriptionTargetPath(targetDir));
        });

        writeIndexFile(rulesMap);
    }

    private String extractTermsFromHtmlFile(Path htmlFile) {
        try {
            var textContent = Jsoup.parse(htmlFile).select("body").text();
            return Stream
                    .of(
                            removeDiacritics(textContent)
                                    .toLowerCase(Locale.ENGLISH)
                                    .replaceAll("[^a-zA-Z0-9]", " ")
                                    .trim()
                                    .split("[\\s\\n\\r]+")
                    )
                    .filter(term -> term.length() >= minTermLength)
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(" "));
        } catch (IOException e) {
            throw new ProcessException("Unable to parse HTML file: " + htmlFile, e);
        }
    }

    private String removeDiacritics(String text) {
        Normalizer.normalize(text, Normalizer.Form.NFKD);
        return text.replaceAll("[^\\p{ASCII}]", "").replaceAll("\\p{M}", "");
    }

    private void writeIndexFile(TreeMap<String, JsonObjectBuilder> rulesMap) {
        if (indexFilepath == null) {
            return;
        }
        var rules = Json.createObjectBuilder();
        rulesMap.forEach(rules::add);
        var result = Json.createObjectBuilder();

        var specification = Json.createObjectBuilder();
        this.specificationInfo.forEach(specification::add);
        result.add("specification", specification);

        result.add("rules", rules);

        try (var resultJsonWriter = Json.createWriter(Files.newBufferedWriter(indexFilepath))) {
            resultJsonWriter.write(result.build());
        } catch (IOException e) {
            throw new ProcessException("cannot write file: " + indexFilepath, e);
        }
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

    private JsonObject mergeOrCopyJsonMetadata(Path source, Path merge, Path target) {
        try {
            Files.createDirectories(target.getParent());
        } catch (IOException e) {
            throw new ProcessException("cannot create directory: " + target.getParent(), e);
        }
        if (Files.isRegularFile(merge)) {
            return mergeJsonFile(source, merge, target).asJsonObject();
        } else {
            copyFile(source, target);
            try (JsonReader targetJsonReader = Json.createReader(Files.newBufferedReader(target))) {
                return targetJsonReader.readObject();
            } catch (IOException e) {
                throw new ProcessException("cannot process source: " + source, e);
            }
        }
    }

    private JsonValue mergeJsonFile(Path source, Path merge, Path target) {
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
            return result;
        } catch (IOException e) {
            throw new ProcessException("cannot process source: " + source, e);
        }
    }

    private void copyFile(Path source, Path target) {
        try {
            LOGGER.log(DEBUG, "Copy: {0} -> {1}", source, target);
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ProcessException("unable to copy '" + source + "' to '" + target + "'", e);
        }
    }
}
