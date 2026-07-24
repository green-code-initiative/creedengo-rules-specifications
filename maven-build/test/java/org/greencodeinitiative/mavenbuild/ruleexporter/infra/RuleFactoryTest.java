package org.greencodeinitiative.mavenbuild.ruleexporter.infra;

import org.greencodeinitiative.mavenbuild.ruleexporter.domain.RuleDescriptionFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RuleFactoryTest {

    public static final String GCI_123 = "GCI123";
    private RuleFactory ruleFactory;

    @BeforeEach
    void setUp() {
        ruleFactory = new RuleFactory();
    }

    @Test
    @DisplayName("Should create Rule when HTML description file exists")
    void shouldCreateRuleWhenHtmlDescriptionFileExists(@TempDir Path tempDir) throws Exception {
        // Given
        Path ruleDir = Files.createDirectories(tempDir.resolve(GCI_123));
        Path languageDir = Files.createDirectories(ruleDir.resolve("java"));
        Path htmlPath = Files.createFile(languageDir.resolve(GCI_123 + ".html"));
        Files.createFile(ruleDir.resolve("GCI123.json"));

        // When
        Optional<RuleDescriptionFile> result = ruleFactory.createFromHtmlDescription(htmlPath);

        // Then
        assertThat(result).isPresent();
        RuleDescriptionFile rule = result.get();
        assertThat(rule.getRuleKey().toString()).hasToString(GCI_123);
        assertThat(rule.getLanguage()).hasToString("java");
    }

    @Test
    @DisplayName("Should return empty when HTML file does not exist")
    void shouldReturnEmptyWhenHtmlFileDoesNotExist(@TempDir Path tempDir) throws Exception {
        // Given
        Path ruleDir = Files.createDirectories(tempDir.resolve(GCI_123));
        Path languageDir = Files.createDirectories(ruleDir.resolve("java"));
        Path htmlPath = languageDir.resolve(GCI_123 + ".html"); // Not created
        Files.createFile(ruleDir.resolve(GCI_123 + ".json"));

        // When
        Optional<RuleDescriptionFile> result = ruleFactory.createFromHtmlDescription(htmlPath);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when metadata file does not exist")
    void shouldReturnEmptyWhenMetadataFileDoesNotExist(@TempDir Path tempDir) throws Exception {
        // Given
        Path ruleDir = Files.createDirectories(tempDir.resolve(GCI_123));
        Path languageDir = Files.createDirectories(ruleDir.resolve("java"));
        Path htmlPath = Files.createFile(languageDir.resolve(GCI_123 + ".html"));
        // Metadata file not created

        // When
        Optional<RuleDescriptionFile> result = ruleFactory.createFromHtmlDescription(htmlPath);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when path does not match pattern")
    void shouldReturnEmptyWhenPathDoesNotMatchPattern(@TempDir Path tempDir) throws Exception {
        // Given
        Path invalidPath = Files.createFile(tempDir.resolve("invalid.html"));

        // When
        Optional<RuleDescriptionFile> result = ruleFactory.createFromHtmlDescription(invalidPath);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return correct metadata path")
    void shouldReturnCorrectMetadataPath(@TempDir Path tempDir) throws Exception {
        // Given
        Path ruleDir = Files.createDirectories(tempDir.resolve(GCI_123));
        Path languageDir = Files.createDirectories(ruleDir.resolve("java"));
        Path htmlPath = languageDir.resolve(GCI_123 + ".html");
        RuleDescriptionFile rule = new RuleDescriptionFile(htmlPath.toString(), GCI_123, "java");

        // When
        Path metadataPath = rule.getMetadataPath();

        // Then
        assertThat(metadataPath).isEqualTo(ruleDir.resolve(GCI_123 + ".json"));
    }

    @Test
    @DisplayName("Should return correct specific metadata path")
    void shouldReturnCorrectSpecificMetadataPath(@TempDir Path tempDir) throws Exception {
        // Given
        Path ruleDir = Files.createDirectories(tempDir.resolve(GCI_123));
        Path languageDir = Files.createDirectories(ruleDir.resolve("java"));
        Path htmlPath = languageDir.resolve(GCI_123 + ".html");
        RuleDescriptionFile rule = new RuleDescriptionFile(htmlPath.toString(), GCI_123, "java");

        // When
        Path specificMetadataPath = rule.getSpecificMetadataPath();

        // Then
        assertThat(specificMetadataPath).isEqualTo(languageDir.resolve(GCI_123 + ".json"));
    }

    @Test
    @DisplayName("Should return correct HTML description target path")
    void shouldReturnCorrectHtmlDescriptionTargetPath() {
        // Given
        Path targetDir = Path.of("target/classes");
        RuleDescriptionFile rule = new RuleDescriptionFile("some/path/" + GCI_123 + "/java/" + GCI_123 + ".html", GCI_123, "java");

        // When
        Path targetPath = rule.getHtmlDescriptionTargetPath(targetDir);

        // Then
        assertThat(targetPath).isEqualTo(Path.of("target/classes/java/" + GCI_123 + ".html"));
    }

    @Test
    @DisplayName("Should return correct metadata target path")
    void shouldReturnCorrectMetadataTargetPath() {
        // Given
        Path targetDir = Path.of("target/classes");
        RuleDescriptionFile rule = new RuleDescriptionFile("some/path/" + GCI_123 + "/java/" + GCI_123 + ".html", GCI_123, "java");

        // When
        Path targetPath = rule.getMetadataTargetPath(targetDir);

        // Then
        assertThat(targetPath).isEqualTo(Path.of("target/classes/java/" + GCI_123 + ".json"));
    }

    @Test
    @DisplayName("Should compute terms of the rule correctly")
    void shouldComputeTermsOfTheRuleCorrectly() {
        // Given
        String resourcePath = "maven-build/test/resources/rules-html/GCI36";
        RuleDescriptionFile rule = new RuleDescriptionFile(resourcePath + "/javascript/GCI36.html", "GCI36", "");

        // When
        String terms = ruleFactory.toRuleMetadata(rule, Path.of(resourcePath)).getTerms();

        // Then
        assertThat(terms).isEqualTo("actively appropriate areas articles audio audiofile autoplay autoplaying avoid battery browsers commence compliant conception configuring connectivity consume consumes consumption content contributing controls costs crucial designersethiques devices documentation download drain ecodesign ecoindex energy engaging environmental especially files guidelines however impact increased internet issue leading leads limited media might mitigate mobile noncompliant particularly plans playback posts potentially practices preload preloading prevent resources return segments settings sound still unnecessary usage users video videos without");
    }

    @Test
    @DisplayName("Should handle different languages")
    void shouldHandleDifferentLanguages(@TempDir Path tempDir) throws Exception {
        // Given
        Path ruleDir = Files.createDirectories(tempDir.resolve("GCI456"));
        Path languageDir = Files.createDirectories(ruleDir.resolve("javascript"));
        Path htmlPath = Files.createFile(languageDir.resolve("GCI456.html"));
        Files.createFile(ruleDir.resolve("GCI456.json"));

        // When
        Optional<RuleDescriptionFile> result = ruleFactory.createFromHtmlDescription(htmlPath);

        // Then
        assertThat(result).isPresent();
        RuleDescriptionFile rule = result.get();
        assertThat(rule.getRuleKey().toString()).hasToString("GCI456");
        assertThat(rule.getLanguage()).isEqualTo("javascript");
    }
}
