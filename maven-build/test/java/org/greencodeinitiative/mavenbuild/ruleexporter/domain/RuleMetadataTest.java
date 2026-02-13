package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleMetadataTest {

    private RuleMetadata ruleMetadata;

    @BeforeEach
    void setUp() {
        ruleMetadata = new RuleMetadata.Builder()
                .key(new RuleKey("GCI10"))
                .technology("java")
                .title("title")
                .severity(RuleSeverity.INFO)
                .status(RuleStatus.READY)
                .build();
    }

    @Test
    void builder() {
        assertThat(ruleMetadata.getKey()).isEqualTo(new RuleKey("GCI10"));
        assertThat(ruleMetadata.getTechnology()).isEqualTo("java");
        assertThat(ruleMetadata.getTitle()).isEqualTo("title");
        assertThat(ruleMetadata.getSeverity()).isEqualTo(RuleSeverity.INFO);
        assertThat(ruleMetadata.getStatus()).isEqualTo(RuleStatus.READY);
    }

    @Test
    void asString() {
        assertThat(ruleMetadata.toString()).isEqualTo(
                "RuleMetadata{key='GCI10', language='java', title='title', severity=INFO, status=READY}"
        );
    }
}
