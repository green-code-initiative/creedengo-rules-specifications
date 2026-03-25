package org.greencodeinitiative.mavenbuild.ruleexporter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class MainTest {

    @Test
    void shouldThrowIfThereIsNoSourceDir() {
        // Given
        Main main = new Main(new String[]{});

        // When
        Throwable thrown = catchThrowable(main::run);

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("argument 1 is required: sourceDir");
    }

    @Test
    void shouldThrowIfThereIsNoTargetDir() {
        // Given
        Main main = new Main(new String[]{"someSourceDir"});

        // When
        Throwable thrown = catchThrowable(main::run);

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("argument 2 is required: targetDir");
    }

    @Test
    void shouldRunPrepareResourcesWithIndexJsonFile() {
        // Given
        String sourceDir = "maven-build/test/resources";
        String targetDir = "target/test-prepare-resources-with-index";
        String indexJsonFile = "target/test-index.json";
        Main main = new Main(
                new String[]{sourceDir, targetDir, indexJsonFile}
        );

        // When
        main.run();

        // Then - no exception thrown means success
    }

    @Test
    void shouldRunPrepareResourcesWithoutIndexJsonFile() {
        // Given
        String sourceDir = "maven-build/test/resources";
        String targetDir = "target/test-prepare-resources-no-index";
        Main main = new Main(new String[]{sourceDir, targetDir});

        // When
        main.run();

        // Then - no exception thrown means success
    }
}
