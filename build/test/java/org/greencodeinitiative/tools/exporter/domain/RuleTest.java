package org.greencodeinitiative.tools.exporter.domain;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleTest {
    @Test
    void createFromHtmlDescription_shouldReturnEmpty_whenPathDoesNotMatchPattern() {
        Path invalidPath = Path.of("invalid/path/to/file.html");
        Optional<Rule> result = Rule.createFromHtmlDescription(invalidPath);
        assertTrue(result.isEmpty(), "Expected empty Optional for invalid path");
    }

    @Test
    void getters_shouldReturnCorrectValues() {
        Path htmlDescription = Path.of("some/path/file.html");
        Path metadata = Path.of("some/path/metadata.json");
        Path specificMetadata = Path.of("some/path/specificMetadata.json");

        Rule rule = new Rule("java", htmlDescription, metadata, specificMetadata);

        assertEquals("java", rule.language());
        assertEquals(htmlDescription, rule.htmlDescription());
        assertEquals(metadata, rule.metadata());
        assertEquals(specificMetadata, rule.specificMetadata());
    }

    @Test
    void getHtmlDescriptionTargetPath_shouldReturnCorrectPath() {
        Path htmlDescription = Path.of("some/path/file.html");
        Path metadata = Path.of("some/path/metadata.json");
        Path specificMetadata = Path.of("some/path/specificMetadata.json");
        Path targetDir = Path.of("target");

        Rule rule = new Rule("java", htmlDescription, metadata, specificMetadata);
        Path expectedPath = targetDir.resolve("java").resolve("file.html");

        assertEquals(expectedPath, rule.getHtmlDescriptionTargetPath(targetDir));
    }

    @Test
    void getMetadataTargetPath_shouldReturnCorrectPath() {
        Path htmlDescription = Path.of("some/path/file.html");
        Path metadata = Path.of("some/path/metadata.json");
        Path specificMetadata = Path.of("some/path/specificMetadata.json");
        Path targetDir = Path.of("target");

        Rule rule = new Rule("java", htmlDescription, metadata, specificMetadata);
        Path expectedPath = targetDir.resolve("java").resolve("metadata.json");

        assertEquals(expectedPath, rule.getMetadataTargetPath(targetDir));
    }
}
