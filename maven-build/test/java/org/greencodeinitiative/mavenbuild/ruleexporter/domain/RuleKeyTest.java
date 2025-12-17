package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class RuleKeyTest {

    @Test
    void valid() {
        RuleKey rule1 = new RuleKey("GCI1");
        RuleKey rule956 = new RuleKey("GCI957");

        assertThat(rule1.toString()).isEqualTo("GCI1");
        assertThat(rule956.toString()).isEqualTo("GCI957");
    }

    @Test
    void invalid() {
        try {
            new RuleKey("GCI");
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid rule id: GCI");
        }

        try {
            new RuleKey("INVALID");
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid rule id: INVALID");
        }
    }

    @Test
    void equals() {
        RuleKey rule1 = new RuleKey("GCI1");
        assertThat(rule1).isEqualTo(rule1);
        assertThat(rule1).isEqualTo(new RuleKey("GCI1"));
        assertThat(rule1).isNotEqualTo(null);
        assertThat(rule1).isNotEqualTo(new RuleKey("GCI956"));
    }

    @Test
    void computeHashCode() {
        assertThat(new RuleKey("GCI1").hashCode()).isEqualTo(
                new RuleKey("GCI1").hashCode()
        );
    }

    @Test
    void comparison() {
        assertThat(
                new RuleKey("GCI5").compareTo(new RuleKey("GCI6"))
        ).isLessThan(0);
        assertThat(new RuleKey("GCI5").compareTo(new RuleKey("GCI5"))).isZero();
        assertThat(
                new RuleKey("GCI6").compareTo(new RuleKey("GCI5"))
        ).isGreaterThan(0);
    }
}
