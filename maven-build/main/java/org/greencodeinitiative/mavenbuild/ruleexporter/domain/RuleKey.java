package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleKey implements Comparable<RuleKey> {

    private static final Pattern PATTERN = Pattern.compile("^GCI(\\d+)$");

    private final String naturalId;

    private final long id;

    public RuleKey(String naturalId) {
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
    public int compareTo(RuleKey o) {
        return Long.compare(this.id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleKey ruleId = (RuleKey) o;
        return id == ruleId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
