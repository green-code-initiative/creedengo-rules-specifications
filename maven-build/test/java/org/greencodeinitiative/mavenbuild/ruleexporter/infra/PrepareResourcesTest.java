package org.greencodeinitiative.mavenbuild.ruleexporter.infra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class PrepareResourcesTest {
	private static final Path SOURCE_DIR = Paths.get("maven-build/test/resources");

	@TempDir
	private Path targetDir;
	private PrepareResources prepareResources;

	@BeforeEach
	void setUp() {
		prepareResources = new PrepareResources(SOURCE_DIR, targetDir);
	}

	@Test
	@DisplayName("Should copy files when no merge file is present")
	void shouldCopyFileWhenNoMergeFile() {
		// When
		prepareResources.run();

		// Then
		assertThat(targetDir.resolve("html/GCI36.json")).exists();
		assertThat(targetDir.resolve("html/GCI36.html")).exists();
		assertThat(targetDir.resolve("javascript/GCI36.json")).exists();
		assertThat(targetDir.resolve("javascript/GCI36.html")).exists();
	}

	@Test
	@DisplayName("Should throw exception when source directory is not found")
	void shouldThrowExceptionWhenSourceDirNotFound() {
		// Given
		Path invalid = Paths.get("not-existing-dir");
		PrepareResources pr = new PrepareResources(invalid, targetDir);

		// When
		Throwable thrown = catchThrowable(pr::run);

		// Then
		assertThat(thrown)
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("Unable to read file")
			.hasCauseInstanceOf(NoSuchFileException.class)
			.hasRootCauseMessage("not-existing-dir");
	}

	@Test
	@DisplayName("Should merge JSON files correctly")
	void shouldMergeJsonFilesCorrectly(@TempDir Path tempDir) throws IOException {
		// Given
		Path sourceJson = Files.createFile(tempDir.resolve("source.json"));
		Path mergeJson = Files.createFile(tempDir.resolve("merge.json"));
		Path targetJson = tempDir.resolve("result.json");

		String sourceContent = "{\n" +
			"\"key1\": \"value1\",\n" +
			"\"key2\": \"value2\"\n" +
			"}";
		String mergeContent = "{\n" +
			"\"key2\": \"newValue2\",\n" +
			"\"key3\": \"value3\"\n" +
			"}";

		Files.writeString(sourceJson, sourceContent);
		Files.writeString(mergeJson, mergeContent);

		// When
		prepareResources.mergeJsonFile(sourceJson, mergeJson, targetJson);

		// Then
		String expectedContent = "{\n" +
			"\"key1\": \"value1\",\n" +
			"\"key2\": \"newValue2\",\n" +
			"\"key3\": \"value3\"\n" +
			"}";
		assertThat(targetJson).exists();
		assertThat(Files.readString(targetJson)).isEqualToIgnoringWhitespace(expectedContent);
	}

	@Test
	@DisplayName("Should throw ProcessException when unable to create directories for merge or copy JSON metadata")
	void shouldThrowProcessExceptionWhenCannotCreateDirectoriesForMergeOrCopyJsonMetadata() {
		// Given
		PrepareResources pr = new PrepareResources(Path.of("/invalid/source"), Path.of("/invalid/target"));
		Path source = Path.of("/invalid/source.json");
		Path merge = Path.of("/invalid/merge.json");
		Path target = Path.of("/invalid/target.json");

		// When
		Throwable thrown = catchThrowable(() -> pr.mergeOrCopyJsonMetadata(source, merge, target));

		// Then
		assertThat(thrown)
			.isInstanceOf(ProcessException.class);
	}

	@Test
	@DisplayName("Should throw ProcessException when merging JSON file fails")
	void shouldThrowProcessExceptionWhenMergeJsonFileFails() {
		// Given
		PrepareResources pr = new PrepareResources(Path.of("."), Path.of("."));
		Path source = Path.of("notfound-source.json");
		Path merge = Path.of("notfound-merge.json");
		Path target = Path.of("notfound-target.json");

		// When
		Throwable thrown = catchThrowable(() -> pr.mergeJsonFile(source, merge, target));

		// Then
		assertThat(thrown)
			.isInstanceOf(ProcessException.class)
			.hasMessageContaining("cannot process source");
	}

	@Test
	@DisplayName("Should throw ProcessException when copying file fails")
	void shouldThrowProcessExceptionWhenCopyFileFails() {
		// Given
		PrepareResources pr = new PrepareResources(Path.of("."), Path.of("."));
		Path source = Path.of("notfound-source.txt");
		Path target = Path.of("notfound-target.txt");

		// When
		Throwable thrown = catchThrowable(() -> pr.copyFile(source, target));

		// Then
		assertThat(thrown)
			.isInstanceOf(ProcessException.class)
			.hasMessageContaining("unable to copy");
	}
}
