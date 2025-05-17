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
package org.greencodeinitiative.tools.exporter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ExporterTest {

    @Test
    void notEnoughArguments() {
        try {
            Exporter.main(new String[0]);
            fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Usage: java -jar rule-exporter.jar <resourceArtifactFile> <outputJsonFile>");
        }
    }

    @Test
    void tooManyArguments() {
        try {
            Exporter.main(new String[]{"arg1", "arg2", "arg3"});
            fail("Exception not thrown");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Usage: java -jar rule-exporter.jar <resourceArtifactFile> <outputJsonFile>");
        }
    }

    @Test
    void cannotReadOrWriteRules() {
        try {
            Exporter.main(new String[]{"invalid-rules.jar", "output.json"});
            fail("Exception not thrown");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).contains("Cannot read or write rules");
        }
    }

}
