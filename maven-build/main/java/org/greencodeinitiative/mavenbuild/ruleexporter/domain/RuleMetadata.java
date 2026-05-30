package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

public class RuleMetadata {

    private final RuleKey key;
    private final String language;
    private final String title;
    private final RuleSeverity severity;
    private final RuleStatus status;

    private RuleMetadata(RuleKey key, String language, String title, RuleSeverity severity, RuleStatus status) {
        this.key = key;
        this.language = language;
        this.title = title;
        this.severity = severity;
        this.status = status;
    }

    public RuleKey getKey() {
        return this.key;
    }

    public String getLanguage() {
        return this.language;
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
                    language +
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
        private String language;
        private String title;
        private RuleSeverity severity;
        private RuleStatus status;

        public Builder key(RuleKey key) {
            this.key = key;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
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
            return new RuleMetadata(key, language, title, severity, status);
        }
    }
}
