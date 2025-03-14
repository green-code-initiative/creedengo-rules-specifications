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
package org.greencodeinitiative.tools.exporter.domain;

public class RuleMetadata {

    private final RuleId id;
    private final String technology;
    private final String title;
    private final RuleSeverity severity;
    private final RuleStatus status;

    private RuleMetadata(String id, String technology, String title, RuleSeverity severity, RuleStatus status) {
        this.id = new RuleId(id);
        this.technology = technology;
        this.title = title;
        this.severity = severity;
        this.status = status;
    }

    public RuleId getId() {
        return this.id;
    }

    public String getTechnology() {
        return this.technology;
    }

    public String getTitle() {
        return this.title;
    }

    public RuleSeverity getSeverity() {
        return this.severity;
    }

    public RuleStatus getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "RuleMetadata{" +
                "id='" + id + '\'' +
                ", language='" + technology + '\'' +
                ", title='" + title + '\'' +
                ", severity=" + severity +
                ", status=" + status +
                '}';
    }

    public static class Builder {

        private String id;
        private String technology;
        private String title;
        private RuleSeverity severity;
        private RuleStatus status;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder technology(String technology) {
            this.technology = technology;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder severity(RuleSeverity severity) {
            this.severity = severity;
            return this;
        }

        public Builder status(RuleStatus status) {
            this.status = status;
            return this;
        }

        public RuleMetadata build() {
            return new RuleMetadata(id, technology, title, severity, status);
        }

    }

}
