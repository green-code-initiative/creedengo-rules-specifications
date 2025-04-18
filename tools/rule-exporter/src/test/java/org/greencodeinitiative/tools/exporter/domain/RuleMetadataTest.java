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
package org.greencodeinitiative.tools.exporter.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleMetadataTest {

    private RuleMetadata ruleMetadata;

    @BeforeEach
    void setUp() {
        ruleMetadata = new RuleMetadata.Builder()
                .id("GCI10")
                .technology("java")
                .title("title")
                .severity(RuleSeverity.INFO)
                .status(RuleStatus.READY)
                .build();
    }

    @Test
    void builder() {
        assertThat(ruleMetadata.getId()).isEqualTo(new RuleId("GCI10"));
        assertThat(ruleMetadata.getTechnology()).isEqualTo("java");
        assertThat(ruleMetadata.getTitle()).isEqualTo("title");
        assertThat(ruleMetadata.getSeverity()).isEqualTo(RuleSeverity.INFO);
        assertThat(ruleMetadata.getStatus()).isEqualTo(RuleStatus.READY);
    }

    @Test
    void asString() {
        assertThat(ruleMetadata.toString()).isEqualTo(
                "RuleMetadata{id='GCI10', language='java', title='title', severity=INFO, status=READY}");
    }

}
