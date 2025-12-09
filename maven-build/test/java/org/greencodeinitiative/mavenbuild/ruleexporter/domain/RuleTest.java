package org.greencodeinitiative.mavenbuild.ruleexporter.domain;

import org.assertj.core.api.AbstractObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RuleTest {
	private final Path htmlDescription = Path.of("some/path/file.html");
	private final Path metadata = Path.of("some/path/metadata.json");
	private final Path specificMetadata = Path.of("some/path/specificMetadata.json");
	private final Path targetDir = Path.of("target");

	@Test
	@DisplayName("Should return an empty Optional when the path does not match the expected pattern")
	void createFromHtmlDescriptionShouldReturnEmptyWhenPathDoesNotMatchPattern() {
		// Given
		Path invalidPath = Path.of("invalid/path/to/file.html");

		// When
		Optional<Rule> result = Rule.createFromHtmlDescription(invalidPath);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should return correct values for all getters")
	void gettersShouldReturnCorrectValues() {
		// Given
		Rule rule = new Rule("java", htmlDescription, metadata, specificMetadata);

		// When & Then
		assertThat(rule.language()).isEqualTo("java");
		assertThat(rule.htmlDescription()).isEqualTo(htmlDescription);
		assertThat(rule.metadata()).isEqualTo(metadata);
		assertThat(rule.specificMetadata()).isEqualTo(specificMetadata);
	}

	@Test
	@DisplayName("Should return the correct target path for the HTML description")
	void getHtmlDescriptionTargetPathShouldReturnCorrectPath() {
		// Given
		Rule rule = new Rule("java", htmlDescription, metadata, specificMetadata);
		Path expectedPath = targetDir.resolve("java").resolve("file.html");

		// When
		Path actualPath = rule.getHtmlDescriptionTargetPath(targetDir);

		// Then
		assertThat(actualPath).isEqualTo(expectedPath);
	}

	@Test
	@DisplayName("Should return the correct target path for the metadata file")
	void getMetadataTargetPathShouldReturnCorrectPath() {
		// Given
		Rule rule = new Rule("java", htmlDescription, metadata, specificMetadata);
		Path expectedPath = targetDir.resolve("java").resolve("metadata.json");

		// When
		Path actualPath = rule.getMetadataTargetPath(targetDir);

		// Then
		assertThat(actualPath).isEqualTo(expectedPath);
	}

	@Test
	@DisplayName("Should return empty Optional if htmlDescription file does not exist")
	void createFromHtmlDescriptionShouldReturnEmptyIfHtmlFileMissing() {
		// Given
		Path html = Path.of("/GCI123/java/GCI123.html");

		// When
		Optional<Rule> result = Rule.createFromHtmlDescription(html);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should return empty Optional if metadata file does not exist")
	void createFromHtmlDescriptionShouldReturnEmptyIfMetadataFileMissing(@TempDir Path tempDir) throws Exception {
		// Given
		Path htmlPath = tempDir.resolve("GCI123/java/GCI123.html");
		Files.createDirectories(htmlPath.getParent());
		Files.createFile(htmlPath);

		// When
		Optional<Rule> result = Rule.createFromHtmlDescription(htmlPath);

		// Then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("Should create Rule when files and pattern are valid")
	void createFromHtmlDescriptionShouldReturnRuleWhenFilesExist(@TempDir Path tempDir) throws Exception {
		// Given
		Path ruleDir = Files.createDirectories(tempDir.resolve("GCI123"));
		Path languageRuleDir = Files.createDirectories(ruleDir.resolve("java"));

		Path htmlDescriptionPath = Files.createFile(languageRuleDir.resolve("GCI123.html"));
		Path metadataPath = Files.createFile(ruleDir.resolve("GCI123.json"));

		// When
		Optional<Rule> result = Rule.createFromHtmlDescription(htmlDescriptionPath);

		// Then
		AbstractObjectAssert<?, Rule> assertThatRule = assertThat(result).isPresent().get();
		assertThatRule.extracting(Rule::language).isEqualTo("java");
		assertThatRule.extracting(Rule::htmlDescription).isEqualTo(htmlDescriptionPath);
		assertThatRule.extracting(Rule::metadata).isEqualTo(metadataPath);
		assertThatRule.extracting(Rule::specificMetadata).isEqualTo(languageRuleDir.resolve("GCI123.json"));
	}
}
