/*
 * Creedengo Rule Exporter - Export all rules to JSON files usable by the website
 * Copyright © 2024 Green Code Initiative (https://green-code-initiative.org)
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
import org.greencodeinitiative.tools.exporter.domain.RuleSeverity;
import org.greencodeinitiative.tools.exporter.domain.RuleStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class RuleReaderTest {

    @Test
    void validRules() throws IOException {
        RuleReader ruleReader = new RuleReader(getFilePath("rules.jar"));
        Collection<RuleMetadata> rules = ruleReader.readRules();

        // check rules count
        assertThat(rules).isNotNull().hasSize(10);

        // check a specific rule
        RuleMetadata ruleEC3 = rules.stream().filter(r -> r.getId().toString().equals("GCI3")).findFirst()
                .orElseGet(() -> fail("GCI3 rule not found"));

        assertThat(ruleEC3.getId()).isEqualTo(new RuleId("GCI3"));
        assertThat(ruleEC3.getTitle()).isEqualTo("Avoid getting the size of the collection in the loop");
        assertThat(ruleEC3.getTechnology()).isEqualTo("java");
        assertThat(ruleEC3.getSeverity()).isEqualTo(RuleSeverity.MINOR);
        assertThat(ruleEC3.getStatus()).isEqualTo(RuleStatus.READY);
    }

    @Test
    void invalidRules() throws IOException {
        RuleReader ruleReader = new RuleReader(getFilePath("invalid-rules.jar"));
        try {
            ruleReader.readRules();
            fail("Exception not thrown");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid file path: org/green-code-initiative/rules/invalid.json");
        }
    }

    private String getFilePath(String fileName) {
        return Objects.requireNonNull(getClass().getResource("/" + fileName)).getPath();
    }

}
