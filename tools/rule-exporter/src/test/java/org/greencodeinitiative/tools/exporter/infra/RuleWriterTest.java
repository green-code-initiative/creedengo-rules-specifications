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

import org.greencodeinitiative.tools.exporter.domain.RuleMetadata;
import org.greencodeinitiative.tools.exporter.domain.RuleSeverity;
import org.greencodeinitiative.tools.exporter.domain.RuleStatus;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleWriterTest {

    private static final Path TARGET_PATH = Paths.get("target/rules.json");

    @Test
    void writeRules() throws IOException {
        Collection<RuleMetadata> rules = new ArrayList<>();

        RuleMetadata rule1 = new RuleMetadata.Builder()
                .id("GCI1")
                .title("Respect the planet")
                .technology("java")
                .severity(RuleSeverity.CRITICAL)
                .status(RuleStatus.READY)
                .build();

        RuleMetadata rule2 = new RuleMetadata.Builder()
                .id("GCI2")
                .title("Recycle whenever possible")
                .technology("javascript")
                .severity(RuleSeverity.MAJOR)
                .status(RuleStatus.READY)
                .build();

        rules.add(rule1);
        rules.add(rule2);

        RuleWriter ruleWriter = new RuleWriter(TARGET_PATH.toString());
        ruleWriter.writeRules(rules);

        InputStream expectedStream = getClass().getResourceAsStream("/expected-rules.json");
        assertThat(expectedStream).isNotNull();

        JsonObject expectedObject = Json.createReader(expectedStream).readObject();
        JsonObject actualObject = Json.createReader(Files.newBufferedReader(TARGET_PATH)).readObject();

        assertThat(actualObject).isEqualTo(expectedObject);
    }

}
