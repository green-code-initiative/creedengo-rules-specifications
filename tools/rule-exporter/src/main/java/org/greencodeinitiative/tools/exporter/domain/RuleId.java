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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleId implements Comparable<RuleId> {

    private static final Pattern PATTERN = Pattern.compile("^GCI(\\d+)$");

    private final String naturalId;

    private final long id;

    public RuleId(String naturalId) {
        Matcher matcher = PATTERN.matcher(naturalId);
        if (matcher.find()) {
            this.naturalId = naturalId;
            this.id = Long.parseLong(matcher.group(1));
        } else {
            throw new IllegalArgumentException("Invalid rule id: " + naturalId);
        }
    }

    @Override
    public String toString() {
        return this.naturalId;
    }

    @Override
    public int compareTo(RuleId o) {
        return Long.compare(this.id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RuleId ruleId = (RuleId) o;
        return id == ruleId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
