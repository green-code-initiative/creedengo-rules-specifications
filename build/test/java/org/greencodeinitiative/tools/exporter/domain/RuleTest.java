package org.greencodeinitiative.tools.exporter.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleTest {
    private final Path htmlDescription = Path.of("some/path/file.html");
    private final Path metadata = Path.of("some/path/metadata.json");
    private final Path specificMetadata = Path.of("some/path/specificMetadata.json");
    private final Path targetDir = Path.of("target");

    @Test
    @DisplayName("Should return an empty Optional when the path does not match the expected pattern")
    void createFromHtmlDescriptionShouldReturnEmptyWhenPathDoesNotMatchPattern() {
        Path invalidPath = Path.of("invalid/path/to/file.html");
        Optional<Rule> result = Rule.createFromHtmlDescription(invalidPath);
        assertTrue(result.isEmpty(), "Expected empty Optional for invalid path");
    }

    @Test
    @DisplayName("Should return correct values for all getters")
    void gettersShouldReturnCorrectValues() {
        Rule rule = new Rule("GCI001", "java", htmlDescription, metadata, specificMetadata);

        assertEquals("java", rule.language());
        assertEquals(htmlDescription, rule.htmlDescription());
        assertEquals(metadata, rule.metadata());
        assertEquals(specificMetadata, rule.specificMetadata());
    }

    @Test
    @DisplayName("Should return the correct target path for the HTML description")
    void getHtmlDescriptionTargetPathShouldReturnCorrectPath() {
        Rule rule = new Rule("GCI002", "java", htmlDescription, metadata, specificMetadata);
        Path expectedPath = targetDir.resolve("java").resolve("file.html");

        assertEquals(expectedPath, rule.getHtmlDescriptionTargetPath(targetDir));
    }

    @Test
    @DisplayName("Should return the correct target path for the metadata file")
    void getMetadataTargetPathShouldReturnCorrectPath() {
        Rule rule = new Rule("GCI003", "java", htmlDescription, metadata, specificMetadata);
        Path expectedPath = targetDir.resolve("java").resolve("metadata.json");

        assertEquals(expectedPath, rule.getMetadataTargetPath(targetDir));
    }
}
