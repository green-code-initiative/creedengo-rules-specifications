package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

public class RuleMetadata {

    private final RuleKey key;
    private final String technology;
    private final String title;
    private final RuleSeverity severity;
    private final RuleStatus status;

    private RuleMetadata(RuleKey key, String technology, String title, RuleSeverity severity, RuleStatus status) {
        this.key = key;
        this.technology = technology;
        this.title = title;
        this.severity = severity;
        this.status = status;
    }

    public RuleKey getKey() {
        return this.key;
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
        return (
                "RuleMetadata{" +
                        "key='" +
                        key +
                        '\'' +
                        ", language='" +
                        technology +
                        '\'' +
                        ", title='" +
                        title +
                        '\'' +
                        ", severity=" +
                        severity +
                        ", status=" +
                        status +
                        '}'
        );
    }

    public static class Builder {

        private RuleKey key;
        private String technology;
        private String title;
        private RuleSeverity severity;
        private RuleStatus status;

        public Builder key(RuleKey key) {
            this.key = key;
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
            return new RuleMetadata(key, technology, title, severity, status);
        }
    }
}
