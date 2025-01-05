/*
 * ecoCode Rule Exporter - Export all rules to JSON files usable by the website
 * Copyright © 2024 Green Code Initiative (https://www.ecocode.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.greencodeinitiative.tools.exporter.infra;

import org.greencodeinitiative.tools.exporter.domain.RuleId;
import org.greencodeinitiative.tools.exporter.domain.RuleMetadata;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuleWriter {

    private final String outputFilename;

    public RuleWriter(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public void writeRules(Collection<RuleMetadata> rules) throws IOException {
        Path path = new File(outputFilename).toPath();
        try (JsonWriter resultJsonWriter = Json.createWriter(Files.newBufferedWriter(path))) {
            resultJsonWriter.writeObject(
                    Json.createObjectBuilder()
                            .add("items", buildItems(rules))
                            .add("meta", buildMeta(rules))
                            .build());
        }
    }

    private JsonArray buildItems(Collection<RuleMetadata> rules) {
        Map<RuleId, List<RuleMetadata>> rulesById = rules.stream().collect(Collectors.groupingBy(RuleMetadata::getId));
        return Json.createArrayBuilder(
                rulesById.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> this.buildItem(e.getValue()))
                        .collect(Collectors.toList()))
                .build();
    }

    private JsonObject buildItem(Collection<RuleMetadata> rulesById) {
        RuleMetadata first = rulesById.iterator().next();
        return Json.createObjectBuilder()
                .add("id", first.getId().toString())
                .add("name", first.getTitle())
                .add("severity", first.getSeverity().toString())
                .add("technologies", extractAllProperties(rulesById, RuleMetadata::getTechnology))
                .add("status", first.getStatus().toString())
                .build();
    }

    private JsonObject buildMeta(Collection<RuleMetadata> rules) {
        return Json.createObjectBuilder()
                .add("technologies", extractAllProperties(rules, RuleMetadata::getTechnology))
                .add("severities", extractAllProperties(rules, rule -> rule.getSeverity().toString()))
                .add("statuses", extractAllProperties(rules, rule -> rule.getStatus().toString()))
                .build();
    }

    private JsonArray extractAllProperties(Collection<RuleMetadata> rules, Function<RuleMetadata, String> mapper) {
        return Json.createArrayBuilder(
                rules.stream()
                        .map(mapper)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList()))
                .build();
    }

}
