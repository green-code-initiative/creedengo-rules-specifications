package org.greencodeinitiative.mavenbuild.ruleexporter.infra;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleDescriptionFile;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleMetadata;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleSeverity;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleStatus;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.empty;

public class RuleFactory {

    public Optional<RuleDescriptionFile> createFromHtmlDescription(Path htmlDescriptionPath) {
        Optional<RuleDescriptionFile> ruleOpt = RuleDescriptionFile.createFromPath(htmlDescriptionPath.toString());

        if (ruleOpt.isEmpty()) {
            return empty();
        }

        RuleDescriptionFile rule = ruleOpt.get();

        if (!Files.isRegularFile(rule.getPath()) || !Files.isRegularFile(rule.getMetadataPath())) {
            return empty();
        }

        return ruleOpt;
    }

    public RuleMetadata toRuleMetadata(RuleDescriptionFile ruleFile, Path targetDir) {
        System.out.println("Reading metadata file: " + ruleFile.getMetadataTargetPath(targetDir));
        try (JsonReader reader = Json.createReader(Files.newBufferedReader(ruleFile.getMetadataTargetPath(targetDir)))) {
            JsonObject jsonObject = reader.readObject();

            RuleSeverity severity = RuleSeverity.valueOf(jsonObject.getString("defaultSeverity").toUpperCase());
            RuleStatus status = RuleStatus.valueOf(jsonObject.getString("status").toUpperCase());

            return new RuleMetadata.Builder()
                    .key(ruleFile.getRuleKey())
                    .language(ruleFile.getLanguage())
                    .title(jsonObject.getString("title"))
                    .terms(extractTermsFromHtmlFile(ruleFile.getPath()))
                    .severity(severity)
                    .status(status)
                    .build();
        } catch (IOException cause) {
            throw new ProcessException("Cannot read metadata file", cause);
        }
    }

    private String extractTermsFromHtmlFile(Path htmlFile) {
        try {
            var textContent = Jsoup.parse(htmlFile).select("body").text();
            return Stream
                    .of(
                            Normalizer
                                    .normalize(textContent, Normalizer.Form.NFKD)
                                    .replaceAll("[^\\p{ASCII}]", "")
                                    .replaceAll("\\p{M}", "")
                                    .toLowerCase(Locale.ENGLISH)
                                    .replaceAll("[^a-zA-Z]", " ")
                                    .trim()
                                    .split("\\s+")
                    )
                    .filter(term -> term.length() >= 5)
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(" "));
        } catch (IOException e) {
            throw new ProcessException("Unable to parse HTML file: " + htmlFile, e);
        }
    }
}
