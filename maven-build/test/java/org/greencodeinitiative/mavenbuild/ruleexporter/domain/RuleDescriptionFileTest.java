package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RuleDescriptionFileTest {

    @Test
    @DisplayName("Should return an empty Optional when the path does not match the expected pattern")
    void createFromPathShouldReturnEmptyWhenPathDoesNotMatchPattern() {
        // Given
        String invalidPath = "invalid/path/to/file.html";

        // When
        Optional<RuleDescriptionFile> result = RuleDescriptionFile.createFromPath(invalidPath);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should create Rule from valid HTML description path")
    void createFromPathShouldReturnRuleWhenPathMatchesPattern() {
        // Given
        String validPath = "some/path/GCI123/java/GCI123.html";

        // When
        Optional<RuleDescriptionFile> result = RuleDescriptionFile.createFromPath(validPath);

        // Then
        assertThat(result).isPresent();
        RuleDescriptionFile rule = result.get();
        assertThat(rule.getRuleKey().toString()).isEqualTo("GCI123");
        assertThat(rule.getTechnology()).isEqualTo("java");
    }

    @Test
    @DisplayName("Should handle Windows-style paths")
    void createFromPathShouldHandleWindowsPaths() {
        // Given
        String windowsPath = "some\\path\\GCI456\\javascript\\GCI456.html";

        // When
        Optional<RuleDescriptionFile> result = RuleDescriptionFile.createFromPath(windowsPath);

        // Then
        assertThat(result).isPresent();
        RuleDescriptionFile rule = result.get();
        assertThat(rule.getRuleKey().toString()).isEqualTo("GCI456");
        assertThat(rule.getTechnology()).isEqualTo("javascript");
    }

    @Test
    @DisplayName("Should return correct values for all getters")
    void gettersShouldReturnCorrectValues() {
        // Given
        RuleDescriptionFile rule = new RuleDescriptionFile("some/path/GCI789/python/GCI789.html", "GCI789", "python");

        // When & Then
        assertThat(rule.getRuleKey().toString()).isEqualTo("GCI789");
        assertThat(rule.getTechnology()).isEqualTo("python");
    }

    @Test
    @DisplayName("Should extract rule key from various valid paths")
    void shouldExtractRuleKeyFromVariousPaths() {
        // Test different rule IDs
        assertThat(RuleDescriptionFile.createFromPath("path/GCI1/java/GCI1.html"))
                .isPresent()
                .get()
                .extracting(r -> r.getRuleKey().toString())
                .isEqualTo("GCI1");

        assertThat(RuleDescriptionFile.createFromPath("path/GCI9999/kotlin/GCI9999.html"))
                .isPresent()
                .get()
                .extracting(r -> r.getRuleKey().toString())
                .isEqualTo("GCI9999");
    }

    @Test
    @DisplayName("Should extract technology from various valid paths")
    void shouldExtractTechnologyFromVariousPaths() {
        // Test different technologies
        assertThat(RuleDescriptionFile.createFromPath("path/GCI1/java/GCI1.html"))
                .isPresent()
                .get()
                .extracting(RuleDescriptionFile::getTechnology)
                .isEqualTo("java");

        assertThat(RuleDescriptionFile.createFromPath("path/GCI2/javascript/GCI2.html"))
                .isPresent()
                .get()
                .extracting(RuleDescriptionFile::getTechnology)
                .isEqualTo("javascript");

        assertThat(RuleDescriptionFile.createFromPath("path/GCI3/python/GCI3.html"))
                .isPresent()
                .get()
                .extracting(RuleDescriptionFile::getTechnology)
                .isEqualTo("python");
    }

    @Test
    @DisplayName("Should return empty for path without GCI prefix")
    void shouldReturnEmptyForInvalidRuleKey() {
        // Given
        String pathWithoutGCI = "path/ABC123/java/ABC123.html";

        // When
        Optional<RuleDescriptionFile> result = RuleDescriptionFile.createFromPath(pathWithoutGCI);

        // Then
        assertThat(result).isEmpty();
    }
}
