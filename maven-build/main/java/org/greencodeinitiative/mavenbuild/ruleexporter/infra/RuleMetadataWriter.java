package org.greencodeinitiative.mavenbuild.ruleexporter.infra;

import jakarta.json.*;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleKey;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuleMetadataWriter {

    private final String outputFilename;
    private final Map<String, String> languageLabels;

    public RuleMetadataWriter(String outputFilename) {
        this.outputFilename = outputFilename;
        this.languageLabels = ConfigLoader.getLanguageLabels();
    }

    public void writeRules(Collection<RuleMetadata> rules) throws IOException {
        Path path = new File(outputFilename).toPath();
        Files.createDirectories(path.getParent());
        try (JsonWriter resultJsonWriter = Json.createWriter(Files.newBufferedWriter(path))) {
            resultJsonWriter.writeObject(
                    Json.createObjectBuilder()
                            .add("items", buildItems(rules))
                            .add("meta", buildMeta(rules))
                            .add("build", buildBuildInfo())
                            .build()
            );
        }
    }

    private JsonArray buildItems(Collection<RuleMetadata> rules) {
        Map<RuleKey, List<RuleMetadata>> rulesById = rules
                .stream()
                .collect(Collectors.groupingBy(RuleMetadata::getKey));
        return Json.createArrayBuilder(
                rulesById
                        .entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> this.buildItem(e.getValue()))
                        .collect(Collectors.toList())
        ).build();
    }

    private JsonObject buildItem(Collection<RuleMetadata> rulesById) {
        RuleMetadata first = rulesById.iterator().next();

        JsonObjectBuilder languagesBuilder = Json.createObjectBuilder();
        rulesById.stream()
                .sorted(Comparator.comparing(RuleMetadata::getLanguage))
                .forEach(rule -> languagesBuilder.add(rule.getLanguage(), buildLanguageEntry(rule)));

        return Json.createObjectBuilder()
                .add("id", first.getKey().toString())
                .add("name", first.getTitle())
                .add("severity", first.getSeverity().toString())
                .add("languages", languagesBuilder.build())
                .add("terms", first.getTerms())
                .build();
    }

    private JsonObject buildLanguageEntry(RuleMetadata rule) {
        return Json.createObjectBuilder()
                .add("status", rule.getStatus().toString())
                .build();
    }

    private JsonObject buildMeta(Collection<RuleMetadata> rules) {
        String deploymentUrl = System.getenv("PAGES_DEPLOYMENT_URL");
        return Json.createObjectBuilder()
                .add("languages", extractAllPropertiesToMap(rules, rule -> Map.entry(
                        rule.getLanguage(),
                        languageLabels.getOrDefault(rule.getLanguage(), rule.getLanguage())
                )))
                .add("severities", extractAllProperties(rules, rule -> rule.getSeverity().toString()))
                .add("statuses", extractAllProperties(rules, rule -> rule.getStatus().toString()))
                .add("contentUrlTemplate",
                        deploymentUrl != null ? Json.createValue(deploymentUrl + "{language}/{id}.html")
                                : JsonValue.NULL)
                .build();
    }

    private JsonObject buildBuildInfo() {
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String gitRef = System.getenv("GITHUB_REF_NAME");
        return Json.createObjectBuilder()
                .add("datetime", timestamp)
                .add("gitRef", gitRef != null ? Json.createValue(gitRef) : JsonValue.NULL)
                .build();
    }

    private JsonArray extractAllProperties(Collection<RuleMetadata> rules, Function<RuleMetadata, String> mapper) {
        return Json.createArrayBuilder(
                rules.stream().map(mapper).distinct().sorted().collect(Collectors.toList())
        ).build();
    }

    private JsonObject extractAllPropertiesToMap(
            Collection<RuleMetadata> rules,
            Function<RuleMetadata, Map.Entry<String, String>> mapper
    ) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        rules.stream()
                .map(mapper)
                .distinct()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> builder.add(entry.getKey(), entry.getValue()));
        return builder.build();
    }
}
