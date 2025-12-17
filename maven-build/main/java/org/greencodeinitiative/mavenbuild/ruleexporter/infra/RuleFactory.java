package org.greencodeinitiative.mavenbuild.ruleexporter.infra;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleDescriptionFile;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleMetadata;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleSeverity;
import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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

    public RuleMetadata toRuleMetadata(RuleDescriptionFile ruleFile) {
        try (JsonReader reader = Json.createReader(Files.newBufferedReader(ruleFile.getMetadataPath()))) {
            JsonObject jsonObject = reader.readObject();

            RuleSeverity severity = RuleSeverity.valueOf(jsonObject.getString("defaultSeverity").toUpperCase());
            RuleStatus status = RuleStatus.valueOf(jsonObject.getString("status").toUpperCase());

            return new RuleMetadata.Builder()
                    .key(ruleFile.getRuleKey())
                    .technology(ruleFile.getTechnology())
                    .title(jsonObject.getString("title"))
                    .severity(severity)
                    .status(status)
                    .build();
        } catch (IOException cause) {
            throw new ProcessException("Cannot read metadata file", cause);
        }
    }
}
