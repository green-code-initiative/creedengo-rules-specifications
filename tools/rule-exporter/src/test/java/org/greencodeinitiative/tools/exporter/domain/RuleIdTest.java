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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class RuleIdTest {

    @Test
    void valid() {
        RuleId rule1 = new RuleId("GCI1");
        RuleId rule956 = new RuleId("GCI957");

        assertThat(rule1.toString()).isEqualTo("GCI1");
        assertThat(rule956.toString()).isEqualTo("GCI957");
    }

    @Test
    void invalid() {
        try {
            new RuleId("GCI");
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid rule id: GCI");
        }

        try {
            new RuleId("INVALID");
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid rule id: INVALID");
        }
    }

    @Test
    void equals() {
        RuleId rule1 = new RuleId("GCI1");
        assertThat(rule1).isEqualTo(rule1);
        assertThat(rule1).isEqualTo(new RuleId("GCI1"));
        assertThat(rule1).isNotEqualTo(null);
        assertThat(rule1).isNotEqualTo(new RuleId("GCI956"));
    }

    @Test
    void computeHashCode() {
        assertThat(new RuleId("GCI1").hashCode()).isEqualTo(new RuleId("GCI1").hashCode());
    }

    @Test
    void comparison() {
        assertThat(new RuleId("GCI5").compareTo(new RuleId("GCI6"))).isLessThan(0);
        assertThat(new RuleId("GCI5").compareTo(new RuleId("GCI5"))).isZero();
        assertThat(new RuleId("GCI6").compareTo(new RuleId("GCI5"))).isGreaterThan(0);
    }

}
